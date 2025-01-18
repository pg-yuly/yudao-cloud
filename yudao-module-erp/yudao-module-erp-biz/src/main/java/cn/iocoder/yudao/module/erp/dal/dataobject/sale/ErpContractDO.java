package cn.iocoder.yudao.module.erp.dal.dataobject.sale;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ERP 合同 DO
 *
 * @author dhb52
 */
@TableName("erp_contract")
@KeySequence("erp_contract_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpContractDO extends BaseDO {

    /**
     * 合同编号
     */
    @TableId
    private Long id;
    /**
     * 合同名称
     */
    private String name;
    /**
     * 合同编号
     */
    private String no;
    /**
     * 客户编号
     * <p>
     * 关联 {@link ErpCustomerDO#getId()}
     */
    private Long customerId;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 合同总金额，单位：分
     */
    private BigDecimal totalPrice;
    /**
     * 公司签约人，非必须
     * <p>
     * 关联 AdminUserDO 的 id 字段
     */
    private Long signUserId;
    /**
     * 备注
     */
    private String remark;
}
