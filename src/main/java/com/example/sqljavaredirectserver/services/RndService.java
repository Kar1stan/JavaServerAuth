package com.example.sqljavaredirectserver.services;

import com.google.inject.Singleton;

import java.util.Random;

@Singleton
public class RndService {
    private final Random random = new Random();
    private final int rnd = random.nextInt(100);

    public int getRnd() {
        return rnd;
    }
    public int getRandom() {
        return random.nextInt();
    }
}
