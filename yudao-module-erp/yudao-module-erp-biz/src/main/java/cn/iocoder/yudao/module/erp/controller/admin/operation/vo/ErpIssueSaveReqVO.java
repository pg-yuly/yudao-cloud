package cn.iocoder.yudao.module.erp.controller.admin.operation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - ERP 问题新增/修改 Request VO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Schema(description = "管理后台 - ERP 问题新增/修改 Request VO")
@Data
public class ErpIssueSaveReqVO {

    @Schema(description = "编号", example = "25787")
    private Long id;

    @Schema(description = "问题名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "问题001")
    @NotEmpty(message = "问题名称不能为空")
    private String name;

    @Schema(description = "客户编号", example = "2")
    private Long customerId;

    @Schema(description = "负责人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "负责人编号不能为空")
    private Long ownerUserId;

    @Schema(description = "提出时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-02-02")
    @NotNull(message = "提出时间不能为空")
    private LocalDateTime proposalTime;

    @Schema(description = "问题描述", example = "问题描述")
    private String description;
}
