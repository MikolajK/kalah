package com.github.mikolajk.kalah.constant;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PitId {

    public static int PLAYER_ONE_KALAH = 7;
    public static List<Integer> PLAYER_ONE_PITS = IntStream.rangeClosed(1, 6).boxed().collect(Collectors.toList());
    public static int PLAYER_TWO_KALAH = 14;
    public static List<Integer> PLAYER_TWO_PITS = IntStream.rangeClosed(8, 13).boxed().collect(Collectors.toList());

}
