package com.shuaiwu.cloud.module.system.api.oauth2;

import com.shuaiwu.cloud.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.shuaiwu.cloud.framework.common.pojo.CommonResult;
import com.shuaiwu.cloud.framework.common.util.object.BeanUtils;
import com.shuaiwu.cloud.framework.tenant.core.aop.TenantIgnore;
import com.shuaiwu.cloud.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.shuaiwu.cloud.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.shuaiwu.cloud.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.shuaiwu.cloud.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.shuaiwu.cloud.module.system.service.oauth2.OAuth2TokenService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.shuaiwu.cloud.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class OAuth2TokenApiImpl implements OAuth2TokenCommonApi {

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Override
    public CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(OAuth2AccessTokenCreateReqDTO reqDTO) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
                reqDTO.getUserId(), reqDTO.getUserType(), reqDTO.getClientId(), reqDTO.getScopes());
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class));
    }

    @Override
    @TenantIgnore // 访问令牌校验时，无需传递租户编号；主要解决上传文件的场景，前端不会传递 tenant-id
    public CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.checkAccessToken(accessToken);
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenCheckRespDTO.class));
    }

    @Override
    public CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(accessToken);
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class));
    }

    @Override
    public CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(String refreshToken, String clientId) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class));
    }

}
