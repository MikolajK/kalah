package com.github.mikolajk.kalah.repository;

import com.github.mikolajk.kalah.exception.GameIdConflictException;
import com.github.mikolajk.kalah.model.KalahGame;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class KalahGameRepository {

    private Map<Integer, KalahGame> gameRepository;

    public KalahGameRepository() {
        this(new HashMap<>());
    }

    public KalahGameRepository(Map<Integer, KalahGame> gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Optional<KalahGame> getGame(int gameId) {
        return Optional.ofNullable(gameRepository.get(gameId));
    }

    public void storeGame(KalahGame game) {
        if (gameRepository.containsKey(game.getId())) {
            throw new GameIdConflictException("Game with this ID already exists: " + game.getId());
        }

        gameRepository.put(game.getId(), game);
    }

}
