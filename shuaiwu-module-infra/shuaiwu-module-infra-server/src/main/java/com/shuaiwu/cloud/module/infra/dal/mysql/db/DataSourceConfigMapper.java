package com.shuaiwu.cloud.module.infra.dal.mysql.db;

import com.shuaiwu.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.shuaiwu.cloud.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
