package cn.iocoder.yudao.module.erp.dal.dataobject.rental;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpWarehouseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 租赁交接单项 DO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@TableName("erp_rental_handover_item")
@KeySequence("erp_rental_handover_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpRentalHandoverItemDO extends BaseDO {

    /**
     * 交接项编号
     */
    @TableId
    private Long id;
    /**
     * 交接编号
     * <p>
     * 关联 {@link ErpRentalHandoverDO#getId()}
     */
    private Long handoverId;
    /**
     * 仓库编号
     * <p>
     * 关联 {@link ErpWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 客户编号
     * <p>
     * 关联 {@link ErpCustomerDO#getId()}
     */
    private Long customerId;
    /**
     * 产品编号
     * <p>
     * 关联 {@link ErpProductDO#getId()}
     */
    private Long productId;
    /**
     * 产品单位编号
     * <p>
     * 冗余 {@link ErpProductDO#getUnitId()}
     */
    private Long productUnitId;
    /**
     * 产品单价
     */
    private BigDecimal productPrice;
    /**
     * 产品数量
     */
    private BigDecimal count;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 备注
     */
    private String remark;

}