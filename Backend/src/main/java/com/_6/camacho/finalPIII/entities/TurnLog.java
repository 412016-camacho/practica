package com._6.camacho.finalPIII.entities;

import com._6.camacho.finalPIII.entities.enums.CellType;
import com._6.camacho.finalPIII.entities.enums.GameStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "turn_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turn_number", nullable = false)
    private Integer turnNumber;

    @Column(name = "from_position", nullable = false)
    private Integer fromPosition;

    @Column(name = "to_position", nullable = false)
    private Integer toPosition;

    @Column(nullable = false)
    private Integer dice;

    @Enumerated(EnumType.STRING)
    @Column(name = "cell_type", nullable = false)
    private CellType cellType;

    @Column(name = "cell_value")
    private Double cellValue;

    @Column(name = "delta_life", nullable = false)
    private Double deltaLife;

    @Column(name = "life_after", nullable = false)
    private Double lifeAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_after", nullable = false)
    private GameStatus statusAfter;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
}
