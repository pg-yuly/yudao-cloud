package cn.iocoder.yudao.module.erp.controller.admin.rental;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.record.ErpRentalRecordPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.record.ErpRentalRecordRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalRecordDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.rental.ErpRentalRecordService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - ERP 租赁库存明细")
@RestController
@RequestMapping("/erp/rental-record")
@Validated
public class ErpRentalRecordController {

    @Resource
    private ErpRentalRecordService rentalRecordService;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpCustomerService customerService;

    @Resource
    private AdminUserApi adminUserApi;

    @GetMapping("/get")
    @Operation(summary = "获得租赁库存明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:rental-record:query')")
    public CommonResult<ErpRentalRecordRespVO> getRentalRecord(@RequestParam("id") Long id) {
        ErpRentalRecordDO rentalRecord = rentalRecordService.getRentalRecord(id);
        return success(BeanUtils.toBean(rentalRecord, ErpRentalRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得租赁库存明细分页")
    @PreAuthorize("@ss.hasPermission('erp:rental-record:query')")
    public CommonResult<PageResult<ErpRentalRecordRespVO>> getRentalRecordPage(@Valid ErpRentalRecordPageReqVO pageReqVO) {
        PageResult<ErpRentalRecordDO> pageResult = rentalRecordService.getRentalRecordPage(pageReqVO);
        return success(buildStockRecrodVOPageResult(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租赁库存明细 Excel")
    @PreAuthorize("@ss.hasPermission('erp:rental-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRentalRecordExcel(@Valid ErpRentalRecordPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpRentalRecordRespVO> list = buildStockRecrodVOPageResult(rentalRecordService.getRentalRecordPage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "设备库存明细.xls", "数据", ErpRentalRecordRespVO.class, list);
    }

    private PageResult<ErpRentalRecordRespVO> buildStockRecrodVOPageResult(PageResult<ErpRentalRecordDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(pageResult.getList(), ErpRentalRecordDO::getProductId));
        Map<Long, ErpCustomerDO> warehouseMap = customerService.getCustomerMap(
                convertSet(pageResult.getList(), ErpRentalRecordDO::getCustomerId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(pageResult.getList(), record -> Long.parseLong(record.getCreator())));
        return BeanUtils.toBean(pageResult, ErpRentalRecordRespVO.class, rental -> {
            MapUtils.findAndThen(productMap, rental.getProductId(), product -> rental.setProductName(product.getName())
                    .setCategoryName(product.getCategoryName()).setUnitName(product.getUnitName()));
            MapUtils.findAndThen(warehouseMap, rental.getCustomerId(), customer -> rental.setCustomerName(customer.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(rental.getCreator()), user -> rental.setCreatorName(user.getNickname()));
        });
    }

}