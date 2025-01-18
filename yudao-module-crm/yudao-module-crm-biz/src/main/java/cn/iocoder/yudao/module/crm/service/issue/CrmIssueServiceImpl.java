package cn.iocoder.yudao.module.crm.service.issue;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssuePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssueSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.issue.CrmIssueDO;
import cn.iocoder.yudao.module.crm.dal.mysql.issue.CrmIssueMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.enums.permission.CrmPermissionLevelEnum;
import cn.iocoder.yudao.module.crm.framework.permission.core.annotations.CrmPermission;
import cn.iocoder.yudao.module.crm.service.permission.CrmPermissionService;
import cn.iocoder.yudao.module.crm.service.permission.bo.CrmPermissionCreateReqBO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.crm.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.crm.enums.LogRecordConstants.*;

/**
 * 问题 Service 实现类
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Service
@Slf4j
@Validated
public class CrmIssueServiceImpl implements CrmIssueService {

    @Resource
    private CrmIssueMapper issueMapper;

    @Resource
    private CrmPermissionService permissionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ISSUE_TYPE, subType = CRM_ISSUE_CREATE_SUB_TYPE, bizNo = "{{#issue.id}}",
            success = CRM_ISSUE_CREATE_SUCCESS)
    public Long createIssue(CrmIssueSaveReqVO createReqVO, Long userId) {
        createReqVO.setId(null);

        // 1. 插入问题
        CrmIssueDO issue = initIssue(createReqVO, userId);
        issueMapper.insert(issue);

        // 2. 创建数据权限
        permissionService.createPermission(new CrmPermissionCreateReqBO().setBizType(CrmBizTypeEnum.CRM_ISSUE.getType())
                .setBizId(issue.getId()).setUserId(userId).setLevel(CrmPermissionLevelEnum.OWNER.getLevel()));

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("issue", issue);
        return issue.getId();
    }

    /**
     * 初始化问题的通用字段
     *
     * @param issue       问题信息
     * @param ownerUserId 负责人编号
     * @return 问题信息 DO
     */
    private static CrmIssueDO initIssue(Object issue, Long ownerUserId) {
        return BeanUtils.toBean(issue, CrmIssueDO.class).setOwnerUserId(ownerUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ISSUE_TYPE, subType = CRM_ISSUE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = CRM_ISSUE_UPDATE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ISSUE, bizId = "#updateReqVO.id", level = CrmPermissionLevelEnum.WRITE)
    public void updateIssue(CrmIssueSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "问题编号不能为空");
        // 更新的时候，要把 updateReqVO 负责人设置为空，避免修改
        updateReqVO.setOwnerUserId(null);
        // 1. 校验存在
        CrmIssueDO oldIssue = validateIssueExists(updateReqVO.getId());

        // 2. 更新问题
        CrmIssueDO updateObj = BeanUtils.toBean(updateReqVO, CrmIssueDO.class);
        issueMapper.updateById(updateObj);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldIssue, CrmIssueSaveReqVO.class));
        LogRecordContext.putVariable("issueName", oldIssue.getName());
    }

    @Override
    @LogRecord(type = CRM_ISSUE_TYPE, subType = CRM_ISSUE_UPDATE_ISSUE_STATUS_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_ISSUE_UPDATE_ISSUE_STATUS_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ISSUE, bizId = "#id", level = CrmPermissionLevelEnum.WRITE)
    public void updateIssueStatus(Long id, Boolean issueStatus) {
        // 1.1 校验存在
        CrmIssueDO issue = validateIssueExists(id);
        // 1.2 校验是否重复操作
        if (Objects.equals(issue.getIssueStatus(), issueStatus)) {
            throw exception(ISSUE_UPDATE_ISSUE_STATUS_FAIL);
        }

        // 2. 更新问题状态
        issueMapper.updateById(new CrmIssueDO().setId(id).setIssueStatus(issueStatus));

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("issueName", issue.getName());
        LogRecordContext.putVariable("issueStatus", issueStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ISSUE_TYPE, subType = CRM_ISSUE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_ISSUE_DELETE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ISSUE, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void deleteIssue(Long id) {
        // 1 校验存在
        CrmIssueDO issue = validateIssueExists(id);

        // 2. 删除问题
        issueMapper.deleteById(id);

        // 3. 删除数据权限
        permissionService.deletePermission(CrmBizTypeEnum.CRM_ISSUE.getType(), id);

        // 4. 记录操作日志上下文
        LogRecordContext.putVariable("issueName", issue.getName());
    }

    //======================= 查询相关 =======================

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ISSUE, bizId = "#id", level = CrmPermissionLevelEnum.READ)
    public CrmIssueDO getIssue(Long id) {
        return issueMapper.selectById(id);
    }

    @Override
    public PageResult<CrmIssueDO> getIssuePage(CrmIssuePageReqVO pageReqVO, Long userId) {
        return issueMapper.selectPage(pageReqVO, userId);
    }

    private CrmIssueDO validateIssueExists(Long id) {
        CrmIssueDO issueDO = issueMapper.selectById(id);
        if (issueDO == null) {
            throw exception(ISSUE_NOT_EXISTS);
        }
        return issueDO;
    }
}
