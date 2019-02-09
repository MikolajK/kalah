package com.github.mikolajk.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KalahGameStateResponse {

    private int id;
    private String url;
    private Map<Integer, Integer> status;

}
