package org.example.utils;

import java.security.SecureRandom;
import java.util.Arrays;

public class AddSalt {
    public String addSalt(){
        SecureRandom randomSalt = new SecureRandom();
        byte[] bytes = new byte[20];
        randomSalt.nextBytes(bytes);
        return Arrays.toString(bytes);
    }
}
