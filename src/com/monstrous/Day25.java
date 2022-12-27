package com.monstrous;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Day25 {

    final static int MAX_DIGITS = 30;

    final FileInput input;
    StringBuilder sb = new StringBuilder();


    public Day25() {
        System.out.print("Day 25\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day25.txt");

        long sum = 0;
        for(String line : input.lines ) {
            long val = decode(line);
            sum += val;
            System.out.println(line+"    "+val);
        }
        System.out.println("Sum = "+sum);
        System.out.println("Sum in SNAFU = "+encode(sum));

//        for(long v = 1; v <= 25; v++)
//            test(v);
//        test(2022);
//        test(12345);
//        test(314159265) ;



        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    private void test(long nr) {
        String s = encode(nr);
        long val = decode(s);
        System.out.println(nr+"     "+s+"   = "+val);
    }

    private long decode(String snafu) {
        long value = 0;
        for(int i = 0; i < snafu.length(); i++){
            char k = snafu.charAt(i);
            value *= 5;
            switch(k){
                case '0': ; break;
                case '1': value += 1; break;
                case '2': value += 2; break;
                case '-': value -= 1; break;
                case '=': value -= 2; break;
            }
        }
        return value;
    }

    private String symbols = "012=-";

    private String encode(long value) {
       //value += 2;
        int[] digits = new int[MAX_DIGITS];
        int index = 0;
        while(value > 0) {
            int digit = (int) (value % 5);
            value /= 5;
            digits[index] = digit;
            index++;
        }
        int numDigits = index;
        for(int i = 0; i < numDigits; i++) {
            if(digits[i] >=3)
                digits[i+1]++;
            if(digits[i] == 5) {
                //digits[i + 1]++;
                digits[i] = 0;
            }
        }
        if(digits[numDigits-1] > 0)
            numDigits++;
        sb.setLength(0);
        boolean nonzero = false;
        for(int i = MAX_DIGITS-1; i >= 0; i--) {
            if(digits[i] != 0)
                nonzero = true;
            if(nonzero)
                sb.append( symbols.charAt( digits[i] ));
        }
        return sb.toString();
    }


}
