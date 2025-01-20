package cn.iocoder.yudao.module.erp.controller.admin.operation;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssuePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssueRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssueSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.operation.ErpIssueDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpCustomerDO;
import cn.iocoder.yudao.module.erp.service.operation.ErpIssueService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSetByFlatMap;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static java.util.Collections.singletonList;

/**
 * 管理后台 - Erp 问题
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Tag(name = "管理后台 - Erp 问题")
@RestController
@RequestMapping("/erp/issue")
@Validated
public class ErpIssueController {

    @Resource
    private ErpIssueService issueService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ErpCustomerService customerService;

    @PostMapping("/create")
    @Operation(summary = "创建问题")
    @PreAuthorize("@ss.hasPermission('erp:issue:create')")
    public CommonResult<Long> createIssue(@Valid @RequestBody ErpIssueSaveReqVO createReqVO) {
        return success(issueService.createIssue(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新问题")
    @PreAuthorize("@ss.hasPermission('erp:issue:update')")
    public CommonResult<Boolean> updateIssue(@Valid @RequestBody ErpIssueSaveReqVO updateReqVO) {
        issueService.updateIssue(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-issue-status")
    @Operation(summary = "更新问题的状态")
    @Parameters({
            @Parameter(name = "id", description = "问题编号", required = true),
            @Parameter(name = "issueStatus", description = "问题状态", required = true)
    })
    public CommonResult<Boolean> updateIssueStatus(@RequestParam("id") Long id,
                                                   @RequestParam("issueStatus") Boolean issueStatus) {
        issueService.updateIssueStatus(id, issueStatus);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除问题")
    @Parameter(name = "id", description = "问题编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:issue:delete')")
    public CommonResult<Boolean> deleteIssue(@RequestParam("id") Long id) {
        issueService.deleteIssue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得问题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:issue:query')")
    public CommonResult<ErpIssueRespVO> getIssue(@RequestParam("id") Long id) {
        // 1. 获取客户
        ErpIssueDO issue = issueService.getIssue(id);
        // 2. 拼接数据
        return success(buildCustomerDetail(issue));
    }

    public ErpIssueRespVO buildCustomerDetail(ErpIssueDO issue) {
        if (issue == null) {
            return null;
        }
        return buildIssueDetailList(singletonList(issue)).get(0);
    }

    @GetMapping("/page")
    @Operation(summary = "获得问题分页")
    @PreAuthorize("@ss.hasPermission('erp:issue:query')")
    public CommonResult<PageResult<ErpIssueRespVO>> getIssuePage(@Valid ErpIssuePageReqVO pageVO) {
        // 1. 查询问题分页
        PageResult<ErpIssueDO> pageResult = issueService.getIssuePage(pageVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        // 2. 拼接数据
        return success(new PageResult<>(buildIssueDetailList(pageResult.getList()), pageResult.getTotal()));
    }

    public List<ErpIssueRespVO> buildIssueDetailList(List<ErpIssueDO> issueList) {
        if (CollUtil.isEmpty(issueList)) {
            return java.util.Collections.emptyList();
        }
        // 1.1 获取客户列表
        Map<Long, ErpCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(issueList, ErpIssueDO::getCustomerId));
        // 1.2 获取创建人、负责人列表
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertSetByFlatMap(issueList,
                contact -> Stream.of(NumberUtils.parseLong(contact.getCreator()), contact.getOwnerUserId())));

        // 2. 转换成 VO
        return BeanUtils.toBean(issueList, ErpIssueRespVO.class, issueVO -> {
            // 2.1 拼接客户名称
            findAndThen(customerMap, issueVO.getCustomerId(), customer -> issueVO.setCustomerName(customer.getName()));
            // 2.2 设置创建人、负责人名称
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(issueVO.getCreator()),
                    user -> issueVO.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, issueVO.getOwnerUserId(), user -> issueVO.setOwnerUserName(user.getNickname()));
        });
    }
}
