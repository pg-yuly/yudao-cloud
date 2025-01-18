package cn.iocoder.yudao.module.erp.service.operation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssuePageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssueSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.operation.ErpIssueDO;

/**
 * 问题 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
public interface ErpIssueService {

    /**
     * 创建问题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createIssue(ErpIssueSaveReqVO createReqVO, Long userId);

    /**
     * 更新问题
     *
     * @param updateReqVO 更新信息
     */
    void updateIssue(ErpIssueSaveReqVO updateReqVO);

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
    ErpIssueDO getIssue(Long id);

    /**
     * 获得问题分页
     *
     * @param pageReqVO 分页查询
     * @return 问题分页
     */
    PageResult<ErpIssueDO> getIssuePage(ErpIssuePageReqVO pageReqVO);
}
