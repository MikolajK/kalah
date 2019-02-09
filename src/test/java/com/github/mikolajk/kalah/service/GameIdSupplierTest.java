package com.github.mikolajk.kalah.service;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class GameIdSupplierTest {

    private GameIdSupplier gameIdSupplier;

    @Before
    public void setup() {
        this.gameIdSupplier = new GameIdSupplier();
    }

    @Test
    public void get_firstValue_returnsZero() {
        // When
        int gameId = gameIdSupplier.get();

        // Then
        assertThat(gameId).isEqualTo(0);
    }

    @Test
    public void get_threeGames_returnsZeroOneAndTwo() {
        // When
        List<Integer> gameIds = IntStream.rangeClosed(1, 3).boxed()
                .map(i -> gameIdSupplier.get()).collect(Collectors.toList());

        // Then
        assertThat(gameIds).containsExactly(0, 1, 2);
    }

}