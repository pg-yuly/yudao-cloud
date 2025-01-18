package cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理后台 - ERP 合同 Response VO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/17
 **/
@Schema(description = "管理后台 - ERP 合同 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpContractRespVO {

    @Schema(description = "合同编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10430")
    @ExcelProperty("合同编号")
    private Long id;

    @Schema(description = "合同名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("合同名称")
    private String name;

    @Schema(description = "合同编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20230101")
    @ExcelProperty("合同编号")
    private String no;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18336")
    @ExcelProperty("客户编号")
    private Long customerId;
    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "18336")
    @ExcelProperty("客户名称")
    private String customerName;

    @Schema(description = "开始时间")
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "合同金额", example = "5617")
    @ExcelProperty("合同金额")
    private BigDecimal totalPrice;

    @Schema(description = "公司签约人", example = "14036")
    private Long signUserId;
    @Schema(description = "公司签约人", example = "小明")
    @ExcelProperty("公司签约人")
    private String signUserName;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人", example = "25682")
    @ExcelProperty("创建人")
    private String creator;

    @Schema(description = "创建人名字", example = "test")
    @ExcelProperty("创建人名字")
    private String creatorName;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;
}
