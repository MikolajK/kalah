package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.model.KalahGame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KalahGameCreatorTest {

    private static final Map<Integer, Integer> STARTING_GAME_STATE =
            IntStream.rangeClosed(1, 14).boxed().collect(toMap(Function.identity(), i -> 4));
    @Mock
    private GameIdSupplier gameIdSupplier;

    @Mock
    private KalahStartingGameStateSupplier startingGameStateSupplier;

    @InjectMocks
    private KalahGameCreator kalahGameCreator;

    @Before
    public void setup() {
        when(gameIdSupplier.get()).thenReturn(0);
        when(startingGameStateSupplier.get()).thenReturn(STARTING_GAME_STATE);
    }

    @Test
    public void createNewGame_happyPath_createsGameZero() {
        // When
        KalahGame newGame = kalahGameCreator.createNewGame();

        // Then
        assertThat(newGame.getGameState()).isEqualTo(STARTING_GAME_STATE);
        assertThat(newGame.getId()).isEqualTo(0);
    }

}