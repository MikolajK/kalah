package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.exception.GameNotFoundException;
import com.github.mikolajk.kalah.model.KalahGame;
import com.github.mikolajk.kalah.repository.KalahGameRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class KalahServiceTest {

    @Mock
    private KalahGameCreator kalahGameCreator;

    @Mock
    private KalahGameMoveHandler kalahGameMoveHandler;

    @Mock
    private KalahGameRepository kalahGameRepository;

    @InjectMocks
    private KalahService kalahService;

    @Rule
    public ExpectedException expectedException = none();

    private KalahGame kalahGame;

    @Before
    public void setUp() throws Exception {
        kalahGame = new KalahGame(1, 1, new HashMap<>());
    }

    @Test
    public void createGame_happyPath_createsAndStoresGame() {
        // Given
        given(kalahGameCreator.createNewGame()).willReturn(kalahGame);

        // When
        int newGameId = kalahService.createNewGame();

        // Then
        then(kalahGameCreator).should().createNewGame();
        then(kalahGameRepository).should().storeGame(kalahGame);
        assertThat(newGameId).isEqualTo(1);
    }

    @Test
    public void makeMove_gameExists_makesMove() {
        // Given
        given(kalahGameRepository.getGame(1)).willReturn(Optional.of(kalahGame));

        // When
        kalahService.makeMove(1, 1);

        // Then
        then(kalahGameRepository).should().getGame(1);
        then(kalahGameMoveHandler).should().performMove(kalahGame, 1);
    }

    @Test
    public void makeMove_gameDoesNotExist_throwsException() {
        // Given
        given(kalahGameRepository.getGame(1)).willReturn(Optional.empty());

        // Then
        expectedException.expect(GameNotFoundException.class);

        // When
        kalahService.makeMove(1, 1);
    }

}