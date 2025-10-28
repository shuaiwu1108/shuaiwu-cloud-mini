import type { Dayjs } from 'dayjs';

import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace NoteApi {
  /** 小红书笔记信息 */
  export interface Note {
    id: number; // 笔记编号
    platformNoteId: string; // 平台笔记编号
    userId: number; // 用户编号
    userName: string; // 用户名称
    name: string; // 笔记名称
    image: string; // 快照图
    releaseTime: Dayjs | string; // 发布时间
    content: string; // 内容
    type: string; // 笔记类型（0图文 1视频）
    views: number; // 观看数
    comments: number; // 评论数
    likes: number; // 点赞数
    collections: number; // 收藏数
    forwards: number; // 转发数
  }
}

/** 查询小红书笔记分页 */
export function getNotePage(params: PageParam) {
  return requestClient.get<PageResult<NoteApi.Note>>('/xhs/note/page', {
    params,
  });
}

/** 查询小红书笔记详情 */
export function getNote(id: number) {
  return requestClient.get<NoteApi.Note>(`/xhs/note/get?id=${id}`, {
    timeout: 0,
  });
}

/** 新增小红书笔记 */
export function createNote(data: NoteApi.Note) {
  return requestClient.post('/xhs/note/create', data, { timeout: 0 });
}

/** 修改小红书笔记 */
export function updateNote(data: NoteApi.Note) {
  return requestClient.put('/xhs/note/update', data, { timeout: 0 });
}

/** 删除小红书笔记 */
export function deleteNote(id: number) {
  return requestClient.delete(`/xhs/note/delete?id=${id}`, { timeout: 0 });
}

/** 批量删除小红书笔记 */
export function deleteNoteList(ids: number[]) {
  return requestClient.delete(`/xhs/note/delete-list?ids=${ids.join(',')}`, {
    timeout: 0,
  });
}

/** 导出小红书笔记 */
export function exportNote(params: any) {
  return requestClient.download('/xhs/note/export-excel', params);
}
