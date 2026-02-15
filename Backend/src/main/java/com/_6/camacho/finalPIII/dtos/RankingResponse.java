package com._6.camacho.finalPIII.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingResponse {

    @JsonProperty("top")
    private Integer top;

    @JsonProperty("items")
    private List<RankingItemDto> items;
}
