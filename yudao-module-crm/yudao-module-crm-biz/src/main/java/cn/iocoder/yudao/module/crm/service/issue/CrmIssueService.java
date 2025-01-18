package cn.iocoder.yudao.module.crm.service.issue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssuePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssueSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.issue.CrmIssueDO;

/**
 * 问题 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
public interface CrmIssueService {

    /**
     * 创建问题
     *
     * @param createReqVO 创建信息
     * @param userId      用户编号
     * @return 编号
     */
    Long createIssue(CrmIssueSaveReqVO createReqVO, Long userId);

    /**
     * 更新问题
     *
     * @param updateReqVO 更新信息
     */
    void updateIssue(CrmIssueSaveReqVO updateReqVO);

    /**
     * 更新问题的状态
     *
     * @param id          编号
     * @param issueStatus 问题状态
     */
    void updateIssueStatus(Long id, Boolean issueStatus);

    /**
     * 删除问题
     *
     * @param id 编号
     */
    void deleteIssue(Long id);

    /**
     * 获得问题
     *
     * @param id 编号
     * @return 问题
     */
    CrmIssueDO getIssue(Long id);

    /**
     * 获得问题分页
     *
     * @param pageReqVO 分页查询
     * @param userId    用户编号
     * @return 问题分页
     */
    PageResult<CrmIssueDO> getIssuePage(CrmIssuePageReqVO pageReqVO, Long userId);
}
