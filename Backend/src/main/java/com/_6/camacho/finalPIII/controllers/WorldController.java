package com._6.camacho.finalPIII.controllers;

import com._6.camacho.finalPIII.dtos.WorldDetailDto;
import com._6.camacho.finalPIII.dtos.WorldListDto;
import com._6.camacho.finalPIII.services.WorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worlds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorldController {

    private final WorldService worldService;

    @GetMapping
    public ResponseEntity<List<WorldListDto>> getAllWorlds() {
        List<WorldListDto> worlds = worldService.getAllWorlds();
        return ResponseEntity.ok(worlds);
    }

    @GetMapping("/{worldId}")
    public ResponseEntity<WorldDetailDto> getWorldById(@PathVariable Long worldId) {
        WorldDetailDto world = worldService.getWorldById(worldId);
        return ResponseEntity.ok(world);
    }
}
