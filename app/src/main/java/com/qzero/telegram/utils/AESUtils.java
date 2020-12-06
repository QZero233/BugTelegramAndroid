package com.qzero.telegram.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AESUtils {

    public static byte[] getAESKey(String keySource){
        return SHA256Utils.getSHA256(keySource.getBytes());
    }

    public static byte[] aesEncrypt(byte[] clear,String pwd){
        byte[] encrypted=aesEncrypt(clear,getAESKey(pwd));
        return encrypted;
        //return Base64.encodeToString(encrypted,Base64.DEFAULT);
    }
    public static byte[] aesDecrypt(byte[] encrypted,String pwd){
        return aesDecrypt(encrypted,getAESKey(pwd));
        //byte[] encryptedInBuf=Base64.decode(encrypted,Base64.DEFAULT);
        //return aesDecrypt(encryptedInBuf,getAESKey(pwd));
    }

    private static byte[] aesEncrypt(byte[] clear,byte[] pwd){
        try{
            SecretKeySpec key = new SecretKeySpec(pwd, "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result=cipher.doFinal(clear);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] aesDecrypt(byte[] encrypted,byte[] pwd){
        try{
            SecretKeySpec key = new SecretKeySpec(pwd, "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] result=cipher.doFinal(encrypted);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
