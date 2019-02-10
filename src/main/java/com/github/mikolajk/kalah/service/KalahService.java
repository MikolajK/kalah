package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.exception.GameNotFoundException;
import com.github.mikolajk.kalah.model.KalahGame;
import com.github.mikolajk.kalah.repository.KalahGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KalahService {

    private KalahGameRepository kalahGameRepository;
    private KalahGameMoveHandler kalahGameMoveHandler;
    private KalahGameCreator kalahGameCreator;

    @Autowired
    public KalahService(KalahGameRepository kalahGameRepository, KalahGameMoveHandler kalahGameMoveHandler,
                        KalahGameCreator kalahGameCreator) {
        this.kalahGameRepository = kalahGameRepository;
        this.kalahGameMoveHandler = kalahGameMoveHandler;
        this.kalahGameCreator = kalahGameCreator;
    }

    public int createNewGame() {
        KalahGame newGame = kalahGameCreator.createNewGame();
        kalahGameRepository.storeGame(newGame);
        return newGame.getId();
    }

    public Map<Integer, Integer> makeMove(int gameId, int pitId) {
        KalahGame game =
                kalahGameRepository.getGame(gameId).orElseThrow(() -> new GameNotFoundException("Game not found " + gameId));
        kalahGameMoveHandler.performMove(game, pitId);
        return game.getGameState();
    }
}
