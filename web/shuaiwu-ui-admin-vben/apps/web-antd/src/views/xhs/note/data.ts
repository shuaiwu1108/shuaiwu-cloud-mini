import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { NoteApi } from '#/api/xhs/note';

import { getUserListAll } from '#/api/xhs/user';
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
      fieldName: 'platformNoteId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'userId',
      label: '用户',
      component: 'ApiSelect',
      componentProps: {
        api: getUserListAll,
        labelField: 'name',
        valueField: 'id',
      },
    },
    {
      fieldName: 'name',
      label: '笔记名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入笔记名称',
      },
    },
    {
      fieldName: 'image',
      label: '快照图',
      component: 'ImageUpload',
    },
    {
      fieldName: 'files',
      label: '笔记文件',
      component: 'FileUpload',
      componentProps: {
        multiple: true,
        maxNumber: 100,
        maxSize: 100, // 10M
      },
    },
    {
      fieldName: 'releaseTime',
      label: '发布时间',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'content',
      label: '内容',
      component: 'RichTextarea',
    },
    {
      fieldName: 'type',
      label: '笔记类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.XHS_NOTE_TYPE, 'string'),
        placeholder: '请选择笔记类型',
      },
    },
    {
      fieldName: 'views',
      label: '观看数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入观看数',
      },
    },
    {
      fieldName: 'comments',
      label: '评论数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入评论数',
      },
    },
    {
      fieldName: 'likes',
      label: '点赞数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入点赞数',
      },
    },
    {
      fieldName: 'collections',
      label: '收藏数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入收藏数',
      },
    },
    {
      fieldName: 'forwards',
      label: '转发数',
      component: 'Input',
      componentProps: {
        placeholder: '请输入转发数',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'userId',
      label: '用户',
      component: 'ApiSelect',
      componentProps: {
        api: getUserListAll,
        labelField: 'name',
        valueField: 'id',
      },
    },
    {
      fieldName: 'name',
      label: '笔记名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入笔记名称',
      },
    },
    {
      fieldName: 'releaseTime',
      label: '发布时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'content',
      label: '内容',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入内容',
      },
    },
    {
      fieldName: 'type',
      label: '笔记类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.XHS_NOTE_TYPE, 'string'),
        placeholder: '请选择笔记类型',
      },
    },
    {
      fieldName: 'views',
      label: '观看数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入观看数',
      },
    },
    {
      fieldName: 'comments',
      label: '评论数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入评论数',
      },
    },
    {
      fieldName: 'likes',
      label: '点赞数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入点赞数',
      },
    },
    {
      fieldName: 'collections',
      label: '收藏数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入收藏数',
      },
    },
    {
      fieldName: 'forwards',
      label: '转发数',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入转发数',
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
export function useGridColumns(): VxeTableGridOptions<NoteApi.Note>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '笔记ID',
      minWidth: 80,
    },
    {
      field: 'platformNoteId',
      title: '平台笔记编号',
      minWidth: 100,
    },
    {
      field: 'userName',
      title: '用户',
      minWidth: 80,
    },
    {
      field: 'name',
      title: '笔记名称',
      minWidth: 120,
    },
    {
      field: 'image',
      title: '快照图',
      minWidth: 80,
      cellRender: {
        name: 'CellImage',
      },
    },
    {
      field: 'releaseTime',
      title: '发布时间',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'content',
      title: '内容',
      minWidth: 100,
    },
    {
      field: 'type',
      title: '笔记类型',
      minWidth: 80,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.XHS_NOTE_TYPE },
      },
    },
    {
      field: 'views',
      title: '观看数',
      minWidth: 80,
    },
    {
      field: 'comments',
      title: '评论数',
      minWidth: 80,
    },
    {
      field: 'likes',
      title: '点赞数',
      minWidth: 80,
    },
    {
      field: 'collections',
      title: '收藏数',
      minWidth: 80,
    },
    {
      field: 'forwards',
      title: '转发数',
      minWidth: 80,
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
