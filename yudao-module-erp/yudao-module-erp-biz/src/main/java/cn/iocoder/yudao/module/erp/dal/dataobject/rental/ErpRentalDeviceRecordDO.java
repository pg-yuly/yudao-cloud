package cn.iocoder.yudao.module.erp.dal.dataobject.rental;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpWarehouseDO;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 租赁设备明细 DO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@TableName("erp_rental_device_record")
@KeySequence("erp_rental_device_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpRentalDeviceRecordDO extends BaseDO {

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
     * 出入库数量
     * <p>
     * 正数，表示入库；负数，表示出库
     */
    private BigDecimal count;
    /**
     * 总库存量
     * <p>
     * 出入库之后，目前的库存量
     */
    private BigDecimal totalCount;
    /**
     * 业务类型
     * <p>
     * 枚举 {@link ErpStockRecordBizTypeEnum}
     */
    private Integer bizType;
    /**
     * 业务编号
     * <p>
     * 例如说：{@link ErpStockInDO#getId()}
     */
    private Long bizId;
    /**
     * 业务项编号
     * <p>
     * 例如说：{@link ErpStockInItemDO#getId()}
     */
    private Long bizItemId;
    /**
     * 业务单号
     * <p>
     * 例如说：{@link ErpStockInDO#getNo()}
     */
    private String bizNo;

}