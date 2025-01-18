package cn.iocoder.yudao.module.erp.service.operation;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssuePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssueSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.operation.ErpIssueDO;
import cn.iocoder.yudao.module.erp.dal.mysql.operation.ErpIssueMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.ISSUE_UPDATE_ISSUE_STATUS_FAIL;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.ISSUE_NOT_EXISTS;

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
public class ErpIssueServiceImpl implements ErpIssueService {

    @Resource
    private ErpIssueMapper issueMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIssue(ErpIssueSaveReqVO createReqVO, Long userId) {
        createReqVO.setId(null);

        // 1. 插入问题
        ErpIssueDO issue = initIssue(createReqVO, userId);
        issueMapper.insert(issue);

        return issue.getId();
    }

    /**
     * 初始化问题的通用字段
     *
     * @param issue       问题信息
     * @param ownerUserId 负责人编号
     * @return 问题信息 DO
     */
    private static ErpIssueDO initIssue(Object issue, Long ownerUserId) {
        return BeanUtils.toBean(issue, ErpIssueDO.class).setOwnerUserId(ownerUserId);
    }

    @Override
    public void updateIssue(ErpIssueSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "问题编号不能为空");
        // 更新的时候，要把 updateReqVO 负责人设置为空，避免修改
        updateReqVO.setOwnerUserId(null);
        // 1. 校验存在
        validateIssueExists(updateReqVO.getId());

        // 2. 更新问题
        ErpIssueDO updateObj = BeanUtils.toBean(updateReqVO, ErpIssueDO.class);
        issueMapper.updateById(updateObj);
    }

    @Override
    public void updateIssueStatus(Long id, Boolean issueStatus) {
        // 1.1 校验存在
        ErpIssueDO issue = validateIssueExists(id);
        // 1.2 校验是否重复操作
        if (Objects.equals(issue.getIssueStatus(), issueStatus)) {
            throw exception(ISSUE_UPDATE_ISSUE_STATUS_FAIL);
        }

        // 2. 更新问题状态
        issueMapper.updateById(new ErpIssueDO().setId(id).setIssueStatus(issueStatus));
    }

    @Override
    public void deleteIssue(Long id) {
        // 1 校验存在
        validateIssueExists(id);

        // 2. 删除问题
        issueMapper.deleteById(id);
    }

    //======================= 查询相关 =======================

    @Override
    public ErpIssueDO getIssue(Long id) {
        return issueMapper.selectById(id);
    }

    @Override
    public PageResult<ErpIssueDO> getIssuePage(ErpIssuePageReqVO pageReqVO) {
        return issueMapper.selectPage(pageReqVO);
    }

    private ErpIssueDO validateIssueExists(Long id) {
        ErpIssueDO issueDO = issueMapper.selectById(id);
        if (issueDO == null) {
            throw exception(ISSUE_NOT_EXISTS);
        }
        return issueDO;
    }
}
