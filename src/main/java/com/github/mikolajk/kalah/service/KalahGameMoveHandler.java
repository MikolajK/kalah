package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.constant.PlayerId;
import com.github.mikolajk.kalah.exception.IllegalMoveException;
import com.github.mikolajk.kalah.model.KalahGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.github.mikolajk.kalah.constant.PitId.PLAYER_ONE_KALAH;
import static com.github.mikolajk.kalah.constant.PitId.PLAYER_ONE_PITS;
import static com.github.mikolajk.kalah.constant.PitId.PLAYER_TWO_KALAH;
import static com.github.mikolajk.kalah.constant.PitId.PLAYER_TWO_PITS;

@Component
@Slf4j
public class KalahGameMoveHandler {

    public void performMove(KalahGame game, int pitId) {
        validate(game, pitId);
        updateGameState(game, pitId);
    }

    private void validate(KalahGame game, int pitId) {
        if (pitId == PLAYER_ONE_KALAH || pitId == PLAYER_TWO_KALAH) {
            throw new IllegalMoveException("Cannot move stones from a Kalah");
        }

        List<Integer> playerPits = getPlayerPits(game.getActivePlayer());
        if (!playerPits.contains(pitId)) {
            throw new IllegalMoveException("Pit ID incorrect or does not belong to the current active player: " + pitId);
        }

        Map<Integer, Integer> gameState = game.getGameState();
        if (gameState.get(pitId) == 0) {
            throw new IllegalMoveException("Pit contains no stones");
        }
    }

    private void updateGameState(KalahGame game, int pitId) {
        // TODO All of the variables declared until the move is made could be stored in an object for readability
        Map<Integer, Integer> gameState = game.getGameState();
        int stonesAmount = gameState.get(pitId);
        int opponentsKalah;
        int playerKalah;
        List<Integer> playerPits;

        log.info("Game {} - moving stones from pit {}", game.getId(), pitId);

        if (game.getActivePlayer() == PlayerId.PLAYER_ONE_ID) {
            opponentsKalah = PLAYER_TWO_KALAH;
            playerKalah = PLAYER_ONE_KALAH;
            playerPits = PLAYER_ONE_PITS;
        } else {
            opponentsKalah = PLAYER_ONE_KALAH;
            playerKalah = PLAYER_TWO_KALAH;
            playerPits = PLAYER_TWO_PITS;
        }

        int distanceToOpponentsKalah = playerKalah - pitId + 7;
        boolean movesOverOpponentsKalah = stonesAmount >= distanceToOpponentsKalah;
        int movesAmount = movesOverOpponentsKalah ? stonesAmount + 1 : stonesAmount;

        gameState.put(pitId, 0);
        int movesMade = 0;
        int unboundLastPitId = pitId + movesAmount;
        for (int currentPitId = pitId + 1; currentPitId <= unboundLastPitId; currentPitId++) {

            int currentPitIdLimitedToBoard = currentPitId > PLAYER_TWO_KALAH ? (currentPitId % 14) : currentPitId;
            if (currentPitIdLimitedToBoard != opponentsKalah) {
                gameState.put(currentPitIdLimitedToBoard, gameState.get(currentPitIdLimitedToBoard) + 1);
            }

            movesMade++;
            if (movesMade == movesAmount) {
                if (gameState.get(currentPitIdLimitedToBoard) == 1 && playerPits.contains(currentPitIdLimitedToBoard)) {
                    // Capture the stone if it's the last one and lands in an empty pit belonging to the player
                    gameState.put(currentPitIdLimitedToBoard, 0);
                    gameState.put(playerKalah, gameState.get(playerKalah) + 1);

                    // Capture the stones in opposing pit
                    int opposingPit = (playerKalah + (playerKalah - currentPitIdLimitedToBoard)) % 14;
                    gameState.put(playerKalah, gameState.get(playerKalah) + gameState.get(opposingPit));
                    gameState.put(opposingPit, 0);
                    log.info("Landed in empty pit {}, capturing pit {}", currentPitIdLimitedToBoard, opposingPit);
                }
                boolean gameOver = checkAndHandleGameOver(game);
                if (!gameOver) {
                    game.setActivePlayer(currentPitIdLimitedToBoard ==
                            playerKalah ?
                            game.getActivePlayer() :
                            (game.getActivePlayer() % 2) + 1);
                }

            }
        }
    }

    private boolean checkAndHandleGameOver(KalahGame game) {
        if (getTotalAmountOfStones(game, PlayerId.PLAYER_ONE_ID) == 0
                || getTotalAmountOfStones(game, PlayerId.PLAYER_TWO_ID) == 0) {
            endGame(game);
            return true;
        }
        return false;
    }

    private void endGame(KalahGame game) {
        Map<Integer, Integer> gameState = game.getGameState();
        int playerOneRemainingStones = getTotalAmountOfStones(game, PlayerId.PLAYER_ONE_ID);
        int playerTwoRemainingStones = getTotalAmountOfStones(game, PlayerId.PLAYER_TWO_ID);

        gameState.put(PLAYER_ONE_KALAH, gameState.get(PLAYER_ONE_KALAH) + playerOneRemainingStones);
        gameState.put(PLAYER_TWO_KALAH, gameState.get(PLAYER_TWO_KALAH) + playerTwoRemainingStones);

        PLAYER_ONE_PITS.forEach(pitId -> gameState.put(pitId, 0));
        PLAYER_TWO_PITS.forEach(pitId -> gameState.put(pitId, 0));
    }

    private int getTotalAmountOfStones(KalahGame game, int playerId) {
        Map<Integer, Integer> gameState = game.getGameState();
        return getPlayerPits(playerId).stream().mapToInt(gameState::get).sum();
    }

    private List<Integer> getPlayerPits(int playerId) {
        return playerId == PlayerId.PLAYER_ONE_ID ? PLAYER_ONE_PITS : PLAYER_TWO_PITS;
    }
}
