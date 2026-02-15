package com._6.camacho.finalPIII.services.impl;

import com._6.camacho.finalPIII.dtos.RankingItemDto;
import com._6.camacho.finalPIII.dtos.RankingResponse;
import com._6.camacho.finalPIII.entities.Game;
import com._6.camacho.finalPIII.repositories.GameRepository;
import com._6.camacho.finalPIII.services.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final GameRepository gameRepository;

    private static final int DEFAULT_TOP = 10;

    @Override
    @Transactional(readOnly = true)
    public RankingResponse getRanking(Integer top) {
        int limit = (top != null && top > 0) ? top : DEFAULT_TOP;

        List<Game> wonGames = gameRepository.findWonGamesOrderedByScore();

        List<RankingItemDto> items = wonGames.stream()
                .limit(limit)
                .map(game -> {
                    double score = (game.getLife() * 10) - game.getTurnNumber();
                    return RankingItemDto.builder()
                            .playerName(game.getPlayerName())
                            .gameId(game.getId())
                            .turnNumber(game.getTurnNumber())
                            .lifeAfter(game.getLife())
                            .score(score)
                            .build();
                })
                .collect(Collectors.toList());

        return RankingResponse.builder()
                .top(limit)
                .items(items)
                .build();
    }
}
