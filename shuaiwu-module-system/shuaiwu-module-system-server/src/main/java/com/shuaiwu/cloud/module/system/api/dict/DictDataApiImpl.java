package com.shuaiwu.cloud.module.system.api.dict;

import com.shuaiwu.cloud.framework.common.pojo.CommonResult;
import com.shuaiwu.cloud.framework.common.util.object.BeanUtils;
import com.shuaiwu.cloud.framework.common.biz.system.dict.dto.DictDataRespDTO;
import com.shuaiwu.cloud.module.system.dal.dataobject.dict.DictDataDO;
import com.shuaiwu.cloud.module.system.service.dict.DictDataService;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static com.shuaiwu.cloud.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
@Primary // 由于 DictDataCommonApi 的存在，必须声明为 @Primary Bean
public class DictDataApiImpl implements DictDataApi {

    @Resource
    private DictDataService dictDataService;

    @Override
    public CommonResult<Boolean> validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
        return success(true);
    }

    @Override
    public CommonResult<List<DictDataRespDTO>> getDictDataList(String dictType) {
        List<DictDataDO> list = dictDataService.getDictDataListByDictType(dictType);
        return success(BeanUtils.toBean(list, DictDataRespDTO.class));
    }

}
