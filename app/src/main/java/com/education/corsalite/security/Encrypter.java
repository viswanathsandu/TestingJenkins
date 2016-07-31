package com.education.corsalite.security;

import com.scottyab.aescrypt.AESCrypt;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vissu on 7/28/16.
 */

public class Encrypter {

    public static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")), 0, s.length());
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
        return AESCrypt.encrypt(password, text);
    }

    public static String decrypt(String password, String encryptedData)
            throws Exception {
        return AESCrypt.decrypt(password, encryptedData);
    }
}
