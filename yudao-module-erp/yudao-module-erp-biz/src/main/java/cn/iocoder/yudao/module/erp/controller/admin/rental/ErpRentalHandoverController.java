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
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.rental.ErpRentalHandoverService;
import cn.iocoder.yudao.module.erp.service.rental.ErpRentalService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMultiMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - ERP 租赁交接单")
@RestController
@RequestMapping("/erp/rental-handover")
@Validated
public class ErpRentalHandoverController {

    @Resource
    private ErpRentalHandoverService rentalHandoverService;
    @Resource
    private ErpStockService stockService;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpCustomerService customerService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建租赁交接单")
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:create')")
    public CommonResult<Long> createRentalHandover(@Valid @RequestBody ErpRentalHandoverSaveReqVO createReqVO) {
        return success(rentalHandoverService.createRentalHandover(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租赁交接单")
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:update')")
    public CommonResult<Boolean> updateRentalHandover(@Valid @RequestBody ErpRentalHandoverSaveReqVO updateReqVO) {
        rentalHandoverService.updateRentalHandover(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新租赁交接单的状态")
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:update-status')")
    public CommonResult<Boolean> updateRentalHandoverStatus(@RequestParam("id") Long id,
                                                     @RequestParam("status") Integer status) {
        rentalHandoverService.updateRentalHandoverStatus(id, status);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租赁交接单")
    @Parameter(name = "ids", description = "编号数组", required = true)
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:delete')")
    public CommonResult<Boolean> deleteRentalHandover(@RequestParam("ids") List<Long> ids) {
        rentalHandoverService.deleteRentalHandover(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租赁交接单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:query')")
    public CommonResult<ErpRentalHandoverRespVO> getRentalHandover(@RequestParam("id") Long id) {
        ErpRentalHandoverDO rentalHandover = rentalHandoverService.getRentalHandover(id);
        if (rentalHandover == null) {
            return success(null);
        }
        List<ErpRentalHandoverItemDO> rentalHandoverItemList = rentalHandoverService.getRentalHandoverItemListByOutId(id);
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(rentalHandoverItemList, ErpRentalHandoverItemDO::getProductId));
        return success(BeanUtils.toBean(rentalHandover, ErpRentalHandoverRespVO.class, rentalHandoverVO ->
                rentalHandoverVO.setItems(BeanUtils.toBean(rentalHandoverItemList, ErpRentalHandoverRespVO.Item.class, item -> {
                    ErpStockDO stock = stockService.getStock(item.getProductId(), item.getWarehouseId());
                    item.setStockCount(stock != null ? stock.getCount() : BigDecimal.ZERO);
                    MapUtils.findAndThen(productMap, item.getProductId(), product -> item.setProductName(product.getName())
                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()));
                }))));
    }

    @GetMapping("/page")
    @Operation(summary = "获得租赁交接单分页")
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:query')")
    public CommonResult<PageResult<ErpRentalHandoverRespVO>> getRentalHandoverPage(@Valid ErpRentalHandoverPageReqVO pageReqVO) {
        PageResult<ErpRentalHandoverDO> pageResult = rentalHandoverService.getRentalHandoverPage(pageReqVO);
        return success(buildRentalHandoverVOPageResult(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租赁交接单 Excel")
    @PreAuthorize("@ss.hasPermission('erp:rental-handover:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRentalHandoverExcel(@Valid ErpRentalHandoverPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpRentalHandoverRespVO> list = buildRentalHandoverVOPageResult(rentalHandoverService.getRentalHandoverPage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "租赁交接单.xls", "数据", ErpRentalHandoverRespVO.class, list);
    }

    private PageResult<ErpRentalHandoverRespVO> buildRentalHandoverVOPageResult(PageResult<ErpRentalHandoverDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 1.1 出库项
        List<ErpRentalHandoverItemDO> rentalHandoverItemList = rentalHandoverService.getRentalHandoverItemListByOutIds(
                convertSet(pageResult.getList(), ErpRentalHandoverDO::getId));
        Map<Long, List<ErpRentalHandoverItemDO>> rentalHandoverItemMap = convertMultiMap(rentalHandoverItemList, ErpRentalHandoverItemDO::getHandoverId);
        // 1.2 产品信息
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(rentalHandoverItemList, ErpRentalHandoverItemDO::getProductId));
        // 1.3 客户信息
        Map<Long, ErpCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(pageResult.getList(), ErpRentalHandoverDO::getCustomerId));
        // 1.4 管理员信息
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(pageResult.getList(), rentalHandover -> Long.parseLong(rentalHandover.getCreator())));
        // 2. 开始拼接
        return BeanUtils.toBean(pageResult, ErpRentalHandoverRespVO.class, rentalHandover -> {
            rentalHandover.setItems(BeanUtils.toBean(rentalHandoverItemMap.get(rentalHandover.getId()), ErpRentalHandoverRespVO.Item.class,
                    item -> MapUtils.findAndThen(productMap, item.getProductId(), product -> item.setProductName(product.getName())
                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()))));
            rentalHandover.setProductNames(CollUtil.join(rentalHandover.getItems(), "，", ErpRentalHandoverRespVO.Item::getProductName));
            MapUtils.findAndThen(customerMap, rentalHandover.getCustomerId(), supplier -> rentalHandover.setCustomerName(supplier.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(rentalHandover.getCreator()), user -> rentalHandover.setCreatorName(user.getNickname()));
        });
    }

}