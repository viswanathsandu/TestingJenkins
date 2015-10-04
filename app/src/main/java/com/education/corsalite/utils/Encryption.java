package com.education.corsalite.utils;

import android.util.Base64;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by vissu on 9/11/15.
 */
public class Encryption {

    public static String md5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encrypt(String password, String text)
            throws Exception {
        try {
            SecretKeySpec skeySpec = getKey(password);
            byte[] clearText = text.getBytes("UTF8");

            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            String encrypedValue = Base64.encodeToString(
                    cipher.doFinal(clearText), Base64.DEFAULT);
            return encrypedValue;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String password, String data)
            throws Exception {
        try {
            SecretKeySpec skeySpec = getKey(password);
            byte[] encrypted = Base64.decode(data, Base64.DEFAULT);
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            String encrypedValue = new String(cipher.doFinal(encrypted));
            return encrypedValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static SecretKeySpec getKey(String password)
            throws Exception {
        int keyLength = 128;
        byte[] keyBytes = new byte[keyLength / 8];
        Arrays.fill(keyBytes, (byte) 0x0);
        byte[] passwordBytes = password.getBytes("UTF-8");
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length
                : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }
}
