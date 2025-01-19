package cn.iocoder.yudao.module.erp.dal.mysql.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.record.ErpRentalRecordPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.record.ErpStockRecordPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalRecordDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 租赁库存明细 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/19
 **/
@Mapper
public interface ErpRentalRecordMapper extends BaseMapperX<ErpRentalRecordDO> {

    default PageResult<ErpRentalRecordDO> selectPage(ErpRentalRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpRentalRecordDO>()
                .eqIfPresent(ErpRentalRecordDO::getProductId, reqVO.getProductId())
                .eqIfPresent(ErpRentalRecordDO::getCustomerId, reqVO.getCustomerId())
                .eqIfPresent(ErpRentalRecordDO::getBizType, reqVO.getBizType())
                .likeIfPresent(ErpRentalRecordDO::getBizNo, reqVO.getBizNo())
                .betweenIfPresent(ErpRentalRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpRentalRecordDO::getId));
    }

}