package cn.iocoder.yudao.module.erp.service.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverItemDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * ERP 租赁交接单 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
public interface ErpRentalHandoverService {

    /**
     * 创建租赁交接单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRentalHandover(@Valid ErpRentalHandoverSaveReqVO createReqVO);

    /**
     * 更新租赁交接单
     *
     * @param updateReqVO 更新信息
     */
    void updateRentalHandover(@Valid ErpRentalHandoverSaveReqVO updateReqVO);

    /**
     * 更新租赁交接单的状态
     *
     * @param id 编号
     * @param status 状态
     */
    void updateRentalHandoverStatus(Long id, Integer status);

    /**
     * 删除租赁交接单
     *
     * @param ids 编号数组
     */
    void deleteRentalHandover(List<Long> ids);

    /**
     * 获得租赁交接单
     *
     * @param id 编号
     * @return 租赁交接单
     */
    ErpRentalHandoverDO getRentalHandover(Long id);

    /**
     * 获得租赁交接单分页
     *
     * @param pageReqVO 分页查询
     * @return 租赁交接单分页
     */
    PageResult<ErpRentalHandoverDO> getRentalHandoverPage(ErpRentalHandoverPageReqVO pageReqVO);

    // ==================== 出库项 ====================

    /**
     * 获得租赁交接单项列表
     *
     * @param outId 出库编号
     * @return 租赁交接单项列表
     */
    List<ErpRentalHandoverItemDO> getRentalHandoverItemListByOutId(Long outId);

    /**
     * 获得租赁交接单项 List
     *
     * @param outIds 出库编号数组
     * @return 租赁交接单项 List
     */
    List<ErpRentalHandoverItemDO> getRentalHandoverItemListByOutIds(Collection<Long> outIds);

}