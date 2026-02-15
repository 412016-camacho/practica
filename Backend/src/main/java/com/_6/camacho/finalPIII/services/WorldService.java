package com._6.camacho.finalPIII.services;

import com._6.camacho.finalPIII.dtos.WorldDetailDto;
import com._6.camacho.finalPIII.dtos.WorldListDto;

import java.util.List;

public interface WorldService {

    List<WorldListDto> getAllWorlds();

    WorldDetailDto getWorldById(Long worldId);
}
