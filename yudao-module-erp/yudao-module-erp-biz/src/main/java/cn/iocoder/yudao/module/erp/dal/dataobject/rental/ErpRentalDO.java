package cn.iocoder.yudao.module.erp.dal.dataobject.rental;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 租赁库存 DO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@TableName("erp_rental")
@KeySequence("erp_rental_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpRentalDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 产品编号
     * <p>
     * 关联 {@link ErpProductDO#getId()}
     */
    private Long productId;
    /**
     * 客户编号
     * <p>
     * 关联 {@link ErpCustomerDO#getId()}
     */
    private Long customerId;
    /**
     * 库存数量
     */
    private BigDecimal count;

}