package cn.iocoder.yudao.module.erp.dal.mysql.sale;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpContractDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * ERP 合同 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/17
 **/
@Mapper
public interface ErpContractMapper extends BaseMapperX<ErpContractDO> {

    default ErpContractDO selectByNo(String no) {
        return selectOne(ErpContractDO::getNo, no);
    }

    default PageResult<ErpContractDO> selectPage(ErpContractPageReqVO pageReqVO) {
        MPJLambdaWrapperX<ErpContractDO> query = new MPJLambdaWrapperX<>();

        // 拼接自身的查询条件
        query.selectAll(ErpContractDO.class)
                .likeIfPresent(ErpContractDO::getNo, pageReqVO.getNo())
                .likeIfPresent(ErpContractDO::getName, pageReqVO.getName())
                .eqIfPresent(ErpContractDO::getCustomerId, pageReqVO.getCustomerId())
                .orderByDesc(ErpContractDO::getId);

        return selectJoinPage(pageReqVO, ErpContractDO.class, query);
    }
}
