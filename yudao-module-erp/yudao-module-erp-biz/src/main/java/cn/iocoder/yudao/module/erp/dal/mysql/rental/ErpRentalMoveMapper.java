package cn.iocoder.yudao.module.erp.dal.mysql.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.move.ErpRentalMovePageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveItemDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 租赁调拨单 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Mapper
public interface ErpRentalMoveMapper extends BaseMapperX<ErpRentalMoveDO> {

    default PageResult<ErpRentalMoveDO> selectPage(ErpRentalMovePageReqVO reqVO) {
        MPJLambdaWrapperX<ErpRentalMoveDO> query = new MPJLambdaWrapperX<ErpRentalMoveDO>()
                .likeIfPresent(ErpRentalMoveDO::getNo, reqVO.getNo())
                .betweenIfPresent(ErpRentalMoveDO::getMoveTime, reqVO.getMoveTime())
                .eqIfPresent(ErpRentalMoveDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ErpRentalMoveDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ErpRentalMoveDO::getCreator, reqVO.getCreator())
                .orderByDesc(ErpRentalMoveDO::getId);
        if (reqVO.getFromCustomerId() != null || reqVO.getProductId() != null) {
            query.leftJoin(ErpRentalMoveItemDO.class, ErpRentalMoveItemDO::getMoveId, ErpRentalMoveDO::getId)
                    .eq(reqVO.getFromCustomerId() != null, ErpRentalMoveItemDO::getFromCustomerId, reqVO.getFromCustomerId())
                    .eq(reqVO.getProductId() != null, ErpRentalMoveItemDO::getProductId, reqVO.getProductId())
                    .groupBy(ErpRentalMoveDO::getId);
        }
        return selectJoinPage(reqVO, ErpRentalMoveDO.class, query);
    }

    default int updateByIdAndStatus(Long id, Integer status, ErpRentalMoveDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpRentalMoveDO>()
                .eq(ErpRentalMoveDO::getId, id).eq(ErpRentalMoveDO::getStatus, status));
    }

    default ErpRentalMoveDO selectByNo(String no) {
        return selectOne(ErpRentalMoveDO::getNo, no);
    }

}