package com.example.SpringAula2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Representa uma mesa do restaurante.
 */
@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O número da mesa é obrigatório")
    @Min(value = 1, message = "O número da mesa deve ser maior que zero")
    @Column(unique = true)
    private Integer numero;

    @NotNull(message = "A capacidade da mesa é obrigatória")
    @Min(value = 1, message = "A capacidade da mesa deve ser maior que zero")
    private Integer capacidade;

    @Builder.Default
    private Boolean ocupada = false;

    /**
     * Verifica se a mesa está disponível.
     * @return true se a mesa estiver disponível, false caso contrário
     */
    public boolean isDisponivel() {
        return !ocupada;
    }

    /**
     * Ocupa a mesa.
     */
    public void ocupar() {
        this.ocupada = true;
    }

    /**
     * Libera a mesa.
     */
    public void liberar() {
        this.ocupada = false;
    }
}