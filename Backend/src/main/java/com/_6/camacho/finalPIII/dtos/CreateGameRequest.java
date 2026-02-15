package com._6.camacho.finalPIII.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGameRequest {

    @JsonProperty("world_id")
    @NotNull(message = "world_id es obligatorio")
    private Long worldId;

    @JsonProperty("initial_life")
    @NotNull(message = "initial_life es obligatorio")
    @Min(value = 1, message = "initial_life debe estar entre 1 y 10")
    @Max(value = 10, message = "initial_life debe estar entre 1 y 10")
    private Integer initialLife;

    @JsonProperty("player_name")
    @NotBlank(message = "player_name es obligatorio")
    @Size(min = 3, max = 20, message = "player_name debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^\\S.*\\S$|^\\S$", message = "player_name no puede tener espacios al inicio o al final")
    private String playerName;

    @JsonProperty("seed")
    private Long seed;
}
