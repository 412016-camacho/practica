package com._6.camacho.finalPIII.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingItemDto {

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("game_id")
    private Long gameId;

    @JsonProperty("turn_number")
    private Integer turnNumber;

    @JsonProperty("life_after")
    private Double lifeAfter;

    @JsonProperty("score")
    private Double score;
}
