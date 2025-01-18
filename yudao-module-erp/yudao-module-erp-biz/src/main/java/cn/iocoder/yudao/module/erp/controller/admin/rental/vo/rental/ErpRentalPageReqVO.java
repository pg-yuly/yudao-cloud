package cn.iocoder.yudao.module.erp.controller.admin.rental.vo.rental;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - ERP 租赁库存分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpRentalPageReqVO extends PageParam {

    @Schema(description = "产品编号", example = "19614")
    private Long productId;

    @Schema(description = "客户编号", example = "2802")
    private Long customerId;

}