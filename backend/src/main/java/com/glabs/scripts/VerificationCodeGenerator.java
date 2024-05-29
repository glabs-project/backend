package com.glabs.scripts;

import java.security.SecureRandom;

public class VerificationCodeGenerator {

    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 4;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}