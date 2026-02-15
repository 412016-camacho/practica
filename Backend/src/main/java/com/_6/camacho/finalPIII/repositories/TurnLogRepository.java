package com._6.camacho.finalPIII.repositories;

import com._6.camacho.finalPIII.entities.TurnLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnLogRepository extends JpaRepository<TurnLog, Long> {

    List<TurnLog> findByGameIdOrderByTurnNumberAsc(Long gameId);

    List<TurnLog> findByGameIdAndTurnNumberBetweenOrderByTurnNumberAsc(Long gameId, Integer from, Integer to);
}
