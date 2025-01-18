package cn.iocoder.yudao.module.erp.service.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMovePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMoveSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveItemDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * ERP 租赁调拨单 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
public interface ErpRentalMoveService {

    /**
     * 创建租赁调拨单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRentalMove(@Valid ErpRentalMoveSaveReqVO createReqVO);

    /**
     * 更新租赁调拨单
     *
     * @param updateReqVO 更新信息
     */
    void updateRentalMove(@Valid ErpRentalMoveSaveReqVO updateReqVO);

    /**
     * 更新租赁调拨单的状态
     *
     * @param id 编号
     * @param status 状态
     */
    void updateRentalMoveStatus(Long id, Integer status);

    /**
     * 删除租赁调拨单
     *
     * @param ids 编号数组
     */
    void deleteRentalMove(List<Long> ids);

    /**
     * 获得租赁调拨单
     *
     * @param id 编号
     * @return 库存调拨单
     */
    ErpRentalMoveDO getRentalMove(Long id);

    /**
     * 获得租赁调拨单分页
     *
     * @param pageReqVO 分页查询
     * @return 库存调拨单分页
     */
    PageResult<ErpRentalMoveDO> getRentalMovePage(ErpRentalMovePageReqVO pageReqVO);

    // ==================== 调拨项 ====================

    /**
     * 获得租赁调拨单项列表
     *
     * @param moveId 调拨编号
     * @return 库存调拨单项列表
     */
    List<ErpRentalMoveItemDO> getRentalMoveItemListByMoveId(Long moveId);

    /**
     * 获得租赁调拨单项 List
     *
     * @param moveIds 调拨编号数组
     * @return 库存调拨单项 List
     */
    List<ErpRentalMoveItemDO> getRentalMoveItemListByMoveIds(Collection<Long> moveIds);

}