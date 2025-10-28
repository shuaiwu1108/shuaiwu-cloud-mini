interface PropertyAndValues {
  id: number;
  name: string;
  values?: PropertyAndValues[];
}

interface RuleConfig {
  // 需要校验的字段
  // 例：name: 'name' 则表示校验 sku.name 的值
  // 例：name: 'productConfig.stock' 则表示校验 sku.productConfig.name 的值,此处 productConfig 表示我在 Sku 上扩展的属性
  name: string;
  // 校验规格为一个毁掉函数，其中 arg 为需要校验的字段的值。
  // 例：需要校验价格必须大于0.01
  // {
  //  name:'price',
  //  rule:(arg: number) => arg > 0.01
  // }
  rule: (arg: any) => boolean;
  // 校验不通过时的消息提示
  message: string;
}

export { getPropertyList, PropertyAndValues, RuleConfig };

export { default as SkuList } from './SkuList.vue';
