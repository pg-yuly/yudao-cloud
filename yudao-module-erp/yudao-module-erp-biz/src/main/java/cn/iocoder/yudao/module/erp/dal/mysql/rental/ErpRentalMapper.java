package cn.iocoder.yudao.module.erp.dal.mysql.rental;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.rental.ErpRentalPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ERP 租赁库存 Mapper
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Mapper
public interface ErpRentalMapper extends BaseMapperX<ErpRentalDO> {

    default PageResult<ErpRentalDO> selectPage(ErpRentalPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpRentalDO>()
                .eqIfPresent(ErpRentalDO::getProductId, reqVO.getProductId())
                .eqIfPresent(ErpRentalDO::getCustomerId, reqVO.getCustomerId())
                .orderByDesc(ErpRentalDO::getId));
    }

    default ErpRentalDO selectByProductIdAndCustomerId(Long productId, Long customerId) {
        return selectOne(ErpRentalDO::getProductId, productId,
                ErpRentalDO::getCustomerId, customerId);
    }

    default int updateCountIncrement(Long id, BigDecimal count, boolean negativeEnable) {
        LambdaUpdateWrapper<ErpRentalDO> updateWrapper = new LambdaUpdateWrapper<ErpRentalDO>()
                .eq(ErpRentalDO::getId, id);
        if (count.compareTo(BigDecimal.ZERO) > 0) {
            updateWrapper.setSql("count = count + " + count);
        } else if (count.compareTo(BigDecimal.ZERO) < 0) {
            if (!negativeEnable) {
                updateWrapper.ge(ErpRentalDO::getCount, count.abs());
            }
            updateWrapper.setSql("count = count - " + count.abs());
        }
        return update(null, updateWrapper);
    }

    default BigDecimal selectSumByProductId(Long productId) {
        // SQL sum 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<ErpRentalDO>()
                .select("SUM(count) AS sumCount")
                .eq("product_id", productId));
        // 获得数量
        if (CollUtil.isEmpty(result)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(MapUtil.getDouble(result.get(0), "sumCount", 0D));
    }

}