package com._6.camacho.finalPIII.controllers;

import com._6.camacho.finalPIII.dtos.*;
import com._6.camacho.finalPIII.entities.enums.CellType;
import com._6.camacho.finalPIII.entities.enums.GameStatus;
import com._6.camacho.finalPIII.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGame_ValidRequest_Returns201() throws Exception {
        CreateGameRequest request = CreateGameRequest.builder()
                .worldId(1L)
                .playerName("TestPlayer")
                .initialLife(5)
                .build();

        GameResponse response = GameResponse.builder()
                .gameId(1L)
                .worldId(1L)
                .playerName("TestPlayer")
                .position(0)
                .life(5.0)
                .status(GameStatus.IN_PROGRESS)
                .turnNumber(0)
                .build();

        when(gameService.createGame(any(CreateGameRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.game_id").value(1))
                .andExpect(jsonPath("$.player_name").value("TestPlayer"))
                .andExpect(jsonPath("$.life").value(5.0))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void createGame_InvalidInitialLife_Returns400() throws Exception {
        CreateGameRequest request = CreateGameRequest.builder()
                .worldId(1L)
                .playerName("TestPlayer")
                .initialLife(15)
                .build();

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGame_InvalidPlayerName_Returns400() throws Exception {
        CreateGameRequest request = CreateGameRequest.builder()
                .worldId(1L)
                .playerName("AB")
                .initialLife(5)
                .build();

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getGameById_ExistingGame_Returns200() throws Exception {
        GameResponse response = GameResponse.builder()
                .gameId(1L)
                .worldId(1L)
                .playerName("TestPlayer")
                .position(5)
                .life(4.0)
                .status(GameStatus.IN_PROGRESS)
                .turnNumber(2)
                .build();

        when(gameService.getGameById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game_id").value(1))
                .andExpect(jsonPath("$.position").value(5))
                .andExpect(jsonPath("$.life").value(4.0));
    }

    @Test
    void getGameById_NonExistingGame_Returns404() throws Exception {
        when(gameService.getGameById(999L))
                .thenThrow(new EntityNotFoundException("Game no encontrado"));

        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void playTurn_ValidTurn_Returns200() throws Exception {
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(4)
                .build();

        TurnResponse response = TurnResponse.builder()
                .gameId(1L)
                .turnNumber(1)
                .fromPosition(0)
                .toPosition(4)
                .dice(4)
                .cell(CellDto.builder()
                        .index(4)
                        .type(CellType.EMPTY)
                        .build())
                .deltaLife(0.0)
                .lifeAfter(5.0)
                .statusAfter(GameStatus.IN_PROGRESS)
                .build();

        when(gameService.playTurn(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/games/1/turns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turn_number").value(1))
                .andExpect(jsonPath("$.dice").value(4))
                .andExpect(jsonPath("$.to_position").value(4));
    }

    @Test
    void playTurn_InvalidDiceOverride_Returns400() throws Exception {
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(7)
                .build();

        mockMvc.perform(post("/api/games/1/turns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void playTurn_ZeroDiceOverride_Returns400() throws Exception {
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(0)
                .build();

        mockMvc.perform(post("/api/games/1/turns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void playTurn_GameAlreadyEnded_Returns409() throws Exception {
        when(gameService.playTurn(eq(1L), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "El juego ya terminó"));

        mockMvc.perform(post("/api/games/1/turns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getTurnHistory_ValidRequest_Returns200() throws Exception {
        TurnHistoryResponse response = TurnHistoryResponse.builder()
                .gameId(1L)
                .turns(List.of(
                        TurnHistoryItemDto.builder()
                                .turnNumber(1)
                                .dice(3)
                                .fromPosition(0)
                                .toPosition(3)
                                .deltaLife(0.0)
                                .lifeAfter(5.0)
                                .statusAfter(GameStatus.IN_PROGRESS)
                                .build()
                ))
                .build();

        when(gameService.getTurnHistory(1L, null, null)).thenReturn(response);

        mockMvc.perform(get("/api/games/1/turns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game_id").value(1))
                .andExpect(jsonPath("$.turns").isArray())
                .andExpect(jsonPath("$.turns[0].turn_number").value(1));
    }

    @Test
    void getTurnHistory_WithFromAndTo_Returns200() throws Exception {
        TurnHistoryResponse response = TurnHistoryResponse.builder()
                .gameId(1L)
                .turns(List.of())
                .build();

        when(gameService.getTurnHistory(1L, 1, 10)).thenReturn(response);

        mockMvc.perform(get("/api/games/1/turns?from=1&to=10"))
                .andExpect(status().isOk());
    }
}
