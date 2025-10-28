package com.shuaiwu.cloud.module.xhs.framework.rpc.config;

import com.shuaiwu.cloud.module.infra.api.config.ConfigApi;
import com.shuaiwu.cloud.module.infra.api.file.FileApi;
import com.shuaiwu.cloud.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "xhsRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
