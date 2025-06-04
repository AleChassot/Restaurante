package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.ItemCardapio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Long> {
    @Query("SELECT i FROM ItemCardapio i WHERE i.preco BETWEEN :min AND :max")
    List<ItemCardapio> findByPrecoBetween(@Param("min") Double min, @Param("max") Double max);

    Page<ItemCardapio> findAll(Pageable pageable);

    boolean existsByNome(String nome);
}

