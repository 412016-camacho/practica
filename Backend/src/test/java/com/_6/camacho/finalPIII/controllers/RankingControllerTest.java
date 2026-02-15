package com._6.camacho.finalPIII.controllers;

import com._6.camacho.finalPIII.dtos.RankingItemDto;
import com._6.camacho.finalPIII.dtos.RankingResponse;
import com._6.camacho.finalPIII.services.RankingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RankingController.class)
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    @Test
    void getRanking_DefaultTop_Returns200() throws Exception {
        RankingResponse response = RankingResponse.builder()
                .top(10)
                .items(List.of(
                        RankingItemDto.builder()
                                .playerName("Player1")
                                .gameId(1L)
                                .turnNumber(5)
                                .lifeAfter(8.0)
                                .score(75.0)
                                .build(),
                        RankingItemDto.builder()
                                .playerName("Player2")
                                .gameId(2L)
                                .turnNumber(10)
                                .lifeAfter(6.0)
                                .score(50.0)
                                .build()
                ))
                .build();

        when(rankingService.getRanking(10)).thenReturn(response);

        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.top").value(10))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].player_name").value("Player1"))
                .andExpect(jsonPath("$.items[0].score").value(75.0));
    }

    @Test
    void getRanking_CustomTop_Returns200() throws Exception {
        RankingResponse response = RankingResponse.builder()
                .top(5)
                .items(List.of())
                .build();

        when(rankingService.getRanking(5)).thenReturn(response);

        mockMvc.perform(get("/api/ranking?top=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.top").value(5));
    }

    @Test
    void getRanking_EmptyRanking_ReturnsEmptyList() throws Exception {
        RankingResponse response = RankingResponse.builder()
                .top(10)
                .items(List.of())
                .build();

        when(rankingService.getRanking(10)).thenReturn(response);

        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void getRanking_OrderedByScoreDescThenTurnNumberAsc() throws Exception {
        RankingResponse response = RankingResponse.builder()
                .top(10)
                .items(List.of(
                        RankingItemDto.builder()
                                .playerName("HighScore")
                                .gameId(1L)
                                .turnNumber(3)
                                .lifeAfter(10.0)
                                .score(97.0)
                                .build(),
                        RankingItemDto.builder()
                                .playerName("MediumScore")
                                .gameId(2L)
                                .turnNumber(5)
                                .lifeAfter(7.0)
                                .score(65.0)
                                .build()
                ))
                .build();

        when(rankingService.getRanking(10)).thenReturn(response);

        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].score").value(97.0))
                .andExpect(jsonPath("$.items[1].score").value(65.0));
    }
}
