package cn.iocoder.yudao.module.erp.dal.dataobject.operation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 问题 DO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@TableName("erp_issue")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpIssueDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 问题名称
     */
    private String name;

    /**
     * 客户编号
     * <p>
     * 关联 {@link ErpCustomerDO#getId()}
     */
    private Long customerId;

    /**
     * 负责人编号
     * <p>
     * 关联 {@link AdminUserRespDTO#getId()}
     */
    private Long ownerUserId;

    /**
     * 提出时间
     */
    private LocalDateTime proposalTime;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 问题状态
     */
    private Boolean issueStatus;
}
