package com._6.camacho.finalPIII.repositories;

import com._6.camacho.finalPIII.entities.Game;
import com._6.camacho.finalPIII.entities.enums.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByStatus(GameStatus status);

    @Query("SELECT g FROM Game g WHERE g.status = 'WON' ORDER BY (g.life * 10 - g.turnNumber) DESC, g.turnNumber ASC")
    List<Game> findWonGamesOrderedByScore();
}
