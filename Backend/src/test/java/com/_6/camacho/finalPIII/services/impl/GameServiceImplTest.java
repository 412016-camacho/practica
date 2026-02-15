package com._6.camacho.finalPIII.services.impl;

import com._6.camacho.finalPIII.dtos.*;
import com._6.camacho.finalPIII.entities.Cell;
import com._6.camacho.finalPIII.entities.Game;
import com._6.camacho.finalPIII.entities.TurnLog;
import com._6.camacho.finalPIII.entities.World;
import com._6.camacho.finalPIII.entities.enums.CellType;
import com._6.camacho.finalPIII.entities.enums.GameStatus;
import com._6.camacho.finalPIII.repositories.GameRepository;
import com._6.camacho.finalPIII.repositories.TurnLogRepository;
import com._6.camacho.finalPIII.repositories.WorldRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private WorldRepository worldRepository;

    @Mock
    private TurnLogRepository turnLogRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    private World testWorld;
    private Game testGame;

    @BeforeEach
    void setUp() {
        testWorld = World.builder()
                .id(1L)
                .name("Test World")
                .description("A test world")
                .cells(new ArrayList<>())
                .build();

        // Add cells to the world
        for (int i = 0; i <= 29; i++) {
            Cell cell = Cell.builder()
                    .id((long) i)
                    .index(i)
                    .type(CellType.EMPTY)
                    .world(testWorld)
                    .build();
            testWorld.getCells().add(cell);
        }

        // Add a monster at position 5
        testWorld.getCells().get(5).setType(CellType.MONSTER);
        testWorld.getCells().get(5).setValue(1.0);

        // Add a bonus at position 10
        testWorld.getCells().get(10).setType(CellType.BONUS);
        testWorld.getCells().get(10).setValue(2.0);

        testGame = Game.builder()
                .id(1L)
                .playerName("TestPlayer")
                .world(testWorld)
                .position(0)
                .life(5.0)
                .status(GameStatus.IN_PROGRESS)
                .turnNumber(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .turnLogs(new ArrayList<>())
                .build();
    }

    @Test
    void createGame_ValidRequest_ReturnsGameResponse() {
        CreateGameRequest request = CreateGameRequest.builder()
                .worldId(1L)
                .playerName("TestPlayer")
                .initialLife(5)
                .build();

        when(worldRepository.findById(1L)).thenReturn(Optional.of(testWorld));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            game.setId(1L);
            return game;
        });

        GameResponse response = gameService.createGame(request);

        assertNotNull(response);
        assertEquals(1L, response.getGameId());
        assertEquals(1L, response.getWorldId());
        assertEquals("TestPlayer", response.getPlayerName());
        assertEquals(0, response.getPosition());
        assertEquals(5.0, response.getLife());
        assertEquals(GameStatus.IN_PROGRESS, response.getStatus());
        assertEquals(0, response.getTurnNumber());

        verify(worldRepository).findById(1L);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void createGame_WorldNotFound_ThrowsEntityNotFoundException() {
        CreateGameRequest request = CreateGameRequest.builder()
                .worldId(999L)
                .playerName("TestPlayer")
                .initialLife(5)
                .build();

        when(worldRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> gameService.createGame(request));

        verify(worldRepository).findById(999L);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void getGameById_ExistingGame_ReturnsGameResponse() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        GameResponse response = gameService.getGameById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getGameId());
        assertEquals("TestPlayer", response.getPlayerName());
    }

    @Test
    void getGameById_NonExistingGame_ThrowsEntityNotFoundException() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> gameService.getGameById(999L));
    }

    @Test
    void playTurn_ValidTurn_ReturnsTurnResponse() {
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(3)
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getGameId());
        assertEquals(1, response.getTurnNumber());
        assertEquals(0, response.getFromPosition());
        assertEquals(3, response.getToPosition());
        assertEquals(3, response.getDice());
        assertEquals(GameStatus.IN_PROGRESS, response.getStatusAfter());
    }

    @Test
    void playTurn_GameAlreadyWon_ThrowsConflictException() {
        testGame.setStatus(GameStatus.WON);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        assertThrows(ResponseStatusException.class, () ->
                gameService.playTurn(1L, new PlayTurnRequest()));
    }

    @Test
    void playTurn_GameAlreadyLost_ThrowsConflictException() {
        testGame.setStatus(GameStatus.LOST);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        assertThrows(ResponseStatusException.class, () ->
                gameService.playTurn(1L, new PlayTurnRequest()));
    }

    @Test
    void playTurn_MonsterCell_ReducesLife() {
        testGame.setPosition(0);
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(5)  // Move to position 5 (monster)
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(5, response.getToPosition());
        assertEquals(-1.0, response.getDeltaLife());
        assertEquals(4.0, response.getLifeAfter());
    }

    @Test
    void playTurn_BonusCell_IncreasesLife() {
        testGame.setPosition(4);
        testGame.setLife(5.0);
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(6)  // Move to position 10 (bonus)
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(10, response.getToPosition());
        assertEquals(2.0, response.getDeltaLife());
        assertEquals(7.0, response.getLifeAfter());
    }

    @Test
    void playTurn_BonusExceedsMaxLife_ClampToMax() {
        testGame.setPosition(4);
        testGame.setLife(9.0);  // Almost max
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(6)  // Move to position 10 (bonus +2)
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(10.0, response.getLifeAfter());  // Clamped to 10
    }

    @Test
    void playTurn_LifeReachesZero_StatusBecomesLost() {
        testGame.setPosition(0);
        testGame.setLife(1.0);  // Will die from monster at position 5 (-1)
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(5)
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(0.0, response.getLifeAfter());
        assertEquals(GameStatus.LOST, response.getStatusAfter());
    }

    @Test
    void playTurn_ReachPosition29_StatusBecomesWon() {
        testGame.setPosition(25);
        testGame.setLife(5.0);
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(6)  // Would be 31, but clamps to 29
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(29, response.getToPosition());
        assertEquals(GameStatus.WON, response.getStatusAfter());
    }

    @Test
    void playTurn_PositionExceeds29_ClampTo29() {
        testGame.setPosition(27);
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(6)  // Would be 33
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(29, response.getToPosition());  // Clamped to 29
    }

    @Test
    void playTurn_LifeExactlyHalf_GameContinues() {
        testGame.setPosition(0);
        testGame.setLife(1.5);  // After monster -1, will be 0.5
        PlayTurnRequest request = PlayTurnRequest.builder()
                .diceOverride(5)
                .build();

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        TurnResponse response = gameService.playTurn(1L, request);

        assertEquals(0.5, response.getLifeAfter());
        assertEquals(GameStatus.IN_PROGRESS, response.getStatusAfter());
    }

    @Test
    void getTurnHistory_ValidGame_ReturnsTurnHistory() {
        List<TurnLog> turnLogs = List.of(
                TurnLog.builder()
                        .turnNumber(1)
                        .fromPosition(0)
                        .toPosition(3)
                        .dice(3)
                        .cellType(CellType.EMPTY)
                        .deltaLife(0.0)
                        .lifeAfter(5.0)
                        .statusAfter(GameStatus.IN_PROGRESS)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(turnLogRepository.findByGameIdOrderByTurnNumberAsc(1L)).thenReturn(turnLogs);

        TurnHistoryResponse response = gameService.getTurnHistory(1L, null, null);

        assertNotNull(response);
        assertEquals(1L, response.getGameId());
        assertEquals(1, response.getTurns().size());
    }

    @Test
    void getTurnHistory_InvalidRange_ThrowsBadRequest() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        assertThrows(ResponseStatusException.class, () ->
                gameService.getTurnHistory(1L, 10, 5));  // from > to
    }
}
