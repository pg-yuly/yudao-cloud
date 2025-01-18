package cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - ERP 合同分页 Request VO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/17
 **/
@Schema(description = "管理后台 - ERP 合同分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpContractPageReqVO extends PageParam {

    @Schema(description = "合同编号", example = "XYZ008")
    private String no;

    @Schema(description = "合同名称", example = "王五")
    private String name;

    @Schema(description = "客户编号", example = "18336")
    private Long customerId;
}
