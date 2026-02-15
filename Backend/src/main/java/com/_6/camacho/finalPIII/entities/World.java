package com._6.camacho.finalPIII.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "worlds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class World {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "world", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Cell> cells = new ArrayList<>();

    public Map<Integer, Cell> getCellsAsMap() {
        Map<Integer, Cell> cellMap = new HashMap<>();
        for (Cell cell : cells) {
            cellMap.put(cell.getIndex(), cell);
        }
        return cellMap;
    }

    public void addCell(Cell cell) {
        cells.add(cell);
        cell.setWorld(this);
    }
}
