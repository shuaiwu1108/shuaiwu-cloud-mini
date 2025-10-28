<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { UserApi } from '#/api/xhs/user';

import { ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { message, Modal } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteUser,
  deleteUserList,
  exportUser,
  getUserPage,
  syncUserNotes,
} from '#/api/xhs/user';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';
import LoginModal from './modules/login.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [LoginFormModal, loginFormModalApi] = useVbenModal({
  connectedComponent: LoginModal,
  destroyOnClose: true,
});

/** 处理登录 */
function handleLogin(row: UserApi.User) {
  loginFormModalApi.setData(row).open();
}

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 创建小红书-用户管理 */
function handleCreate() {
  formModalApi.setData({}).open();
}

/** 编辑小红书-用户管理 */
function handleEdit(row: UserApi.User) {
  formModalApi.setData(row).open();
}

/** 删除小红书-用户管理 */
async function handleDelete(row: UserApi.User) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteUser(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除小红书-用户管理 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteUserList(checkedIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({ records }: { records: UserApi.User[] }) {
  checkedIds.value = records.map((item) => item.id);
}

// 同步状态管理
const syncLoading = ref<null | number>(null);

/** 处理笔记同步 */
async function handleSyncNotes(row: UserApi.User) {
  Modal.confirm({
    title: '确认同步',
    content: `是否开始同步用户 "${row.name}" 的笔记？`,
    okText: '开始同步',
    cancelText: '取消',
    onOk: async () => {
      syncLoading.value = row.id;
      try {
        await syncUserNotes({ phone: row.phone });
        message.success('笔记同步成功');
      } catch {
        message.error('笔记同步失败');
      } finally {
        syncLoading.value = null;
      }
    },
  });
}

/** 导出表格 */
async function handleExport() {
  const data = await exportUser(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '小红书用户.xls', source: data });
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    pagerConfig: {
      enabled: true,
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getUserPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
  } as VxeTableGridOptions<UserApi.User>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />
    <LoginFormModal />

    <Grid table-title="小红书用户列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['小红书用户']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['xhs:user:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['xhs:user:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['xhs:user:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['xhs:user:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['xhs:user:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.id]),
                confirm: handleDelete.bind(null, row),
              },
            },
            {
              label: $t('common.login'),
              type: 'link',
              icon: ACTION_ICON.VIEW,
              auth: ['xhs:user:update'],
              onClick: handleLogin.bind(null, row),
            },
            {
              label: '笔记同步',
              type: 'link',
              icon: 'lucide:refresh-cw',
              loading: syncLoading === row.id,
              disabled: syncLoading !== null,
              auth: ['xhs:user:update'],
              onClick: handleSyncNotes.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
