package cn.iocoder.yudao.module.erp.service.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.record.ErpRentalRecordPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalRecordDO;
import cn.iocoder.yudao.module.erp.dal.mysql.rental.ErpRentalRecordMapper;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpRentalRecordCreateReqBO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * ERP 租赁库存明细 Service 实现类
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/19
 **/
@Service
@Validated
public class ErpRentalRecordServiceImpl implements ErpRentalRecordService {

    @Resource
    private ErpRentalRecordMapper rentalRecordMapper;

    @Resource
    private ErpRentalService rentalService;

    @Override
    public ErpRentalRecordDO getRentalRecord(Long id) {
        return rentalRecordMapper.selectById(id);
    }

    @Override
    public PageResult<ErpRentalRecordDO> getRentalRecordPage(ErpRentalRecordPageReqVO pageReqVO) {
        return rentalRecordMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRentalRecord(ErpRentalRecordCreateReqBO createReqBO) {
        // 1. 更新库存
        BigDecimal totalCount = rentalService.updateStockCountIncrement(
                createReqBO.getProductId(), createReqBO.getCustomerId(), createReqBO.getCount());
        // 2. 创建库存明细
        ErpRentalRecordDO rentalRecord = BeanUtils.toBean(createReqBO, ErpRentalRecordDO.class)
                .setTotalCount(totalCount);
        rentalRecordMapper.insert(rentalRecord);
    }

}