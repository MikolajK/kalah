package com.github.mikolajk.kalah.repository;

import com.github.mikolajk.kalah.exception.GameIdConflictException;
import com.github.mikolajk.kalah.model.KalahGame;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;

public class KalahGameRepositoryTest {

    private static final int EXISTING_GAME_ID = 0;
    private static final KalahGame EXISTING_GAME = new KalahGame(EXISTING_GAME_ID, new HashMap<>());

    @Rule
    public ExpectedException expectedException = none();

    private KalahGameRepository kalahGameRepository;
    private Map<Integer, KalahGame> gameRepository;

    @Before
    public void setup() {
        this.gameRepository = new HashMap<>();
        this.gameRepository.put(EXISTING_GAME_ID, EXISTING_GAME);

        this.kalahGameRepository = new KalahGameRepository(gameRepository);
    }

    @Test
    public void getGame_existingGameId_returnsExistingGame() {
        // When
        Optional<KalahGame> game = kalahGameRepository.getGame(EXISTING_GAME_ID);

        // Then
        assertThat(game).get().isEqualTo(EXISTING_GAME);
    }

    @Test
    public void getGame_nonExistingGameId_returnsEmptyOptional() {
        // When
        Optional<KalahGame> game = kalahGameRepository.getGame(-1);

        // Then
        assertThat(game).isNotPresent();
    }

    @Test
    public void storeGame_newGame_storesGame() {
        // When
        KalahGame game = new KalahGame(1, new HashMap<>());
        kalahGameRepository.storeGame(game);

        // Then
        assertThat(gameRepository).containsKey(1);
        assertThat(gameRepository.get(1)).isEqualTo(game);
    }

    @Test
    public void storeGame_existingGame_throwsException() {
        // Then
        expectedException.expect(GameIdConflictException.class);
        expectedException.expectMessage("Game with this ID already exists");

        // When
        kalahGameRepository.storeGame(new KalahGame(EXISTING_GAME_ID, new HashMap<>()));
    }


}