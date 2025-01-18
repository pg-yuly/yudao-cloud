package cn.iocoder.yudao.module.crm.controller.admin.issue.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - CRM 问题分页 Request VO
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Schema(description = "管理后台 - CRM 问题分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrmIssuePageReqVO extends PageParam {

    @Schema(description = "问题名称", example = "问题001")
    private String name;

    @Schema(description = "客户编号", example = "4963")
    private Long customerId;
}
