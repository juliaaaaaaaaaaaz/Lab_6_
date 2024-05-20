package org.example.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingWithSalt {
    private BigInteger hash;
    public String hashing(String pswd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] messageDigestByte = messageDigest.digest((pswd).getBytes("UTF-8"));
        hash = new BigInteger(1, messageDigestByte);
        return hash.toString();
    }
}
