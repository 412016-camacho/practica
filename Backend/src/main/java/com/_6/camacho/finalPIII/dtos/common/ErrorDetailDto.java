package com._6.camacho.finalPIII.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetailDto {

    @JsonProperty("field")
    private String field;

    @JsonProperty("issue")
    private String issue;
}
