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
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.rental.ErpRentalPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.rental.ErpRentalRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.rental.ErpRentalService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - ERP 租赁库存")
@RestController
@RequestMapping("/erp/rental")
@Validated
public class ErpRentalController {

    @Resource
    private ErpRentalService rentalService;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpCustomerService customerService;

    @GetMapping("/get")
    @Operation(summary = "获得产品库存")
    @Parameters({
            @Parameter(name = "id", description = "编号", example = "1"), // 方案一：传递 id
            @Parameter(name = "productId", description = "产品编号", example = "10"), // 方案二：传递 productId + customerId
            @Parameter(name = "customerId", description = "客户编号", example = "2")
    })
    @PreAuthorize("@ss.hasPermission('erp:rental:query')")
    public CommonResult<ErpRentalRespVO> getStock(@RequestParam(value = "id", required = false) Long id,
                                                  @RequestParam(value = "productId", required = false) Long productId,
                                                  @RequestParam(value = "customerId", required = false) Long customerId) {
        ErpRentalDO stock = id != null ? rentalService.getStock(id) : rentalService.getStock(productId, customerId);
        return success(BeanUtils.toBean(stock, ErpRentalRespVO.class));
    }

    @GetMapping("/get-count")
    @Operation(summary = "获得产品库存数量")
    @Parameter(name = "productId", description = "产品编号", example = "10")
    public CommonResult<BigDecimal> getStockCount(@RequestParam("productId") Long productId) {
        return success(rentalService.getStockCount(productId));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品库存分页")
    @PreAuthorize("@ss.hasPermission('erp:rental:query')")
    public CommonResult<PageResult<ErpRentalRespVO>> getStockPage(@Valid ErpRentalPageReqVO pageReqVO) {
        PageResult<ErpRentalDO> pageResult = rentalService.getStockPage(pageReqVO);
        return success(buildRentalVOPageResult(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品库存 Excel")
    @PreAuthorize("@ss.hasPermission('erp:rental:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStockExcel(@Valid ErpRentalPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpRentalRespVO> list = buildRentalVOPageResult(rentalService.getStockPage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "设备库存.xls", "数据", ErpRentalRespVO.class, list);
    }

    private PageResult<ErpRentalRespVO> buildRentalVOPageResult(PageResult<ErpRentalDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(pageResult.getList(), ErpRentalDO::getProductId));
        Map<Long, ErpCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(pageResult.getList(), ErpRentalDO::getCustomerId));
        return BeanUtils.toBean(pageResult, ErpRentalRespVO.class, stock -> {
            MapUtils.findAndThen(productMap, stock.getProductId(), product -> stock.setProductName(product.getName())
                    .setCategoryName(product.getCategoryName()).setUnitName(product.getUnitName()));
            MapUtils.findAndThen(customerMap, stock.getCustomerId(), customer -> stock.setCustomerName(customer.getName()));
        });
    }

}