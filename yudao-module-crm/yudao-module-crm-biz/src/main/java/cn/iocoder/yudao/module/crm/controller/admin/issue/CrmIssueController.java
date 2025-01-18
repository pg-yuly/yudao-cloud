package cn.iocoder.yudao.module.crm.controller.admin.issue;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssuePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssueRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssueSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.issue.CrmIssueDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.receivable.CrmReceivableDO;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.issue.CrmIssueService;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
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
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.framework.common.util.collection.MapUtils.findAndThen;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static java.util.Collections.singletonList;

/**
 * 管理后台 - CRM 问题
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Tag(name = "管理后台 - CRM 问题")
@RestController
@RequestMapping("/crm/issue")
@Validated
public class CrmIssueController {

    @Resource
    private CrmIssueService issueService;
    @Resource
    private DeptApi deptApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private CrmCustomerService customerService;

    @PostMapping("/create")
    @Operation(summary = "创建问题")
    @PreAuthorize("@ss.hasPermission('crm:issue:create')")
    public CommonResult<Long> createIssue(@Valid @RequestBody CrmIssueSaveReqVO createReqVO) {
        return success(issueService.createIssue(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新问题")
    @PreAuthorize("@ss.hasPermission('crm:issue:update')")
    public CommonResult<Boolean> updateIssue(@Valid @RequestBody CrmIssueSaveReqVO updateReqVO) {
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
    @PreAuthorize("@ss.hasPermission('crm:issue:delete')")
    public CommonResult<Boolean> deleteIssue(@RequestParam("id") Long id) {
        issueService.deleteIssue(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得问题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:issue:query')")
    public CommonResult<CrmIssueRespVO> getIssue(@RequestParam("id") Long id) {
        // 1. 获取客户
        CrmIssueDO issue = issueService.getIssue(id);
        // 2. 拼接数据
        return success(buildCustomerDetail(issue));
    }

    public CrmIssueRespVO buildCustomerDetail(CrmIssueDO issue) {
        if (issue == null) {
            return null;
        }
        return buildIssueDetailList(singletonList(issue)).get(0);
    }

    @GetMapping("/page")
    @Operation(summary = "获得问题分页")
    @PreAuthorize("@ss.hasPermission('crm:issue:query')")
    public CommonResult<PageResult<CrmIssueRespVO>> getIssuePage(@Valid CrmIssuePageReqVO pageVO) {
        // 1. 查询问题分页
        PageResult<CrmIssueDO> pageResult = issueService.getIssuePage(pageVO, getLoginUserId());
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        // 2. 拼接数据
        return success(new PageResult<>(buildIssueDetailList(pageResult.getList()), pageResult.getTotal()));
    }

    public List<CrmIssueRespVO> buildIssueDetailList(List<CrmIssueDO> issueList) {
        if (CollUtil.isEmpty(issueList)) {
            return java.util.Collections.emptyList();
        }
        // 1.1 获取客户列表
        Map<Long, CrmCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(issueList, CrmIssueDO::getCustomerId));
        // 1.2 获取创建人、负责人列表
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertSetByFlatMap(issueList,
                contact -> Stream.of(NumberUtils.parseLong(contact.getCreator()), contact.getOwnerUserId())));
        Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(convertSet(userMap.values(), AdminUserRespDTO::getDeptId));

        // 2. 转换成 VO
        return BeanUtils.toBean(issueList, CrmIssueRespVO.class, issueVO -> {
            // 2.1 拼接客户名称
            findAndThen(customerMap, issueVO.getCustomerId(), customer -> issueVO.setCustomerName(customer.getName()));
            // 2.2 设置创建人、负责人名称
            MapUtils.findAndThen(userMap, NumberUtils.parseLong(issueVO.getCreator()),
                    user -> issueVO.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, issueVO.getOwnerUserId(), user -> {
                issueVO.setOwnerUserName(user.getNickname());
                MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> issueVO.setOwnerUserDeptName(dept.getName()));
            });
        });
    }
}
