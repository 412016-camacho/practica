package com._6.camacho.finalPIII.controllers;

import com._6.camacho.finalPIII.dtos.*;
import com._6.camacho.finalPIII.services.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@Valid @RequestBody CreateGameRequest request) {
        GameResponse response = gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Long gameId) {
        GameResponse response = gameService.getGameById(gameId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/turns")
    public ResponseEntity<TurnResponse> playTurn(
            @PathVariable Long gameId,
            @Valid @RequestBody(required = false) PlayTurnRequest request) {
        TurnResponse response = gameService.playTurn(gameId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameId}/turns")
    public ResponseEntity<TurnHistoryResponse> getTurnHistory(
            @PathVariable Long gameId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer to) {
        TurnHistoryResponse response = gameService.getTurnHistory(gameId, from, to);
        return ResponseEntity.ok(response);
    }
}
