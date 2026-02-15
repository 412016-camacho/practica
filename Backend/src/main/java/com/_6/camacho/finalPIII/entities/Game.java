package com._6.camacho.finalPIII.entities;

import com._6.camacho.finalPIII.entities.enums.GameStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "world_id", nullable = false)
    private World world;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private Double life;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @Column(name = "turn_number", nullable = false)
    private Integer turnNumber;

    @Column
    private Long seed;

    @Transient
    private Random random;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("turnNumber ASC")
    @Builder.Default
    private List<TurnLog> turnLogs = new ArrayList<>();

    public void addTurnLog(TurnLog turnLog) {
        turnLogs.add(turnLog);
        turnLog.setGame(this);
    }

    public Random getRandom() {
        if (random == null) {
            if (seed != null) {
                random = new Random(seed + turnNumber);
            } else {
                random = new Random();
            }
        }
        return random;
    }

    public int rollDice() {
        return getRandom().nextInt(6) + 1;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
