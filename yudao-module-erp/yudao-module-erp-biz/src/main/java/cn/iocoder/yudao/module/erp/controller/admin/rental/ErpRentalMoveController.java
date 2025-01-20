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
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMovePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMoveRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMoveSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveItemDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.rental.ErpRentalMoveService;
import cn.iocoder.yudao.module.erp.service.rental.ErpRentalService;
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

@Tag(name = "管理后台 - ERP 租赁调拨单")
@RestController
@RequestMapping("/erp/rental-move")
@Validated
public class ErpRentalMoveController {

    @Resource
    private ErpRentalMoveService rentalMoveService;
    @Resource
    private ErpRentalService rentalService;
    @Resource
    private ErpProductService productService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建租赁调拨单")
    @PreAuthorize("@ss.hasPermission('erp:rental-move:create')")
    public CommonResult<Long> createRentalMove(@Valid @RequestBody ErpRentalMoveSaveReqVO createReqVO) {
        return success(rentalMoveService.createRentalMove(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租赁调拨单")
    @PreAuthorize("@ss.hasPermission('erp:rental-move:update')")
    public CommonResult<Boolean> updateRentalMove(@Valid @RequestBody ErpRentalMoveSaveReqVO updateReqVO) {
        rentalMoveService.updateRentalMove(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新租赁调拨单的状态")
    @PreAuthorize("@ss.hasPermission('erp:rental-move:update-status')")
    public CommonResult<Boolean> updateRentalMoveStatus(@RequestParam("id") Long id,
                                                        @RequestParam("status") Integer status) {
        rentalMoveService.updateRentalMoveStatus(id, status);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租赁调拨单")
    @Parameter(name = "ids", description = "编号数组", required = true)
    @PreAuthorize("@ss.hasPermission('erp:rental-move:delete')")
    public CommonResult<Boolean> deleteRentalMove(@RequestParam("ids") List<Long> ids) {
        rentalMoveService.deleteRentalMove(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租赁调拨单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:rental-move:query')")
    public CommonResult<ErpRentalMoveRespVO> getRentalMove(@RequestParam("id") Long id) {
        ErpRentalMoveDO rentalMove = rentalMoveService.getRentalMove(id);
        if (rentalMove == null) {
            return success(null);
        }
        List<ErpRentalMoveItemDO> rentalMoveItemList = rentalMoveService.getRentalMoveItemListByMoveId(id);
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(rentalMoveItemList, ErpRentalMoveItemDO::getProductId));
        return success(BeanUtils.toBean(rentalMove, ErpRentalMoveRespVO.class, rentalMoveVO ->
                rentalMoveVO.setItems(BeanUtils.toBean(rentalMoveItemList, ErpRentalMoveRespVO.Item.class, item -> {
                    ErpRentalDO rental = rentalService.getStock(item.getProductId(), item.getFromCustomerId());
                    item.setStockCount(rental != null ? rental.getCount() : BigDecimal.ZERO);
                    MapUtils.findAndThen(productMap, item.getProductId(), product -> item.setProductName(product.getName())
                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()));
                }))));
    }

    @GetMapping("/page")
    @Operation(summary = "获得租赁调拨单分页")
    @PreAuthorize("@ss.hasPermission('erp:rental-move:query')")
    public CommonResult<PageResult<ErpRentalMoveRespVO>> getRentalMovePage(@Valid ErpRentalMovePageReqVO pageReqVO) {
        PageResult<ErpRentalMoveDO> pageResult = rentalMoveService.getRentalMovePage(pageReqVO);
        return success(buildRentalMoveVOPageResult(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租赁调拨单 Excel")
    @PreAuthorize("@ss.hasPermission('erp:rental-move:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRentalMoveExcel(@Valid ErpRentalMovePageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpRentalMoveRespVO> list = buildRentalMoveVOPageResult(rentalMoveService.getRentalMovePage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "设备调拨单.xls", "数据", ErpRentalMoveRespVO.class, list);
    }

    private PageResult<ErpRentalMoveRespVO> buildRentalMoveVOPageResult(PageResult<ErpRentalMoveDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 1.1 调拨项
        List<ErpRentalMoveItemDO> rentalMoveItemList = rentalMoveService.getRentalMoveItemListByMoveIds(
                convertSet(pageResult.getList(), ErpRentalMoveDO::getId));
        Map<Long, List<ErpRentalMoveItemDO>> rentalMoveItemMap = convertMultiMap(rentalMoveItemList, ErpRentalMoveItemDO::getMoveId);
        // 1.2 产品信息
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(rentalMoveItemList, ErpRentalMoveItemDO::getProductId));
        // 1.3 TODO 芋艿：搞仓库信息
        // 1.4 管理员信息
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(pageResult.getList(), rentalMove -> Long.parseLong(rentalMove.getCreator())));
        // 2. 开始拼接
        return BeanUtils.toBean(pageResult, ErpRentalMoveRespVO.class, rentalMove -> {
            rentalMove.setItems(BeanUtils.toBean(rentalMoveItemMap.get(rentalMove.getId()), ErpRentalMoveRespVO.Item.class,
                    item -> MapUtils.findAndThen(productMap, item.getProductId(), product -> item.setProductName(product.getName())
                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()))));
            rentalMove.setProductNames(CollUtil.join(rentalMove.getItems(), "，", ErpRentalMoveRespVO.Item::getProductName));
            // TODO 芋艿：
//            MapUtils.findAndThen(customerMap, rentalMove.getCustomerId(), supplier -> rentalMove.setCustomerName(supplier.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(rentalMove.getCreator()), user -> rentalMove.setCreatorName(user.getNickname()));
        });
    }

}