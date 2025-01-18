package cn.iocoder.yudao.module.erp.service.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.rental.ErpRentalPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalDO;

import java.math.BigDecimal;

/**
 * ERP 租赁库存 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
public interface ErpRentalService {

    /**
     * 获得产品库存
     *
     * @param id 编号
     * @return 库存
     */
    ErpRentalDO getStock(Long id);

    /**
     * 基于产品 + 客户，获得产品库存
     *
     * @param productId 产品编号
     * @param customerId 客户编号
     * @return 产品库存
     */
    ErpRentalDO getStock(Long productId, Long customerId);

    /**
     * 获得产品库存数量
     * <p>
     * 如果不存在库存记录，则返回 0
     *
     * @param productId 产品编号
     * @return 产品库存数量
     */
    BigDecimal getStockCount(Long productId);

    /**
     * 获得产品库存分页
     *
     * @param pageReqVO 分页查询
     * @return 库存分页
     */
    PageResult<ErpRentalDO> getStockPage(ErpRentalPageReqVO pageReqVO);

    /**
     * 增量更新产品库存数量
     *
     * @param productId 产品编号
     * @param customerId 客户编号
     * @param count 增量数量：正数，表示增加；负数，表示减少
     * @return 更新后的库存
     */
    BigDecimal updateStockCountIncrement(Long productId, Long customerId, BigDecimal count);

}