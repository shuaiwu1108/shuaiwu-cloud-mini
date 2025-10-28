import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

namespace ErpSaleOutApi {
  /** 销售出库信息 */
  export interface SaleOut {
    id?: number; // 销售出库编号
    no: string; // 销售出库号
    customerId: number; // 客户编号
    outTime: Date; // 出库时间
    totalCount: number; // 合计数量
    totalPrice: number; // 合计金额，单位：元
    status: number; // 状态
    remark: string; // 备注
  }

  /** 销售出库分页查询参数 */
  export interface SaleOutPageParams extends PageParam {
    no?: string;
    customerId?: number;
    status?: number;
  }

  /** 销售出库状态更新参数 */
  export interface SaleOutStatusParams {
    id: number;
    status: number;
  }
}

/**
 * 查询销售出库分页
 */
export function getSaleOutPage(params: ErpSaleOutApi.SaleOutPageParams) {
  return requestClient.get<PageResult<ErpSaleOutApi.SaleOut>>(
    '/erp/sale-out/page',
    {
      params,
    },
  );
}

/**
 * 查询销售出库详情
 */
export function getSaleOut(id: number) {
  return requestClient.get<ErpSaleOutApi.SaleOut>(`/erp/sale-out/get?id=${id}`);
}

/**
 * 新增销售出库
 */
export function createSaleOut(data: ErpSaleOutApi.SaleOut) {
  return requestClient.post('/erp/sale-out/create', data);
}

/**
 * 修改销售出库
 */
export function updateSaleOut(data: ErpSaleOutApi.SaleOut) {
  return requestClient.put('/erp/sale-out/update', data);
}

/**
 * 更新销售出库的状态
 */
export function updateSaleOutStatus(params: ErpSaleOutApi.SaleOutStatusParams) {
  return requestClient.put('/erp/sale-out/update-status', null, {
    params,
  });
}

/**
 * 删除销售出库
 */
export function deleteSaleOut(ids: number[]) {
  return requestClient.delete('/erp/sale-out/delete', {
    params: {
      ids: ids.join(','),
    },
  });
}

/**
 * 导出销售出库 Excel
 */
export function exportSaleOut(params: ErpSaleOutApi.SaleOutPageParams) {
  return requestClient.download('/erp/sale-out/export-excel', {
    params,
  });
}
