package com._6.camacho.finalPIII.dtos;

import com._6.camacho.finalPIII.entities.enums.CellType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellDto {

    @JsonProperty("index")
    private Integer index;

    @JsonProperty("type")
    private CellType type;

    @JsonProperty("value")
    private Double value;
}
