package ru.ifmo.kachalsky.test;

import java.util.Random;
import java.util.SplittableRandom;

public class Test {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        SplittableRandom n = new SplittableRandom();
        double j = 0;
        int times = 100000000;
        for (int i = 0; i < times; i++) {
            j += n.nextDouble();
        }
        System.out.println(j / times);
        System.out.println(System.currentTimeMillis() - time);
    }
}
