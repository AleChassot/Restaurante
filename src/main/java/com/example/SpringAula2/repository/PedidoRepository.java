package com.example.SpringAula2.repository;

import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.pedidoItens pi LEFT JOIN FETCH pi.item")
    Page<Pedido> findAllWithItens(Pageable pageable);

    List<Pedido> findByClienteContainingIgnoreCase(String cliente);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.pedidoItens pi LEFT JOIN FETCH pi.item WHERE p.data BETWEEN :inicio AND :fim")
    List<Pedido> findByDataBetweenWithItens(LocalDate inicio, LocalDate fim);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.pedidoItens pi LEFT JOIN FETCH pi.item WHERE LOWER(p.cliente) LIKE LOWER(CONCAT('%', :cliente, '%'))")
    List<Pedido> findByClienteContainingIgnoreCaseWithItens(String cliente);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.pedidoItens pi LEFT JOIN FETCH pi.item WHERE p.statusPagamento != :status")
    Page<Pedido> findByStatusPagamentoNotWithItens(StatusPagamento status, Pageable pageable);

    List<Pedido> findByDataBetween(LocalDate inicio, LocalDate fim);
}