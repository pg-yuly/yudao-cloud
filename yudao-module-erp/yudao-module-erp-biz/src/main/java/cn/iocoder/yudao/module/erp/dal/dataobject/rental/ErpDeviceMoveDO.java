package cn.iocoder.yudao.module.erp.dal.dataobject.rental;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 设备调拨单 DO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@TableName("erp_device_move")
@KeySequence("erp_device_move_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpDeviceMoveDO extends BaseDO {

    /**
     * 调拨编号
     */
    @TableId
    private Long id;
    /**
     * 调拨单号
     */
    private String no;
    /**
     * 调拨时间
     */
    private LocalDateTime moveTime;
    /**
     * 合计数量
     */
    private BigDecimal totalCount;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.iocoder.yudao.module.erp.enums.ErpAuditStatus}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 附件 URL
     */
    private String fileUrl;

}