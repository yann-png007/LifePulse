package com.lifepulse.util;

import java.util.Random;

public class RandomUtil {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final Random random = new Random();

    /**
     * 生成指定长度的随机数字字符串
     * @param length 长度
     * @return 随机数字字符串
     */
    public static String generateNumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字母字符串
     * @param length 长度
     * @return 随机字母字符串
     */
    public static String generateAlphabetic(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
