package com.github.mikolajk.kalah.controller;

import com.github.mikolajk.kalah.model.KalahGameCreationResponse;
import com.github.mikolajk.kalah.model.KalahGameStateResponse;
import com.github.mikolajk.kalah.service.KalahService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@Controller
public class KalahController {

    private final KalahService kalahService;

    @Autowired
    public KalahController(KalahService kalahService) {
        this.kalahService = kalahService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/games", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<KalahGameCreationResponse> createGame(HttpServletRequest request) {
        int gameId = kalahService.createNewGame();
        String gameUrl = getGameUrl(request, gameId);

        return new ResponseEntity<>(new KalahGameCreationResponse(gameId, gameUrl), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/games/{gameId}/pits/{pitId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<KalahGameStateResponse> makeMove(HttpServletRequest request, @PathVariable("gameId") int gameId,
                                                           @PathVariable("pitId") int pitId) {
        Map<Integer, Integer> status = kalahService.makeMove(gameId, pitId);
        return new ResponseEntity<>(new KalahGameStateResponse(gameId, getGameUrl(request, gameId), status), HttpStatus.OK);
    }

    private String getGameUrl(HttpServletRequest request, int gameId) {
        String baseUrl = "http://" + request.getRemoteHost() + ":" + request.getLocalPort();
        return baseUrl + "/games/" + gameId;
    }

}
