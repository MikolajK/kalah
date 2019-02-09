package com.github.mikolajk.kalah.service;

import com.github.mikolajk.kalah.constant.PitId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class KalahStartingGameStateSupplier implements Supplier<Map<Integer, Integer>> {

    @Override
    public Map<Integer, Integer> get() {
        return IntStream.rangeClosed(1, 14).boxed().collect(toMap(
                identity(),
                (pitId) -> pitId == PitId.PLAYER_ONE_KALAH || pitId == PitId.PLAYER_TWO_KALAH ? 0 : 6));
    }
}
