package com._6.camacho.finalPIII.services.impl;

import com._6.camacho.finalPIII.dtos.CellDto;
import com._6.camacho.finalPIII.dtos.WorldDetailDto;
import com._6.camacho.finalPIII.dtos.WorldListDto;
import com._6.camacho.finalPIII.entities.World;
import com._6.camacho.finalPIII.repositories.WorldRepository;
import com._6.camacho.finalPIII.services.WorldService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorldServiceImpl implements WorldService {

    private final WorldRepository worldRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WorldListDto> getAllWorlds() {
        return worldRepository.findAll().stream()
                .map(world -> modelMapper.map(world, WorldListDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorldDetailDto getWorldById(Long worldId) {
        World world = worldRepository.findById(worldId)
                .orElseThrow(() -> new EntityNotFoundException("World no encontrado con id: " + worldId));

        List<CellDto> cellDtos = world.getCells().stream()
                .sorted(Comparator.comparing(cell -> cell.getIndex()))
                .map(cell -> CellDto.builder()
                        .index(cell.getIndex())
                        .type(cell.getType())
                        .value(cell.getValue())
                        .build())
                .collect(Collectors.toList());

        return WorldDetailDto.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .cells(cellDtos)
                .build();
    }
}
