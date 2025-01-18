package cn.iocoder.yudao.module.erp.dal.mysql.rental;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalHandoverItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * ERP 租赁交接单项 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Mapper
public interface ErpRentalHandoverItemMapper extends BaseMapperX<ErpRentalHandoverItemDO> {

    default List<ErpRentalHandoverItemDO> selectListByOutId(Long outId) {
        return selectList(ErpRentalHandoverItemDO::getHandoverId, outId);
    }

    default List<ErpRentalHandoverItemDO> selectListByOutIds(Collection<Long> outIds) {
        return selectList(ErpRentalHandoverItemDO::getHandoverId, outIds);
    }

    default int deleteByOutId(Long outId) {
        return delete(ErpRentalHandoverItemDO::getHandoverId, outId);
    }

}