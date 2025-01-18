package cn.iocoder.yudao.module.erp.controller.admin.operation.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.infra.enums.DictTypeConstants;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - ERP 问题 Response VO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Schema(description = "管理后台 - ERP 问题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpIssueRespVO {

    @Schema(description = "编号", example = "25787")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "问题名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "问题001")
    @ExcelProperty("问题名称")
    private String name;

    @Schema(description = "问题状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "13563")
    @ExcelProperty(value = "问题状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BOOLEAN_STRING)
    private Boolean issueStatus;

    @Schema(description = "提出时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-02-02")
    @ExcelProperty("提出时间")
    private LocalDateTime proposalTime;

    @Schema(description = "问题描述", example = "吃饭、睡觉、打逗逗")
    @ExcelProperty("问题描述")
    private String description;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long customerId;
    @Schema(description = "客户名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
    @ExcelProperty("客户名字")
    private String customerName;

    @Schema(description = "负责人的用户编号", example = "25682")
    private Long ownerUserId;
    @Schema(description = "负责人名字", example = "25682")
    @ExcelProperty("负责人名字")
    private String ownerUserName;
    @Schema(description = "负责人部门")
    @ExcelProperty("负责人部门")
    private String ownerUserDeptName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人", example = "25682")
    private String creator;
    @Schema(description = "创建人名字", example = "test")
    @ExcelProperty("创建人名字")
    private String creatorName;
}
