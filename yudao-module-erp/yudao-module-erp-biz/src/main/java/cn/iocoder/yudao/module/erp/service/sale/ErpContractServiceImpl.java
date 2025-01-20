package cn.iocoder.yudao.module.erp.service.sale;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpContractDO;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpContractMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.CONTRACT_NOT_EXISTS;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.CONTRACT_NO_EXISTS;

/**
 * ERP 合同 Service 实现类
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/17
 **/
@Service
@Validated
public class ErpContractServiceImpl implements ErpContractService{

    @Resource
    private ErpContractMapper contractMapper;
    @Resource
    private ErpCustomerService customerService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Override
    public Long createContract(ErpContractSaveReqVO createReqVO) {
        // 1.1 校验关联字段
        validateRelationDataExists(createReqVO);
        // 1.2 生成合同号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.SALE_CONTRACT_NO_PREFIX);
        if (contractMapper.selectByNo(no) != null) {
            throw exception(CONTRACT_NO_EXISTS);
        }

        // 2.1 插入合同
        ErpContractDO contract = BeanUtils.toBean(createReqVO, ErpContractDO.class).setNo(no);
        contractMapper.insert(contract);
        return contract.getId();
    }

    @Override
    public void updateContract(ErpContractSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "合同编号不能为空");
        // 1.1 校验存在
        ErpContractDO contract = validateContractExists(updateReqVO.getId());
        // 1.2 校验关联字段
        validateRelationDataExists(updateReqVO);

        // 2.1 更新合同
        ErpContractDO updateObj = BeanUtils.toBean(updateReqVO, ErpContractDO.class);
        contractMapper.updateById(updateObj);
    }

    @Override
    public void deleteContract(Long id) {
        // 1.1 校验存在
        validateContractExists(id);

        // 2.1 删除合同
        contractMapper.deleteById(id);
    }

    @Override
    public ErpContractDO getContract(Long id) {
        return contractMapper.selectById(id);
    }

    @Override
    public PageResult<ErpContractDO> getContractPage(ErpContractPageReqVO pageReqVO) {
        return contractMapper.selectPage(pageReqVO);
    }

    /**
     * 校验关联数据是否存在
     *
     * @param reqVO 请求
     */
    private void validateRelationDataExists(ErpContractSaveReqVO reqVO) {
        // 1. 校验客户
        if (reqVO.getCustomerId() != null) {
            customerService.validateCustomer(reqVO.getCustomerId());
        }

        // 2. 校验签约相关字段
        if (reqVO.getSignUserId() != null) {
            adminUserApi.validateUser(reqVO.getSignUserId());
        }
    }

    private ErpContractDO validateContractExists(Long id) {
        ErpContractDO contract = contractMapper.selectById(id);
        if (contract == null) {
            throw exception(CONTRACT_NOT_EXISTS);
        }
        return contract;
    }
}
