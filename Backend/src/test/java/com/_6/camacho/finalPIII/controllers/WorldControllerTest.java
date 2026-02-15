package com._6.camacho.finalPIII.controllers;

import com._6.camacho.finalPIII.dtos.CellDto;
import com._6.camacho.finalPIII.dtos.WorldDetailDto;
import com._6.camacho.finalPIII.dtos.WorldListDto;
import com._6.camacho.finalPIII.entities.enums.CellType;
import com._6.camacho.finalPIII.services.WorldService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorldController.class)
class WorldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorldService worldService;

    @Test
    void getAllWorlds_ReturnsListOfWorlds() throws Exception {
        List<WorldListDto> worlds = List.of(
                WorldListDto.builder()
                        .id(1L)
                        .name("Bosque Encantado")
                        .description("Un bosque misterioso")
                        .build(),
                WorldListDto.builder()
                        .id(2L)
                        .name("Caverna Oscura")
                        .description("Una caverna peligrosa")
                        .build()
        );

        when(worldService.getAllWorlds()).thenReturn(worlds);

        mockMvc.perform(get("/api/worlds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Bosque Encantado"));
    }

    @Test
    void getWorldById_ExistingWorld_ReturnsWorldDetail() throws Exception {
        WorldDetailDto world = WorldDetailDto.builder()
                .id(1L)
                .name("Bosque Encantado")
                .description("Un bosque misterioso")
                .cells(List.of(
                        CellDto.builder()
                                .index(0)
                                .type(CellType.EMPTY)
                                .build(),
                        CellDto.builder()
                                .index(1)
                                .type(CellType.MONSTER)
                                .value(1.0)
                                .build()
                ))
                .build();

        when(worldService.getWorldById(1L)).thenReturn(world);

        mockMvc.perform(get("/api/worlds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bosque Encantado"))
                .andExpect(jsonPath("$.cells").isArray())
                .andExpect(jsonPath("$.cells[0].type").value("EMPTY"))
                .andExpect(jsonPath("$.cells[1].type").value("MONSTER"));
    }

    @Test
    void getWorldById_NonExistingWorld_Returns404() throws Exception {
        when(worldService.getWorldById(999L))
                .thenThrow(new EntityNotFoundException("World no encontrado"));

        mockMvc.perform(get("/api/worlds/999"))
                .andExpect(status().isNotFound());
    }
}
