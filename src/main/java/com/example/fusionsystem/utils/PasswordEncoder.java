package com.example.fusionsystem.utils;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordEncoder {

    /*加密密码*/
    public static String encode(String rawpassword) {
        return BCrypt.hashpw(rawpassword,BCrypt.gensalt());
    }


    /**
     * 验证密码
     * @param rawPassword 原始密码(用户输入的)
     * @param hashedPassword 加密后的密码(数据库存储的)
     */
    public static  boolean matches(String rawpassword, String hashepassword) {
        return BCrypt.checkpw(rawpassword,hashepassword);
    }

}
