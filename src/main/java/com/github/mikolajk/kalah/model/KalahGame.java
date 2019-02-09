package com.github.mikolajk.kalah.model;

import lombok.Value;

import java.util.Map;

@Value
public class KalahGame {

    private int id;
    private Map<Integer, Integer> gameState;

}
