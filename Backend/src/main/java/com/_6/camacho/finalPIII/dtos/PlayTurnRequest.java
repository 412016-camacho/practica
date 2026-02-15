package com._6.camacho.finalPIII.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayTurnRequest {

    @JsonProperty("dice_override")
    @Min(value = 1, message = "dice_override debe estar entre 1 y 6")
    @Max(value = 6, message = "dice_override debe estar entre 1 y 6")
    private Integer diceOverride;
}
