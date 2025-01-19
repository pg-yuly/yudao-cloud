package cn.iocoder.yudao.module.erp.service.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.record.ErpRentalRecordPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalRecordDO;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpRentalRecordCreateReqBO;
import jakarta.validation.Valid;

/**
 * ERP 租赁库存明细 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/19
 **/
public interface ErpRentalRecordService {

    /**
     * 获得租赁库存明细
     *
     * @param id 编号
     * @return 租赁库存明细
     */
    ErpRentalRecordDO getRentalRecord(Long id);

    /**
     * 获得租赁库存明细分页
     *
     * @param pageReqVO 分页查询
     * @return 租赁库存明细分页
     */
    PageResult<ErpRentalRecordDO> getRentalRecordPage(ErpRentalRecordPageReqVO pageReqVO);

    /**
     * 创建库存明细
     *
     * @param createReqBO 创建库存明细 BO
     */
    void createRentalRecord(@Valid ErpRentalRecordCreateReqBO createReqBO);

}