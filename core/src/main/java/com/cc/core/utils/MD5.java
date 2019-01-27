package com.cc.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 generator util class
 *
 * @author Sylthas
 */
public class MD5 {
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMD5(String inStr) {
        byte[] inStrBytes = StrUtils.stringNotNull(inStr).toString().getBytes();
        try {
            MessageDigest MD = MessageDigest.getInstance("MD5");
            MD.update(inStrBytes);
            byte[] mdByte = MD.digest();
            char[] str = new char[mdByte.length * 2];
            int k = 0;
            for (int i = 0; i < mdByte.length; i++) {
                byte temp = mdByte[i];
                str[k++] = hexDigits[temp >>> 4 & 0xf];
                str[k++] = hexDigits[temp & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
