package com.example.SpringAula2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String cliente;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    private Double valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoItem> pedidoItens = new ArrayList<>();

    @Builder.Default
    private Boolean comServico = false;

    private Double valorPago;

    @NotNull(message = "O status do pagamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @ManyToOne
    @JoinColumn(name = "mesa_id")
    @NotNull(message = "A mesa é obrigatória")
    private Mesa mesa;

    public void adicionarItem(PedidoItem item) {
        pedidoItens.add(item);
        item.setPedido(this);
    }

    public void removerItem(PedidoItem item) {
        pedidoItens.remove(item);
        item.setPedido(null);
    }

    // Getter e Setter para data
    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public enum FormaPagamento {
        DINHEIRO("Dinheiro"),
        DEBITO("Cartão de Débito"),
        CREDITO("Cartão de Crédito"),
        PIX("PIX");

        private final String descricao;

        FormaPagamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}