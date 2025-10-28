import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { UserApi } from '#/api/xhs/user';

import { DICT_TYPE, getDictOptions } from '#/utils';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'name',
      label: '名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入名称',
      },
    },
    {
      fieldName: 'gender',
      label: '性别',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SYSTEM_USER_SEX, 'string'),
        buttonStyle: 'solid',
        optionType: 'button',
      },
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
      },
    },
    {
      fieldName: 'image',
      label: '头像',
      component: 'ImageUpload',
    },
    {
      fieldName: 'explainStr',
      label: '个人说明',
      component: 'Input',
      componentProps: {
        placeholder: '请输入个人说明',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'name',
      label: '名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入名称',
      },
    },
    {
      fieldName: 'gender',
      label: '性别',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.SYSTEM_USER_SEX, 'string'),
        placeholder: '请输入性别',
      },
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入联系电话',
      },
    },
    {
      fieldName: 'status',
      label: '账号状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.XHS_USER_STATUS, 'string'),
        placeholder: '请选择账号状态',
      },
    },
    {
      fieldName: 'loginStatus',
      label: '登录状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.XHS_LOGIN_STATUS, 'string'),
        placeholder: '请选择登录状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<UserApi.User>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: 'ID',
      minWidth: 60,
    },
    {
      field: 'name',
      title: '名称',
      minWidth: 120,
    },
    {
      field: 'platformNo',
      title: '平台账号',
      minWidth: 120,
    },
    {
      field: 'gender',
      title: '性别',
      minWidth: 60,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SYSTEM_USER_SEX },
      },
    },
    {
      field: 'phone',
      title: '联系电话',
      minWidth: 120,
    },
    {
      field: 'image',
      title: '头像',
      minWidth: 80,
      cellRender: {
        name: 'CellImage',
      },
    },
    {
      field: 'status',
      title: '账号状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.XHS_USER_STATUS },
      },
    },
    {
      field: 'statusImage',
      title: '账号状态图',
      minWidth: 100,
      cellRender: {
        name: 'CellImage',
      },
    },
    {
      field: 'loginStatus',
      title: '登录状态',
      minWidth: 80,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.XHS_LOGIN_STATUS },
      },
    },
    {
      field: 'watchNum',
      title: '关注数',
      minWidth: 80,
    },
    {
      field: 'fansNum',
      title: '粉丝数',
      minWidth: 80,
    },
    {
      field: 'starsNum',
      title: '获赞与收藏数',
      minWidth: 100,
    },
    {
      field: 'explainStr',
      title: '个人说明',
      minWidth: 120,
    },
    {
      field: 'creator',
      title: '创建者',
      minWidth: 120,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'updater',
      title: '更新者',
      minWidth: 120,
    },
    {
      title: '操作',
      width: 300,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
