package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    boolean existsByNumero(Integer numero);
}