package cn.iocoder.yudao.module.erp.service.rental;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.rental.vo.rental.ErpRentalPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.rental.ErpRentalDO;
import cn.iocoder.yudao.module.erp.dal.mysql.rental.ErpRentalMapper;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.sale.ErpCustomerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 租赁库存 Service 实现类
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/18
 **/
@Service
@Validated
public class ErpRentalServiceImpl implements ErpRentalService {

    /**
     * 允许库存为负数
     */
    private static final Boolean NEGATIVE_STOCK_COUNT_ENABLE = false;

    @Resource
    private ErpProductService productService;
    @Resource
    private ErpCustomerService customerService;

    @Resource
    private ErpRentalMapper rentalMapper;

    @Override
    public ErpRentalDO getStock(Long id) {
        return rentalMapper.selectById(id);
    }

    @Override
    public ErpRentalDO getStock(Long productId, Long customerId) {
        return rentalMapper.selectByProductIdAndCustomerId(productId, customerId);
    }

    @Override
    public BigDecimal getStockCount(Long productId) {
        BigDecimal count = rentalMapper.selectSumByProductId(productId);
        return count != null ? count : BigDecimal.ZERO;
    }

    @Override
    public PageResult<ErpRentalDO> getStockPage(ErpRentalPageReqVO pageReqVO) {
        return rentalMapper.selectPage(pageReqVO);
    }

    @Override
    public BigDecimal updateStockCountIncrement(Long productId, Long customerId, BigDecimal count) {
        // 1.1 查询当前库存
        ErpRentalDO stock = rentalMapper.selectByProductIdAndCustomerId(productId, customerId);
        if (stock == null) {
            stock = new ErpRentalDO().setProductId(productId).setCustomerId(customerId).setCount(BigDecimal.ZERO);
            rentalMapper.insert(stock);
        }
        // 1.2 校验库存是否充足
        if (!NEGATIVE_STOCK_COUNT_ENABLE && stock.getCount().add(count).compareTo(BigDecimal.ZERO) < 0) {
            throw exception(RENTAL_STOCK_COUNT_NEGATIVE, productService.getProduct(productId).getName(),
                    customerService.getCustomer(customerId).getName(), stock.getCount(), count);
        }

        // 2. 库存变更
        int updateCount = rentalMapper.updateCountIncrement(stock.getId(), count, NEGATIVE_STOCK_COUNT_ENABLE);
        if (updateCount == 0) {
            // 此时不好去查询最新库存，所以直接抛出该提示，不提供具体库存数字
            throw exception(RENTAL_STOCK_COUNT_NEGATIVE2, productService.getProduct(productId).getName(),
                    customerService.getCustomer(customerId).getName());
        }

        // 3. 返回最新库存
        return stock.getCount().add(count);
    }

}