import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { ContentApi } from '#/api/ai/content';

import { DICT_TYPE, getDictOptions, getRangePickerDefaultProps } from '#/utils';

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
      label: '作品名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入作品名称',
      },
    },
    {
      fieldName: 'type',
      label: '作品类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.XHS_NOTE_TYPE, 'number'),
        placeholder: '请选择作品类型',
      },
    },
    {
      fieldName: 'content',
      label: '作品内容',
      component: 'Textarea',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'name',
      label: '作品名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入作品名称',
      },
    },
    {
      fieldName: 'type',
      label: '作品类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.XHS_NOTE_TYPE, 'number'),
        placeholder: '请选择作品类型',
      },
    },
    {
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<ContentApi.Content>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: 'ID',
      minWidth: 60,
    },
    {
      field: 'name',
      title: '作品名称',
      minWidth: 100,
    },
    {
      field: 'type',
      title: '作品类型',
      minWidth: 80,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.XHS_NOTE_TYPE },
      },
    },
    {
      field: 'content',
      title: '作品内容',
      minWidth: 120,
    },
    {
      field: 'prompt',
      title: '提示词',
      minWidth: 120,
    },
    {
      field: 'files',
      title: '文件列表',
      minWidth: 120,
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 120,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
