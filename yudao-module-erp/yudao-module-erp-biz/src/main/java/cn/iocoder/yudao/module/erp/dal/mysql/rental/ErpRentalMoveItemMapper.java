package cn.iocoder.yudao.module.erp.dal.mysql.rental;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalMoveItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * ERP 租赁调拨单项 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Mapper
public interface ErpRentalMoveItemMapper extends BaseMapperX<ErpRentalMoveItemDO> {

    default List<ErpRentalMoveItemDO> selectListByMoveId(Long moveId) {
        return selectList(ErpRentalMoveItemDO::getMoveId, moveId);
    }

    default List<ErpRentalMoveItemDO> selectListByMoveIds(Collection<Long> moveIds) {
        return selectList(ErpRentalMoveItemDO::getMoveId, moveIds);
    }

    default int deleteByMoveId(Long moveId) {
        return delete(ErpRentalMoveItemDO::getMoveId, moveId);
    }

}