package com.github.mikolajk.kalah.service;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class KalahStartingGameStateSupplierTest {

    @Test
    public void get_happyPath_returnsStartingGameState() {
        // When
        Map<Integer, Integer> startingGameState = new KalahStartingGameStateSupplier().get();

        // Then
        assertThat(startingGameState).containsOnlyKeys(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        assertThat(startingGameState.values()).containsExactly(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
    }

}