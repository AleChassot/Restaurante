package com.example.SpringAula2.service;

import com.example.SpringAula2.exception.ResourceNotFoundException;
import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.model.PedidoItem;
import com.example.SpringAula2.model.StatusPagamento;
import com.example.SpringAula2.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Value("${app.taxa-servico:0.10}")
    private double taxaServico;

    public Page<Pedido> listarPaginado(Pageable pageable) {
        log.info("Listando pedidos paginados");
        return pedidoRepository.findAllWithItens(pageable);
    }

    public Page<Pedido> listarPendentesPaginado(Pageable pageable) {
        log.info("Listando pedidos pendentes paginados");
        return pedidoRepository.findByStatusPagamentoNotWithItens(StatusPagamento.PAGO, pageable);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        log.info("Salvando pedido para cliente: {}", pedido.getCliente());
        validarPedido(pedido);
        calcularValorTotal(pedido);
        return pedidoRepository.save(pedido);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }
        if (pedido.getPedidoItens() == null || pedido.getPedidoItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter pelo menos um item");
        }
        pedido.getPedidoItens().forEach(this::validarPedidoItem);
    }

    private void validarPedidoItem(PedidoItem item) {
        if (item.getItem() == null) {
            throw new IllegalArgumentException("Item do pedido não pode ser nulo");
        }
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
    }

    public void calcularValorTotal(Pedido pedido) {
        log.debug("Calculando valor total do pedido");
        double total = 0.0;
        if (pedido.getPedidoItens() != null) {
            for (PedidoItem pi : pedido.getPedidoItens()) {
                if (pi.getItem() != null && pi.getQuantidade() != null) {
                    total += pi.getItem().getPreco() * pi.getQuantidade();
                }
            }
        }
        if (Boolean.TRUE.equals(pedido.getComServico())) {
            total = total * (1 + taxaServico);
            log.debug("Taxa de serviço aplicada: {}%", taxaServico * 100);
        }
        pedido.setValorTotal(total);
    }

    public Pedido buscarPorId(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido p = pedido.get();
            // Força o carregamento dos itens
            if (p.getPedidoItens() != null) {
                p.getPedidoItens().size();
            }
            return p;
        }
        throw new ResourceNotFoundException("Pedido", "id", id);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo pedido com ID: {}", id);
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido", "id", id);
        }
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(String cliente) {
        log.info("Buscando pedidos do cliente: {}", cliente);
        return pedidoRepository.findByClienteContainingIgnoreCaseWithItens(cliente);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorData(LocalDate inicio, LocalDate fim) {
        log.info("Buscando pedidos entre {} e {}", inicio, fim);
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial deve ser anterior à data final");
        }
        return pedidoRepository.findByDataBetween(inicio, fim);
    }
}

