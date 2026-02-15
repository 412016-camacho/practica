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
public class TurnHistoryItemDto {

    @JsonProperty("turn_number")
    private Integer turnNumber;

    @JsonProperty("dice")
    private Integer dice;

    @JsonProperty("from_position")
    private Integer fromPosition;

    @JsonProperty("to_position")
    private Integer toPosition;

    @JsonProperty("delta_life")
    private Double deltaLife;

    @JsonProperty("life_after")
    private Double lifeAfter;

    @JsonProperty("status_after")
    private GameStatus statusAfter;
}
