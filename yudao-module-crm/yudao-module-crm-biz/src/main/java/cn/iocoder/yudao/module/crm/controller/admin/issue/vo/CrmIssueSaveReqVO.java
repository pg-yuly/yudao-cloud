package cn.iocoder.yudao.module.crm.controller.admin.issue.vo;

import cn.iocoder.yudao.module.crm.framework.operatelog.core.CrmCustomerParseFunction;
import cn.iocoder.yudao.module.crm.framework.operatelog.core.SysAdminUserParseFunction;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - CRM 问题新增/修改 Request VO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Schema(description = "管理后台 - CRM 问题新增/修改 Request VO")
@Data
public class CrmIssueSaveReqVO {

    @Schema(description = "编号", example = "25787")
    private Long id;

    @Schema(description = "问题名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "问题001")
    @DiffLogField(name = "问题名称")
    @NotEmpty(message = "问题名称不能为空")
    private String name;

    @Schema(description = "客户编号", example = "2")
    @DiffLogField(name = "客户", function = CrmCustomerParseFunction.NAME)
    private Long customerId;

    @Schema(description = "负责人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @DiffLogField(name = "负责人", function = SysAdminUserParseFunction.NAME)
    @NotNull(message = "负责人编号不能为空")
    private Long ownerUserId;

    @Schema(description = "提出时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-02-02")
    @NotNull(message = "提出时间不能为空")
    @DiffLogField(name = "提出时间")
    private LocalDateTime proposalTime;

    @Schema(description = "问题描述", example = "问题描述")
    @DiffLogField(name = "问题描述")
    private String description;
}
