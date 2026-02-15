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
import com._6.camacho.finalPIII.services.GameService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final WorldRepository worldRepository;
    private final TurnLogRepository turnLogRepository;

    private static final int MAX_POSITION = 29;
    private static final double MAX_LIFE = 10.0;

    @Override
    @Transactional
    public GameResponse createGame(CreateGameRequest request) {
        World world = worldRepository.findById(request.getWorldId())
                .orElseThrow(() -> new EntityNotFoundException("World no encontrado con id: " + request.getWorldId()));

        Game game = Game.builder()
                .playerName(request.getPlayerName().trim())
                .world(world)
                .position(0)
                .life(request.getInitialLife().doubleValue())
                .status(GameStatus.IN_PROGRESS)
                .turnNumber(0)
                .seed(request.getSeed())
                .build();

        Game savedGame = gameRepository.save(game);
        return mapToGameResponse(savedGame);
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGameById(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game no encontrado con id: " + gameId));

        return mapToGameResponse(game);
    }

    @Override
    @Transactional
    public TurnResponse playTurn(Long gameId, PlayTurnRequest request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game no encontrado con id: " + gameId));

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede jugar turno: el juego ya terminó con estado " + game.getStatus());
        }

        int fromPosition = game.getPosition();

        // 1. Obtener valor del dado
        int dice;
        if (request != null && request.getDiceOverride() != null) {
            dice = request.getDiceOverride();
        } else {
            dice = game.rollDice();
        }

        // 2. Avanzar posición (clamp a 29)
        int newPosition = Math.min(MAX_POSITION, fromPosition + dice);
        game.setPosition(newPosition);

        // 3. Obtener la celda y aplicar efecto
        Map<Integer, Cell> cellMap = game.getWorld().getCellsAsMap();
        Cell cell = cellMap.get(newPosition);

        CellType cellType = cell != null ? cell.getType() : CellType.EMPTY;
        Double cellValue = cell != null ? cell.getValue() : null;

        double deltaLife = 0.0;
        double oldLife = game.getLife();

        switch (cellType) {
            case MONSTER:
                if (cellValue != null) {
                    deltaLife = -cellValue;
                    game.setLife(oldLife + deltaLife);
                }
                break;
            case BONUS:
                if (cellValue != null) {
                    deltaLife = cellValue;
                    double newLife = Math.min(MAX_LIFE, oldLife + deltaLife);
                    deltaLife = newLife - oldLife;
                    game.setLife(newLife);
                }
                break;
            case EMPTY:
            default:
                break;
        }

        // 4. Incrementar número de turno
        game.setTurnNumber(game.getTurnNumber() + 1);

        // 5. Evaluar estado del juego
        GameStatus newStatus = GameStatus.IN_PROGRESS;
        if (game.getLife() <= 0) {
            newStatus = GameStatus.LOST;
        } else if (newPosition == MAX_POSITION) {
            newStatus = GameStatus.WON;
        }
        game.setStatus(newStatus);

        // 6. Registrar TurnLog
        TurnLog turnLog = TurnLog.builder()
                .turnNumber(game.getTurnNumber())
                .fromPosition(fromPosition)
                .toPosition(newPosition)
                .dice(dice)
                .cellType(cellType)
                .cellValue(cellValue)
                .deltaLife(deltaLife)
                .lifeAfter(game.getLife())
                .statusAfter(newStatus)
                .timestamp(LocalDateTime.now())
                .build();

        game.addTurnLog(turnLog);
        game.setRandom(null);
        gameRepository.save(game);

        return TurnResponse.builder()
                .gameId(game.getId())
                .turnNumber(game.getTurnNumber())
                .fromPosition(fromPosition)
                .toPosition(newPosition)
                .dice(dice)
                .cell(CellDto.builder()
                        .index(newPosition)
                        .type(cellType)
                        .value(cellValue)
                        .build())
                .deltaLife(deltaLife)
                .lifeAfter(game.getLife())
                .statusAfter(newStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TurnHistoryResponse getTurnHistory(Long gameId, Integer from, Integer to) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game no encontrado con id: " + gameId));

        List<TurnLog> turnLogs;

        if (from != null && to != null) {
            if (from > to) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El parámetro 'from' debe ser menor o igual a 'to'");
            }
            if (from < 1 || to < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Los parámetros 'from' y 'to' deben ser mayores o iguales a 1");
            }
            turnLogs = turnLogRepository.findByGameIdAndTurnNumberBetweenOrderByTurnNumberAsc(gameId, from, to);
        } else if (from != null) {
            if (from < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El parámetro 'from' debe ser mayor o igual a 1");
            }
            turnLogs = turnLogRepository.findByGameIdOrderByTurnNumberAsc(gameId).stream()
                    .filter(t -> t.getTurnNumber() >= from)
                    .collect(Collectors.toList());
        } else if (to != null) {
            if (to < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El parámetro 'to' debe ser mayor o igual a 1");
            }
            turnLogs = turnLogRepository.findByGameIdOrderByTurnNumberAsc(gameId).stream()
                    .filter(t -> t.getTurnNumber() <= to)
                    .collect(Collectors.toList());
        } else {
            turnLogs = turnLogRepository.findByGameIdOrderByTurnNumberAsc(gameId);
        }

        List<TurnHistoryItemDto> turnItems = turnLogs.stream()
                .map(log -> TurnHistoryItemDto.builder()
                        .turnNumber(log.getTurnNumber())
                        .dice(log.getDice())
                        .fromPosition(log.getFromPosition())
                        .toPosition(log.getToPosition())
                        .deltaLife(log.getDeltaLife())
                        .lifeAfter(log.getLifeAfter())
                        .statusAfter(log.getStatusAfter())
                        .build())
                .collect(Collectors.toList());

        return TurnHistoryResponse.builder()
                .gameId(gameId)
                .turns(turnItems)
                .build();
    }

    private GameResponse mapToGameResponse(Game game) {
        return GameResponse.builder()
                .gameId(game.getId())
                .worldId(game.getWorld().getId())
                .playerName(game.getPlayerName())
                .position(game.getPosition())
                .life(game.getLife())
                .status(game.getStatus())
                .turnNumber(game.getTurnNumber())
                .build();
    }
}
