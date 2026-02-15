package com._6.camacho.finalPIII.controllers;

import com._6.camacho.finalPIII.dtos.RankingResponse;
import com._6.camacho.finalPIII.services.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<RankingResponse> getRanking(
            @RequestParam(required = false, defaultValue = "10") Integer top) {
        RankingResponse response = rankingService.getRanking(top);
        return ResponseEntity.ok(response);
    }
}
