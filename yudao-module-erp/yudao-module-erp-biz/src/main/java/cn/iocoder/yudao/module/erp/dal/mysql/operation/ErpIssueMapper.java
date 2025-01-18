package cn.iocoder.yudao.module.erp.dal.mysql.operation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.operation.vo.ErpIssuePageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.operation.ErpIssueDO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 问题 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/14
 **/
@Mapper
public interface ErpIssueMapper extends BaseMapperX<ErpIssueDO> {

    default PageResult<ErpIssueDO> selectPage(ErpIssuePageReqVO pageReqVO) {
        MPJLambdaWrapperX<ErpIssueDO> query = new MPJLambdaWrapperX<>();

        // 拼接自身的查询条件
        query.selectAll(ErpIssueDO.class)
                .likeIfPresent(ErpIssueDO::getName, pageReqVO.getName())
                .eqIfPresent(ErpIssueDO::getCustomerId, pageReqVO.getCustomerId());

        return selectJoinPage(pageReqVO, ErpIssueDO.class, query);
    }
}
