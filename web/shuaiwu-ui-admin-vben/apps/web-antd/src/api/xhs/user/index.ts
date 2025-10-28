import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace UserApi {
  /** 小红书-用户管理信息 */
  export interface User {
    id: number; // ID
    name: string; // 名称
    platformNo: string; // 平台账号
    gender: string; // 性别
    phone: string; // 联系电话
    email: string; // 邮箱
    image: string; // 头像
    watchNum: string; // 关注数
    fansNum: string; // 粉丝数
    starsNum: string; // 收藏与获赞数
    explainStr: string; // 个人说明
    status: string; // 账号状态（0健康 1停用）
    statusImage: string; // 账号状态图
    loginStatus: string; // 登录状态（0未登录 1已登录）
    cookie: string; // 登录cookie
    delFlag: string; // 删除标志（0代表存在 2代表删除）
    createBy: string; // 创建者
    updateBy: string; // 更新者
  }
}

/** 查询小红书-用户管理分页 */
export function getUserPage(params: PageParam) {
  return requestClient.get<PageResult<UserApi.User>>('/xhs/user/page', {
    params,
  });
}

/** 获取小红书-用户管理精简信息列表 */
export function getUserListAll() {
  return requestClient.get<UserApi.User[]>('/xhs/user/list-all');
}

/** 查询小红书-用户管理详情 */
export function getUser(id: number) {
  return requestClient.get<UserApi.User>(`/xhs/user/get?id=${id}`);
}

/** 新增小红书-用户管理 */
export function createUser(data: UserApi.User) {
  return requestClient.post('/xhs/user/create', data);
}

/** 修改小红书-用户管理 */
export function updateUser(data: UserApi.User) {
  return requestClient.put('/xhs/user/update', data);
}

/** 删除小红书-用户管理 */
export function deleteUser(id: number) {
  return requestClient.delete(`/xhs/user/delete?id=${id}`);
}

/** 批量删除小红书-用户管理 */
export function deleteUserList(ids: number[]) {
  return requestClient.delete(`/xhs/user/delete-list?ids=${ids.join(',')}`);
}

/** 导出小红书-用户管理 */
export function exportUser(params: any) {
  return requestClient.download('/xhs/user/export-excel', params);
}

/** 获取验证码 */
export function getVerifyCode(userId: number) {
  return requestClient.get(`/xhs/user/${userId}/verify-code`, {
    timeout: 0, // 设置为0表示不超时
  });
}

/** 验证码登录参数 */
export interface LoginByCodeParams {
  id: number;
  phone: string;
  verifyCode: string;
}

/** 验证码登录 */
export function loginByCode(params: LoginByCodeParams) {
  return requestClient.post('/xhs/user/login', params, {
    timeout: 0,
  });
}

/** 笔记同步参数 */
export interface SyncNotesParams {
  phone: string;
}

/** 同步用户笔记 */
export function syncUserNotes(params: SyncNotesParams) {
  return requestClient.post('/xhs/user/sync-notes', params, {
    timeout: 0, // 设置为0表示不超时
  });
}
