package com.cjh.blog.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

/**
 * @author CJH
 * on 2019/3/13
 */

public class Tools {

    private static final Random random = new Random();

    /**
     * 生成随机数
     * @param min
     * @param max
     * @return
     */
    public static int rand(int min, int max) {
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 字符串是否是纯数字
     * @param str
     * @return
     */
    public static Boolean isNumber(String str){
        if (str != null && str.length() != 0 && str.matches("\\d*")){
            return true;
        }
        return false;
    }

    /**
     * aes 加密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String enAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return new BASE64Encoder().encode(encryptedBytes);
    }

    /**
     * aes 解密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String deAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] cipherTextBytes = new BASE64Decoder().decodeBuffer(data);
        byte[] decValue = cipher.doFinal(cipherTextBytes);
        return new String(decValue);
    }


}
