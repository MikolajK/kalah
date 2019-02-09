package com.github.mikolajk.kalah.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Component
public class GameIdSupplier implements Supplier<Integer> {

    private AtomicInteger id;

    public GameIdSupplier() {
        this.id = new AtomicInteger();
    }

    @Override
    public Integer get() {
        return id.getAndIncrement();
    }
}
