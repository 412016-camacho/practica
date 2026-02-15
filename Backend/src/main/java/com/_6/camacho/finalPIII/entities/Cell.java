package com._6.camacho.finalPIII.entities;

import com._6.camacho.finalPIII.entities.enums.CellType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cells")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cell_index", nullable = false)
    private Integer index;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CellType type;

    @Column(name = "cell_value")
    private Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_id", nullable = false)
    private World world;
}
