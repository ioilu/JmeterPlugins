package com.yzw.jmetersampler.utils;

import java.util.Random;

public class test {
    public static void main(String[] args) {
        for(int i = 0 ;i< 50 ;i ++) {
            Random random = new Random();
            int a = 0;
            while (a == 0) {
                a = random.nextInt(10) * 4;
                System.out.print(String.valueOf(a) + "  ");

            } ;
            System.out.println(String.valueOf(a));
        }
    }
}
