package com._6.camacho.finalPIII.services;

import com._6.camacho.finalPIII.dtos.*;

public interface GameService {

    GameResponse createGame(CreateGameRequest request);

    GameResponse getGameById(Long gameId);

    TurnResponse playTurn(Long gameId, PlayTurnRequest request);

    TurnHistoryResponse getTurnHistory(Long gameId, Integer from, Integer to);
}
