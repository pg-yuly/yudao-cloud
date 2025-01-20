package cn.iocoder.yudao.module.erp.service.rental;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.rental.ErpRentalHandoverItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.rental.ErpRentalHandoverMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.rental.ErpRentalRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.ErpWarehouseService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpRentalRecordCreateReqBO;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 租赁交接单 Service 实现类
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Service
@Validated
public class ErpRentalHandoverServiceImpl implements ErpRentalHandoverService {

    @Resource
    private ErpRentalHandoverMapper rentalHandoverMapper;
    @Resource
    private ErpRentalHandoverItemMapper rentalHandoverItemMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;
    @Resource
    private ErpCustomerService customerService;
    @Resource
    private ErpStockRecordService stockRecordService;
    @Resource
    private ErpRentalRecordService rentalRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRentalHandover(ErpRentalHandoverSaveReqVO createReqVO) {
        // 1.1 校验出库项的有效性
        List<ErpRentalHandoverItemDO> rentalHandoverItems = validateRentalHandoverItems(createReqVO.getItems());
        // 1.2 校验客户
        customerService.validateCustomer(createReqVO.getCustomerId());
        // 1.3 生成交接单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.RENTAL_HANDOVER_NO_PREFIX);
        if (rentalHandoverMapper.selectByNo(no) != null) {
            throw exception(RENTAL_HANDOVER_NO_EXISTS);
        }

        // 2.1 插入出库单
        ErpRentalHandoverDO rentalHandover = BeanUtils.toBean(createReqVO, ErpRentalHandoverDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus())
                .setTotalCount(getSumValue(rentalHandoverItems, ErpRentalHandoverItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(rentalHandoverItems, ErpRentalHandoverItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO)));
        rentalHandoverMapper.insert(rentalHandover);
        // 2.2 插入出库单项
        rentalHandoverItems.forEach(o -> {
            o.setHandoverId(rentalHandover.getId());
            o.setCustomerId(rentalHandover.getCustomerId());
        });
        rentalHandoverItemMapper.insertBatch(rentalHandoverItems);
        return rentalHandover.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRentalHandover(ErpRentalHandoverSaveReqVO updateReqVO) {
        // 1.1 校验存在
        ErpRentalHandoverDO rentalHandover = validateRentalHandoverExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(rentalHandover.getStatus())) {
            throw exception(RENTAL_HANDOVER_UPDATE_FAIL_APPROVE, rentalHandover.getNo());
        }
        // 1.2 校验客户
        customerService.validateCustomer(updateReqVO.getCustomerId());
        // 1.3 校验出库项的有效性
        List<ErpRentalHandoverItemDO> rentalHandoverItems = validateRentalHandoverItems(updateReqVO.getItems());

        // 2.1 更新出库单
        ErpRentalHandoverDO updateObj = BeanUtils.toBean(updateReqVO, ErpRentalHandoverDO.class, in -> in
                .setTotalCount(getSumValue(rentalHandoverItems, ErpRentalHandoverItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(rentalHandoverItems, ErpRentalHandoverItemDO::getTotalPrice, BigDecimal::add)));
        rentalHandoverMapper.updateById(updateObj);
        // 2.2 更新出库单项
        updateRentalHandoverItemList(updateReqVO.getId(), rentalHandoverItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRentalHandoverStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpRentalHandoverDO rentalHandover = validateRentalHandoverExists(id);
        // 1.2 校验状态
        if (rentalHandover.getStatus().equals(status)) {
            throw exception(approve ? RENTAL_HANDOVER_APPROVE_FAIL : RENTAL_HANDOVER_PROCESS_FAIL);
        }

        // 2. 更新状态
        int updateCount = rentalHandoverMapper.updateByIdAndStatus(id, rentalHandover.getStatus(),
                new ErpRentalHandoverDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? RENTAL_HANDOVER_APPROVE_FAIL : RENTAL_HANDOVER_PROCESS_FAIL);
        }

        // 3. 变更库存
        List<ErpRentalHandoverItemDO> rentalHandoverItems = rentalHandoverItemMapper.selectListByOutId(id);
        Integer fromBizType = approve ? ErpStockRecordBizTypeEnum.HANDOVER_OUT.getType()
                : ErpStockRecordBizTypeEnum.HANDOVER_OUT_CANCEL.getType();
        Integer toBizType = approve ? ErpRentalRecordBizTypeEnum.HANDOVER_IN.getType()
                : ErpRentalRecordBizTypeEnum.HANDOVER_IN_CANCEL.getType();
        rentalHandoverItems.forEach(rentalHandoverItem -> {
            BigDecimal fromCount = approve ? rentalHandoverItem.getCount().negate() : rentalHandoverItem.getCount();
            BigDecimal toCount = approve ? rentalHandoverItem.getCount() : rentalHandoverItem.getCount().negate();
            stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                    rentalHandoverItem.getProductId(), rentalHandoverItem.getWarehouseId(), fromCount,
                    fromBizType, rentalHandoverItem.getHandoverId(), rentalHandoverItem.getId(), rentalHandover.getNo()));
            rentalRecordService.createRentalRecord(new ErpRentalRecordCreateReqBO(
                    rentalHandoverItem.getProductId(), rentalHandoverItem.getCustomerId(), toCount,
                    toBizType, rentalHandoverItem.getHandoverId(), rentalHandoverItem.getId(), rentalHandover.getNo()));
        });
    }

    private List<ErpRentalHandoverItemDO> validateRentalHandoverItems(List<ErpRentalHandoverSaveReqVO.Item> list) {
        // 1.1 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpRentalHandoverSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 1.2 校验仓库存在
        warehouseService.validWarehouseList(convertSet(list, ErpRentalHandoverSaveReqVO.Item::getWarehouseId));
        // 2. 转化为 ErpRentalHandoverItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpRentalHandoverItemDO.class, item -> item
                .setProductUnitId(productMap.get(item.getProductId()).getUnitId())
                .setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()))));
    }

    private void updateRentalHandoverItemList(Long id, List<ErpRentalHandoverItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpRentalHandoverItemDO> oldList = rentalHandoverItemMapper.selectListByOutId(id);
        List<List<ErpRentalHandoverItemDO>> diffList = diffList(oldList, newList,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setHandoverId(id));
            rentalHandoverItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            rentalHandoverItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            rentalHandoverItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpRentalHandoverItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRentalHandover(List<Long> ids) {
        // 1. 校验不处于已审批
        List<ErpRentalHandoverDO> rentalHandovers = rentalHandoverMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(rentalHandovers)) {
            return;
        }
        rentalHandovers.forEach(rentalHandover -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(rentalHandover.getStatus())) {
                throw exception(RENTAL_HANDOVER_DELETE_FAIL_APPROVE, rentalHandover.getNo());
            }
        });

        // 2. 遍历删除，并记录操作日志
        rentalHandovers.forEach(rentalHandover -> {
            // 2.1 删除出库单
            rentalHandoverMapper.deleteById(rentalHandover.getId());
            // 2.2 删除出库单项
            rentalHandoverItemMapper.deleteByOutId(rentalHandover.getId());
        });
    }

    private ErpRentalHandoverDO validateRentalHandoverExists(Long id) {
        ErpRentalHandoverDO rentalHandover = rentalHandoverMapper.selectById(id);
        if (rentalHandover == null) {
            throw exception(RENTAL_HANDOVER_NOT_EXISTS);
        }
        return rentalHandover;
    }

    @Override
    public ErpRentalHandoverDO getRentalHandover(Long id) {
        return rentalHandoverMapper.selectById(id);
    }

    @Override
    public PageResult<ErpRentalHandoverDO> getRentalHandoverPage(ErpRentalHandoverPageReqVO pageReqVO) {
        return rentalHandoverMapper.selectPage(pageReqVO);
    }

    // ==================== 出库项 ====================

    @Override
    public List<ErpRentalHandoverItemDO> getRentalHandoverItemListByOutId(Long outId) {
        return rentalHandoverItemMapper.selectListByOutId(outId);
    }

    @Override
    public List<ErpRentalHandoverItemDO> getRentalHandoverItemListByOutIds(Collection<Long> outIds) {
        if (CollUtil.isEmpty(outIds)) {
            return Collections.emptyList();
        }
        return rentalHandoverItemMapper.selectListByOutIds(outIds);
    }

}