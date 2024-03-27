package com.commercebank.accounts;

import java.util.Random;
import java.security.SecureRandom;
public class RandNumGenerator {
    public static long generateRandom16DigitNumber() {
        // Generate a random 16-digit number
        long min = 1000000000000000L; // 10^15
        long max = 9999999999999999L; // 10^16 - 1

        Random random = new Random();
        return min + ((long) (random.nextDouble() * (max - min + 1)));
    }

    public static int generateRandom4DigitCode(){
        int min = 1000;
        int max = 9999;
        SecureRandom random = new SecureRandom();
//        Random random = new Random(); // use this for better performance
        return min + ((int) (random.nextDouble() * (max - min + 1)));
    }
}
