package com.haha.guli.mail.util;

import cn.hutool.core.lang.Validator;

public class CommonUtil {

    /**
     * 检测邮箱是否合法
     *
     * @param username 用户名
     * @return 合法状态
     */
    public static boolean checkEmail(String username) {
        return Validator.isEmail(username);
    }

    public static boolean isMobile(String mobile) {
        return Validator.isMobile(mobile);
    }
}
