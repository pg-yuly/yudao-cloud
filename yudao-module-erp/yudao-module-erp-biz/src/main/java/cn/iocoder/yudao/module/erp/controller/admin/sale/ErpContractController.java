package cn.iocoder.yudao.module.erp.controller.admin.sale;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpContractDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.service.sale.ErpContractService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;
import static java.util.Collections.singletonList;

/**
 * 管理后台 - ERP 合同
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/17
 **/
@Tag(name = "管理后台 - ERP 合同")
@RestController
@RequestMapping("/erp/contract")
@Validated
public class ErpContractController {

    @Resource
    private ErpContractService contractService;
    @Resource
    private ErpCustomerService customerService;

    @PostMapping("/create")
    @Operation(summary = "创建合同")
    @PreAuthorize("@ss.hasPermission('erp:contract:create')")
    public CommonResult<Long> createContract(@Valid @RequestBody ErpContractSaveReqVO createReqVO) {
        return success(contractService.createContract(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新合同")
    @PreAuthorize("@ss.hasPermission('erp:contract:update')")
    public CommonResult<Boolean> updateContract(@Valid @RequestBody ErpContractSaveReqVO updateReqVO) {
        contractService.updateContract(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除合同")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:contract:delete')")
    public CommonResult<Boolean> deleteContract(@RequestParam("id") Long id) {
        contractService.deleteContract(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得合同")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:contract:query')")
    public CommonResult<ErpContractRespVO> getContract(@RequestParam("id") Long id) {
        ErpContractDO contract = contractService.getContract(id);
        return success(buildContractDetail(contract));
    }

    private ErpContractRespVO buildContractDetail(ErpContractDO contract) {
        if (contract == null) {
            return null;
        }
        ErpContractRespVO contractVO = buildContractDetailList(singletonList(contract)).get(0);
        return contractVO;
    }

    @GetMapping("/page")
    @Operation(summary = "获得合同分页")
    @PreAuthorize("@ss.hasPermission('erp:contract:query')")
    public CommonResult<PageResult<ErpContractRespVO>> getContractPage(@Valid ErpContractPageReqVO pageVO) {
        PageResult<ErpContractDO> pageResult = contractService.getContractPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ErpContractRespVO.class).setList(buildContractDetailList(pageResult.getList())));
    }

    private List<ErpContractRespVO> buildContractDetailList(List<ErpContractDO> contractList) {
        if (CollUtil.isEmpty(contractList)) {
            return Collections.emptyList();
        }
        // 1.1 获取客户列表
        Map<Long, ErpCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(contractList, ErpContractDO::getCustomerId));

        // 2. 拼接数据
        return BeanUtils.toBean(contractList, ErpContractRespVO.class, contractVO -> {
            // 2.1 设置客户信息
            findAndThen(customerMap, contractVO.getCustomerId(), customer -> contractVO.setCustomerName(customer.getName()));
        });
    }
}
