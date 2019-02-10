package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.constant.PlayerId;
import com.github.mikolajk.kalah.model.KalahGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KalahGameCreator {

    private final KalahStartingGameStateSupplier startingGameStateSupplier;
    private final GameIdSupplier gameIdSupplier;

    @Autowired
    public KalahGameCreator(KalahStartingGameStateSupplier startingGameStateSupplier, GameIdSupplier gameIdSupplier) {
        this.startingGameStateSupplier = startingGameStateSupplier;
        this.gameIdSupplier = gameIdSupplier;
    }

    public KalahGame createNewGame() {
        return new KalahGame(gameIdSupplier.get(), PlayerId.PLAYER_ONE_ID, startingGameStateSupplier.get());
    }

}
