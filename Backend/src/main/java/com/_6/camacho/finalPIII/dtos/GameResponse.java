package com._6.camacho.finalPIII.dtos;

import com._6.camacho.finalPIII.entities.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResponse {

    @JsonProperty("game_id")
    private Long gameId;

    @JsonProperty("world_id")
    private Long worldId;

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("life")
    private Double life;

    @JsonProperty("status")
    private GameStatus status;

    @JsonProperty("turn_number")
    private Integer turnNumber;
}
