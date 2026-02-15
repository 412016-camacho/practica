package com._6.camacho.finalPIII.repositories;

import com._6.camacho.finalPIII.entities.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldRepository extends JpaRepository<World, Long> {
}
