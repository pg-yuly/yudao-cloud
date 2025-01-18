package cn.iocoder.yudao.module.erp.service.sale;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.sale.vo.contract.ErpContractSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpContractDO;
import jakarta.validation.Valid;


/**
 * ERP 合同 Service 接口
 *
 * @author 于立洋
 * @version 1.0
 * @since 2025/1/17
 **/
public interface ErpContractService {

    /**
     * 创建合同
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createContract(@Valid ErpContractSaveReqVO createReqVO);

    /**
     * 更新合同
     *
     * @param updateReqVO 更新信息
     */
    void updateContract(@Valid ErpContractSaveReqVO updateReqVO);

    /**
     * 删除合同
     *
     * @param id 编号
     */
    void deleteContract(Long id);

    /**
     * 获得合同
     *
     * @param id 编号
     * @return 合同
     */
    ErpContractDO getContract(Long id);

    /**
     * 获得合同分页
     * <p>
     * 数据权限：基于 {@link ErpContractDO} 读取
     *
     * @param pageReqVO 分页查询
     * @return 合同分页
     */
    PageResult<ErpContractDO> getContractPage(ErpContractPageReqVO pageReqVO);
}