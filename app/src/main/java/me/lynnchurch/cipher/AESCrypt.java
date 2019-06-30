/*
 * Created by 邱志立 on 17-3-3 下午5:06
 * Copyright (c) 2017. All rights reserved.
 */

package me.lynnchurch.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Lynn on 2017-3-3.
 */

public class AESCrypt
{
    public static String ALGORITHM = "AES/CBC/PKCS5PADDING";
    public static String TRANSFORMATION = "AES";

    public static String encrypt(String data, String password) throws Exception
    {
        password = EncryptUtils.encryptSHA1ToString(password).substring(0, 16);
        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 初始化加密器
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bt = cipher.doFinal(data.getBytes("UTF-8"));
        return parseByte2HexStr(bt);
    }


    public static String decrypt(String message, String password) throws Exception
    {
        password = EncryptUtils.encryptSHA1ToString(password).substring(0, 16);
        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] res = parseHexStr2Byte(message);
        res = cipher.doFinal(res);
        return new String(res);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[])
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++)
        {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr)
    {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++)
        {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
