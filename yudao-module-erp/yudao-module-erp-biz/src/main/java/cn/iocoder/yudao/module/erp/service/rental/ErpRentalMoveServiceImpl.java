package cn.iocoder.yudao.module.erp.service.rental;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMovePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMoveSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.rental.ErpRentalMoveItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.rental.ErpRentalMoveMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
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
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.RENTAL_MOVE_PROCESS_FAIL;

/**
 * ERP 租赁调拨单 Service 实现类
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Service
@Validated
public class ErpRentalMoveServiceImpl implements ErpRentalMoveService {

    @Resource
    private ErpRentalMoveMapper rentalMoveMapper;
    @Resource
    private ErpRentalMoveItemMapper rentalMoveItemMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpCustomerService customerService;
    @Resource
    private ErpStockRecordService stockRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRentalMove(ErpRentalMoveSaveReqVO createReqVO) {
        // 1.1 校验出库项的有效性
        List<ErpRentalMoveItemDO> rentalMoveItems = validateRentalMoveItems(createReqVO.getItems());
        // 1.2 生成调拨单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.RENTAL_MOVE_NO_PREFIX);
        if (rentalMoveMapper.selectByNo(no) != null) {
            throw exception(RENTAL_MOVE_NO_EXISTS);
        }

        // 2.1 插入出库单
        ErpRentalMoveDO rentalMove = BeanUtils.toBean(createReqVO, ErpRentalMoveDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus())
                .setTotalCount(getSumValue(rentalMoveItems, ErpRentalMoveItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(rentalMoveItems, ErpRentalMoveItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO)));
        rentalMoveMapper.insert(rentalMove);
        // 2.2 插入出库单项
        rentalMoveItems.forEach(o -> o.setMoveId(rentalMove.getId()));
        rentalMoveItemMapper.insertBatch(rentalMoveItems);
        return rentalMove.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRentalMove(ErpRentalMoveSaveReqVO updateReqVO) {
        // 1.1 校验存在
        ErpRentalMoveDO rentalMove = validateRentalMoveExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(rentalMove.getStatus())) {
            throw exception(RENTAL_MOVE_UPDATE_FAIL_APPROVE, rentalMove.getNo());
        }
        // 1.2 校验出库项的有效性
        List<ErpRentalMoveItemDO> rentalMoveItems = validateRentalMoveItems(updateReqVO.getItems());

        // 2.1 更新出库单
        ErpRentalMoveDO updateObj = BeanUtils.toBean(updateReqVO, ErpRentalMoveDO.class, in -> in
                .setTotalCount(getSumValue(rentalMoveItems, ErpRentalMoveItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(rentalMoveItems, ErpRentalMoveItemDO::getTotalPrice, BigDecimal::add)));
        rentalMoveMapper.updateById(updateObj);
        // 2.2 更新出库单项
        updateRentalMoveItemList(updateReqVO.getId(), rentalMoveItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRentalMoveStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpRentalMoveDO rentalMove = validateRentalMoveExists(id);
        // 1.2 校验状态
        if (rentalMove.getStatus().equals(status)) {
            throw exception(approve ? RENTAL_MOVE_APPROVE_FAIL : RENTAL_MOVE_PROCESS_FAIL);
        }

        // 2. 更新状态
        int updateCount = rentalMoveMapper.updateByIdAndStatus(id, rentalMove.getStatus(),
                new ErpRentalMoveDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? RENTAL_MOVE_APPROVE_FAIL : RENTAL_MOVE_PROCESS_FAIL);
        }

        // 3. 变更库存
        List<ErpRentalMoveItemDO> rentalMoveItems = rentalMoveItemMapper.selectListByMoveId(id);
        Integer fromBizType = approve ? ErpStockRecordBizTypeEnum.MOVE_OUT.getType()
                : ErpStockRecordBizTypeEnum.MOVE_OUT_CANCEL.getType();
        Integer toBizType = approve ? ErpStockRecordBizTypeEnum.MOVE_IN.getType()
                : ErpStockRecordBizTypeEnum.MOVE_IN_CANCEL.getType();
        rentalMoveItems.forEach(rentalMoveItem -> {
            BigDecimal fromCount = approve ? rentalMoveItem.getCount().negate() : rentalMoveItem.getCount();
            BigDecimal toCount = approve ? rentalMoveItem.getCount() : rentalMoveItem.getCount().negate();
            stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                    rentalMoveItem.getProductId(), rentalMoveItem.getFromCustomerId(), fromCount,
                    fromBizType, rentalMoveItem.getMoveId(), rentalMoveItem.getId(), rentalMove.getNo()));
            stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                    rentalMoveItem.getProductId(), rentalMoveItem.getToCustomerId(), toCount,
                    toBizType, rentalMoveItem.getMoveId(), rentalMoveItem.getId(), rentalMove.getNo()));
        });
    }

    private List<ErpRentalMoveItemDO> validateRentalMoveItems(List<ErpRentalMoveSaveReqVO.Item> list) {
        // 1.1 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpRentalMoveSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 1.2 校验客户存在
        customerService.validCustomerList(convertSetByFlatMap(list,
                item -> Stream.of(item.getFromCustomerId(), item.getToCustomerId())));
        // 2. 转化为 ErpRentalMoveItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpRentalMoveItemDO.class, item -> item
                .setProductUnitId(productMap.get(item.getProductId()).getUnitId())
                .setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()))));
    }

    private void updateRentalMoveItemList(Long id, List<ErpRentalMoveItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpRentalMoveItemDO> oldList = rentalMoveItemMapper.selectListByMoveId(id);
        List<List<ErpRentalMoveItemDO>> diffList = diffList(oldList, newList,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setMoveId(id));
            rentalMoveItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            rentalMoveItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            rentalMoveItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpRentalMoveItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRentalMove(List<Long> ids) {
        // 1. 校验不处于已审批
        List<ErpRentalMoveDO> rentalMoves = rentalMoveMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(rentalMoves)) {
            return;
        }
        rentalMoves.forEach(rentalMove -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(rentalMove.getStatus())) {
                throw exception(RENTAL_MOVE_DELETE_FAIL_APPROVE, rentalMove.getNo());
            }
        });

        // 2. 遍历删除，并记录操作日志
        rentalMoves.forEach(rentalMove -> {
            // 2.1 删除出库单
            rentalMoveMapper.deleteById(rentalMove.getId());
            // 2.2 删除出库单项
            rentalMoveItemMapper.deleteByMoveId(rentalMove.getId());
        });
    }

    private ErpRentalMoveDO validateRentalMoveExists(Long id) {
        ErpRentalMoveDO rentalMove = rentalMoveMapper.selectById(id);
        if (rentalMove == null) {
            throw exception(RENTAL_MOVE_NOT_EXISTS);
        }
        return rentalMove;
    }

    @Override
    public ErpRentalMoveDO getRentalMove(Long id) {
        return rentalMoveMapper.selectById(id);
    }

    @Override
    public PageResult<ErpRentalMoveDO> getRentalMovePage(ErpRentalMovePageReqVO pageReqVO) {
        return rentalMoveMapper.selectPage(pageReqVO);
    }

    // ==================== 出库项 ====================

    @Override
    public List<ErpRentalMoveItemDO> getRentalMoveItemListByMoveId(Long moveId) {
        return rentalMoveItemMapper.selectListByMoveId(moveId);
    }

    @Override
    public List<ErpRentalMoveItemDO> getRentalMoveItemListByMoveIds(Collection<Long> moveIds) {
        if (CollUtil.isEmpty(moveIds)) {
            return Collections.emptyList();
        }
        return rentalMoveItemMapper.selectListByMoveIds(moveIds);
    }

}