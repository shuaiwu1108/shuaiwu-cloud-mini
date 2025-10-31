import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace ContentApi {
  /** 作品管理信息 */
  export interface Content {
    id: number; // ID
    name: string; // 作品名称
    type?: number; // 作品类型
    content: string; // 作品内容
    prompt: string; // 提示词
    files: string; // 文件列表
    remark: string; // 备注
  }
}

/** 查询作品管理分页 */
export function getContentPage(params: PageParam) {
  return requestClient.get<PageResult<ContentApi.Content>>('/ai/content/page', {
    params,
  });
}

/** 查询作品管理详情 */
export function getContent(id: number) {
  return requestClient.get<ContentApi.Content>(`/ai/content/get?id=${id}`);
}

/** 新增作品管理 */
export function createContent(data: ContentApi.Content) {
  return requestClient.post('/ai/content/create', data);
}

/** 修改作品管理 */
export function updateContent(data: ContentApi.Content) {
  return requestClient.put('/ai/content/update', data);
}

/** 删除作品管理 */
export function deleteContent(id: number) {
  return requestClient.delete(`/ai/content/delete?id=${id}`);
}

/** 批量删除作品管理 */
export function deleteContentList(ids: number[]) {
  return requestClient.delete(`/ai/content/delete-list?ids=${ids.join(',')}`);
}

/** 导出作品管理 */
export function exportContent(params: any) {
  return requestClient.download('/ai/content/export-excel', params);
}

/** AI生成内容 */
export function generateContentById(id: number) {
  return requestClient.post(`/ai/content/generate/${id}`);
}
