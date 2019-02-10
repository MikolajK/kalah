package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.constant.PitId;
import com.github.mikolajk.kalah.constant.PlayerId;
import com.github.mikolajk.kalah.exception.IllegalMoveException;
import com.github.mikolajk.kalah.model.KalahGame;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;

public class KalahGameMoveHandlerTest {

    private KalahGameMoveHandler gameMoveHandler;

    @Rule
    public ExpectedException expectedException = none();

    @Before
    public void setup() {
        this.gameMoveHandler = new KalahGameMoveHandler();
    }

    @Test
    public void performMove_validMoveInNewGame_performsMove() {
        // Given
        KalahGame newGame = new KalahGame(0, PlayerId.PLAYER_ONE_ID, newGameState());
        int pitId = 3;

        // When
        gameMoveHandler.performMove(newGame, pitId);

        // Then
        Map<Integer, Integer> newGameState = newGame.getGameState();
        assertThat(newGameState.get(pitId)).isEqualTo(0);
        assertThat(newGameState.get(PitId.PLAYER_ONE_KALAH)).isEqualTo(1);
        assertStonesInPitAreCorrect(newGameState, 7, 4, 5, 6, 8, 9);
        assertThat(newGame.getActivePlayer()).isEqualTo(2);
    }

    @Test
    public void performMove_extraTurn_performsMoveDoesNotChangeActivePlayer() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, extraTurnPossibleGameState());

        // When
        gameMoveHandler.performMove(game, 6);

        assertThat(game.getActivePlayer()).isEqualTo(PlayerId.PLAYER_ONE_ID);
        assertThat(game.getGameState().get(PitId.PLAYER_ONE_KALAH)).isEqualTo(6);
    }

    @Test
    public void performMove_moveOverOpponentsKalah_performsMoveNoStoneInOpponentsKalah() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, ableToMoveOverOpponentsKalahGameState());
        int pitId = 6;

        // When
        gameMoveHandler.performMove(game, pitId);

        // Then
        Map<Integer, Integer> newGameState = game.getGameState();
        assertThat(newGameState.get(pitId)).isEqualTo(0);
        assertThat(newGameState.get(PitId.PLAYER_ONE_KALAH)).isEqualTo(33);
        assertThat(newGameState.get(PitId.PLAYER_TWO_KALAH)).isEqualTo(30);
        assertThat(newGameState.get(8)).isEqualTo(2);
        assertThat(newGameState.get(11)).isEqualTo(0);
        assertThat(newGameState.get(3)).isEqualTo(0);
        assertStonesInPitAreCorrect(newGameState, 1, 1, 2, 9, 10, 12, 13);
        assertThat(game.getActivePlayer()).isEqualTo(2);
    }

    @Test
    public void performMove_gameEndingMove_gameEndsPlayerOneWins() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, oneMoveFromEndingGameState());

        // When
        gameMoveHandler.performMove(game, 2);

        // Then
        Map<Integer, Integer> newGameState = game.getGameState();
        assertStonesInPitAreCorrect(newGameState, 0, PitId.PLAYER_ONE_PITS.toArray(new Integer[]{}));
        assertStonesInPitAreCorrect(newGameState, 0, PitId.PLAYER_TWO_PITS.toArray(new Integer[]{}));
        assertThat(newGameState.get(PitId.PLAYER_ONE_KALAH)).isEqualTo(37);
        assertThat(newGameState.get(PitId.PLAYER_TWO_KALAH)).isEqualTo(35);
        assertThat(game.getActivePlayer()).isEqualTo(1);
    }

    @Test
    public void performMove_opponentsPit_throwsException() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, newGameState());

        // Then
        expectedException.expectMessage("Pit ID incorrect or does not belong to the current active player: 11");
        expectedException.expect(IllegalMoveException.class);

        // When
        gameMoveHandler.performMove(game, 11);
    }

    @Test
    public void performMove_incorrectPit_throwsException() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, newGameState());

        // Then
        expectedException.expectMessage("Pit ID incorrect or does not belong to the current active player: -1");
        expectedException.expect(IllegalMoveException.class);

        // When
        gameMoveHandler.performMove(game, -1);
    }

    @Test
    public void performMove_kalah_throwsException() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, newGameState());

        // Then
        expectedException.expectMessage("Cannot move stones from a Kalah");
        expectedException.expect(IllegalMoveException.class);

        // When
        gameMoveHandler.performMove(game, PitId.PLAYER_ONE_KALAH);
    }

    @Test
    public void performMove_emptyPit_throwsException() {
        // Given
        KalahGame game = new KalahGame(0, PlayerId.PLAYER_ONE_ID, newGameState());
        game.getGameState().put(2, 0);

        // Then
        expectedException.expectMessage("Pit contains no stones");
        expectedException.expect(IllegalMoveException.class);

        // When
        gameMoveHandler.performMove(game, 2);
    }

    private void assertStonesInPitAreCorrect(Map<Integer, Integer> newGameState, int expectedAmount, Integer... pitIds) {
        Stream.of(pitIds).forEach(pit -> assertThat(newGameState.get(pit))
                .describedAs("Incorrect amount in pit " + pit + ": " + newGameState.get(pit)).isEqualTo(expectedAmount));
    }

    private Map<Integer, Integer> oneMoveFromEndingGameState() {
        // One move left, pit 2, will end the game and player one will win
        Map<Integer, Integer> gameState = IntStream.rangeClosed(1, 14).boxed().collect(toMap(identity(),
                pitId -> pitId == PitId.PLAYER_ONE_KALAH || pitId == PitId.PLAYER_TWO_KALAH ? 35 : 0));
        gameState.put(2, 1);
        gameState.put(11, 1);
        return gameState;
    }

    private Map<Integer, Integer> ableToMoveOverOpponentsKalahGameState() {
        // 30 stones in each kalah, one in pit 8 (so the game's not over) and 10 in pit 6
        // If moving with pit 6, the player will go over opponent's kalah when moving stones and capture pits 3 and 11
        Map<Integer, Integer> gameState = IntStream.rangeClosed(1, 14).boxed().collect(toMap(identity(),
                pitId -> pitId == PitId.PLAYER_ONE_KALAH || pitId == PitId.PLAYER_TWO_KALAH ? 30 : 0));
        gameState.put(6, 10);
        gameState.put(8, 1);
        return gameState;
    }

    private Map<Integer, Integer> newGameState() {
        // Starting game state: empty kalahs, 6 stones in each pit
        return IntStream.rangeClosed(1, 14).boxed().collect(toMap(identity(),
                pitId -> pitId == PitId.PLAYER_ONE_KALAH || pitId == PitId.PLAYER_TWO_KALAH ? 0 : 6));
    }

    private Map<Integer, Integer> extraTurnPossibleGameState() {
        Map<Integer, Integer> gameState = newGameState();
        gameState.put(6, 1);
        gameState.put(PitId.PLAYER_ONE_KALAH, 5);
        return gameState;
    }
}