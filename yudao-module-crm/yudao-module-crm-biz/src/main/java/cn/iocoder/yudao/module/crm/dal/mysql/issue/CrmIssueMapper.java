package cn.iocoder.yudao.module.crm.dal.mysql.issue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.crm.controller.admin.issue.vo.CrmIssuePageReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.issue.CrmIssueDO;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.util.CrmPermissionUtils;
import org.apache.ibatis.annotations.Mapper;


/**
 * 问题 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Mapper
public interface CrmIssueMapper extends BaseMapperX<CrmIssueDO> {

    default PageResult<CrmIssueDO> selectPage(CrmIssuePageReqVO pageReqVO, Long ownerUserId) {
        MPJLambdaWrapperX<CrmIssueDO> query = new MPJLambdaWrapperX<>();

        // 拼接自身的查询条件
        query.selectAll(CrmIssueDO.class)
                .likeIfPresent(CrmIssueDO::getName, pageReqVO.getName())
                .eqIfPresent(CrmIssueDO::getCustomerId, pageReqVO.getCustomerId());

        return selectJoinPage(pageReqVO, CrmIssueDO.class, query);
    }
}
