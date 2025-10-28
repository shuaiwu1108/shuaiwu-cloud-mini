<script lang="ts" setup>
import type { UserApi } from '#/api/xhs/user';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { getVerifyCode, loginByCode } from '#/api/xhs/user';
import { $t } from '#/locales';

const emit = defineEmits(['success']);
const formData = ref<UserApi.User>();
const getCodeLoading = ref(false);

const [Form, formApi] = useVbenForm({
  schema: [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'phone',
      label: '手机号',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
    },
    {
      fieldName: 'verifyCode',
      label: '验证码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入验证码',
      },
      rules: 'required',
    },
  ],
});

async function handleGetCode() {
  getCodeLoading.value = true;
  try {
    const data = modalApi.getData<UserApi.User>();
    formData.value = data;
    await getVerifyCode(formData.value.id);
    message.success($t('获取验证码成功'));
  } catch (error) {
    console.error(error);
    message.error($t('获取验证码失败'));
  } finally {
    getCodeLoading.value = false;
  }
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    try {
      const data = await formApi.getValues();
      await loginByCode({
        id: data.id,
        phone: data.phone,
        verifyCode: data.verifyCode,
      });
      await modalApi.close();
      emit('success');
      message.success($t('登录成功'));
    } catch (error) {
      console.error(error);
      message.error($t('登录失败'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      await formApi.setValues({});
    }
    const data = modalApi.getData<UserApi.User>();
    if (!data || !data.id) {
      return;
    }
    formData.value = data;
    // 设置到 values
    await formApi.setValues(formData.value);
  },
});
</script>

<template>
  <Modal :title="$t('用户登录')">
    <Form class="mx-4">
      <a-button
        type="primary"
        :loading="getCodeLoading"
        @click="handleGetCode"
        class="mt-4"
      >
        {{ $t('获取验证码') }}
      </a-button>
    </Form>
  </Modal>
</template>
