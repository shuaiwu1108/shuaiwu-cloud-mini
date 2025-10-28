package com.shuaiwu.cloud.module.xhs.enums;

import com.shuaiwu.cloud.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_003_000_000, "小红书用户不存在");
    ErrorCode USER_IS_EXISTS = new ErrorCode(1_003_000_001, "小红书用户已存在");
    ErrorCode NOTE_NOT_EXISTS = new ErrorCode(1_003_000_002, "小红书笔记不存在");

}
