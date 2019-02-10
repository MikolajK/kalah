package com.github.mikolajk.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class KalahGame {

    private int id;
    private int activePlayer;
    private Map<Integer, Integer> gameState;

}
