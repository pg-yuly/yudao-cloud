package cn.iocoder.yudao.module.erp.dal.mysql.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.handover.ErpRentalHandoverPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverItemDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 租赁交接单 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Mapper
public interface ErpRentalHandoverMapper extends BaseMapperX<ErpRentalHandoverDO> {

    default PageResult<ErpRentalHandoverDO> selectPage(ErpRentalHandoverPageReqVO reqVO) {
        MPJLambdaWrapperX<ErpRentalHandoverDO> query = new MPJLambdaWrapperX<ErpRentalHandoverDO>()
                .likeIfPresent(ErpRentalHandoverDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpRentalHandoverDO::getCustomerId, reqVO.getCustomerId())
                .betweenIfPresent(ErpRentalHandoverDO::getHandoverTime, reqVO.getHandoverTime())
                .eqIfPresent(ErpRentalHandoverDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ErpRentalHandoverDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ErpRentalHandoverDO::getCreator, reqVO.getCreator())
                .orderByDesc(ErpRentalHandoverDO::getId);
        if (reqVO.getWarehouseId() != null || reqVO.getProductId() != null) {
            query.leftJoin(ErpRentalHandoverItemDO.class, ErpRentalHandoverItemDO::getHandoverId, ErpRentalHandoverDO::getId)
                    .eq(reqVO.getWarehouseId() != null, ErpRentalHandoverItemDO::getWarehouseId, reqVO.getWarehouseId())
                    .eq(reqVO.getProductId() != null, ErpRentalHandoverItemDO::getProductId, reqVO.getProductId())
                    .groupBy(ErpRentalHandoverDO::getId);
        }
        return selectJoinPage(reqVO, ErpRentalHandoverDO.class, query);
    }

    default int updateByIdAndStatus(Long id, Integer status, ErpRentalHandoverDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpRentalHandoverDO>()
                .eq(ErpRentalHandoverDO::getId, id).eq(ErpRentalHandoverDO::getStatus, status));
    }

    default ErpRentalHandoverDO selectByNo(String no) {
        return selectOne(ErpRentalHandoverDO::getNo, no);
    }

}