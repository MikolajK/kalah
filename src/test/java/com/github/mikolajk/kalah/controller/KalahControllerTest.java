package com.github.mikolajk.kalah.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikolajk.kalah.exception.IllegalMoveException;
import com.github.mikolajk.kalah.model.ErrorResponse;
import com.github.mikolajk.kalah.model.KalahGameCreationResponse;
import com.github.mikolajk.kalah.model.KalahGameStateResponse;
import com.github.mikolajk.kalah.service.KalahService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class KalahControllerTest {

    private static final int GAME_ID = 0;
    private static final int PIT_ID = 1;
    private static final Map<Integer, Integer> EXPECTED_GAME_STATE =
            IntStream.rangeClosed(1, 14).boxed().collect(toMap(identity(), id -> 6));
    private static final String MAKE_MOVE_URL = "/games/{gameId}/pits/{pitId}";
    private static final String CREATE_GAME_URL = "/games";

    private MockMvc mockMvc;

    @Mock
    private KalahService kalahService;

    @InjectMocks
    private KalahController kalahController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(kalahController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        when(kalahService.createNewGame()).thenReturn(GAME_ID);
        when(kalahService.makeMove(GAME_ID, PIT_ID)).thenReturn(EXPECTED_GAME_STATE);
    }

    @Test
    public void createGame_correctRequest_returnsIdAndUrl() throws Exception {
        // When
        MvcResult result = mockMvc.perform(post(CREATE_GAME_URL))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        // Then
        KalahGameCreationResponse responseBody = getResponse(result, KalahGameCreationResponse.class);
        assertThat(responseBody.getId()).isEqualTo(GAME_ID);
        assertThat(responseBody.getUri()).endsWith("/games/" + GAME_ID);
    }

    @Test
    public void createGame_exceptionThrown_returnsInternalServerErrorWithDetails() throws Exception {
        // Given
        String exceptionMessage = "Something went wrong";
        given(kalahService.createNewGame()).willThrow(new RuntimeException(exceptionMessage));

        // When
        MvcResult mvcResult = mockMvc.perform(post(CREATE_GAME_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        // Then
        ErrorResponse responseBody = getResponse(mvcResult, ErrorResponse.class);

        assertThat(responseBody.getException()).isEqualTo("RuntimeException");
        assertThat(responseBody.getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    public void makeMove_correctMove_returnsGameState() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(put(MAKE_MOVE_URL, GAME_ID, PIT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String responseBodyString = mvcResult.getResponse().getContentAsString();
        KalahGameStateResponse responseBody = new ObjectMapper().readValue(responseBodyString, KalahGameStateResponse.class);
        assertThat(responseBody.getId()).isEqualTo(GAME_ID);
        assertThat(responseBody.getUrl()).endsWith("/games/" + GAME_ID);
        assertThat(responseBody.getStatus()).isEqualTo(EXPECTED_GAME_STATE);
    }

    @Test
    public void makeMove_incorrectMove_returnsConflictWithDetails() throws Exception {
        // Given
        String exceptionMessage = "Incorrect pit";
        given(kalahService.makeMove(GAME_ID, PIT_ID)).willThrow(new IllegalMoveException(exceptionMessage));

        // When
        MvcResult mvcResult = mockMvc.perform(put(MAKE_MOVE_URL, GAME_ID, PIT_ID))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        // Then
        Class<ErrorResponse> valueType = ErrorResponse.class;
        ErrorResponse responseBody = getResponse(mvcResult, valueType);

        assertThat(responseBody.getException()).isEqualTo("IllegalMoveException");
        assertThat(responseBody.getMessage()).isEqualTo(exceptionMessage);
    }

    private <T> T getResponse(MvcResult mvcResult, Class<T> returnClass) throws IOException {
        String responseBodyString = mvcResult.getResponse().getContentAsString();
        return new ObjectMapper().readValue(responseBodyString, returnClass);
    }


}