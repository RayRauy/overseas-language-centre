package com.school_management.overseas_language_centre.feature.integration.captcha.component;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
public class RandomCodeGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private final SecureRandom random = new SecureRandom();
    private static final String CHARS = UPPER + LOWER + DIGITS;
    public String generate(int length) {
        int codeLength = Math.max(length, 3);
        char[] code = new char[codeLength];
        code[0] = randomChar(UPPER);
        code[1] = randomChar(LOWER);
        code[2] = randomChar(DIGITS);

        for (int i = 3; i < codeLength; i++){
            code[i] = randomChar(CHARS);
        }

        shuffle(code);
        return new String(code);
    }

    private char randomChar(String chars) {
        // nextInt(n) → 0 .. n-1, then take that index from the pool
        return chars.charAt(random.nextInt(chars.length()));
    }

    private void shuffle(char[] chars) {

        // Walk from the end toward the start
        for (int i = chars.length - 1; i > 0; i--) {

            // Pick a random index in the unshuffled prefix [0 .. i]
            int j = random.nextInt(i + 1);

            // Swap chars[i] ↔ chars[j]
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
    }
}
