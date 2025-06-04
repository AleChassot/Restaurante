package com.example.SpringAula2.controller;

import com.example.SpringAula2.exception.ResourceNotFoundException;
import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.model.PedidoItem;
import com.example.SpringAula2.model.StatusPagamento;
import com.example.SpringAula2.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/novo/{pedidoId}")
    public String novo(@PathVariable Long pedidoId, Model model) {
        log.info("Carregando formulário de pagamento para pedido: {}", pedidoId);
        Pedido pedido = pedidoService.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new ResourceNotFoundException("Pedido", "id", pedidoId);
        }
        
        // Garante que os itens do pedido estejam carregados
        if (pedido.getPedidoItens() == null || pedido.getPedidoItens().isEmpty()) {
            log.warn("Pedido {} não possui itens", pedidoId);
            throw new ResourceNotFoundException("Itens do Pedido", "pedidoId", pedidoId);
        }
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("formasPagamento", Pedido.FormaPagamento.values());
        return "pagamentos/form";
    }

    @PostMapping("/processar")
    public String processar(@RequestParam Long pedidoId,
                            @RequestParam(required = false) Map<String, String> selecionado,
                            @RequestParam(required = false) Integer[] quantidadePaga,
                            @RequestParam(required = false) String formaPagamento,
                            @RequestParam(required = false) Double valorRecebido,
                            @RequestParam(required = false) Boolean comServico,
                            RedirectAttributes redirectAttributes) {

        try {
            log.info("=== INÍCIO DO PROCESSAMENTO DE PAGAMENTO ===");
            log.info("Pedido ID: {}", pedidoId);
            log.info("Itens selecionados: {}", selecionado);
            log.info("Quantidades: {}", quantidadePaga != null ? Arrays.toString(quantidadePaga) : "null");
            log.info("Forma de pagamento: {}", formaPagamento);
            log.info("Valor recebido: {}", valorRecebido);
            log.info("Com serviço: {}", comServico);

            if (pedidoId == null) {
                log.error("Pedido ID não fornecido");
                return redirecionarComErro("ID do pedido não fornecido", null, redirectAttributes);
            }

            if (selecionado == null || quantidadePaga == null) {
                log.error("Dados inválidos - selecionado: {}, quantidadePaga: {}", 
                         selecionado != null ? selecionado.size() : "null", 
                         quantidadePaga != null ? quantidadePaga.length : "null");
                return redirecionarComErro("Itens selecionados ou quantidades inválidas", pedidoId, redirectAttributes);
            }

            log.info("Tamanho do map selecionado: {}", selecionado.size());
            log.info("Tamanho do array quantidadePaga: {}", quantidadePaga.length);

            if (formaPagamento == null || formaPagamento.trim().isEmpty()) {
                log.error("Forma de pagamento não selecionada");
                return redirecionarComErro("Forma de pagamento não selecionada", pedidoId, redirectAttributes);
            }

            boolean temItemSelecionado = false;
            for (Map.Entry<String, String> entry : selecionado.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.startsWith("selecionado[") && "true".equals(value)) {
                    temItemSelecionado = true;
                    break;
                }
            }

            if (!temItemSelecionado) {
                log.error("Nenhum item selecionado para pagamento");
                return redirecionarComErro("Nenhum item selecionado para pagamento", pedidoId, redirectAttributes);
            }

            Pedido pedido = pedidoService.buscarPorId(pedidoId);
            if (pedido == null) {
                log.error("Pedido não encontrado: {}", pedidoId);
                return redirecionarComErro("Pedido não encontrado", pedidoId, redirectAttributes);
            }

            log.info("Total de itens no pedido: {}", pedido.getPedidoItens().size());

            double totalPago = 0;
            for (Map.Entry<String, String> entry : selecionado.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.startsWith("selecionado[") && "true".equals(value)) {
                    String[] parts = key.split("\\[|\\]");
                    int index = Integer.parseInt(parts[1]);
                    if (index >= pedido.getPedidoItens().size()) {
                        log.error("Índice de item inválido: {} (total de itens: {})", 
                                index, pedido.getPedidoItens().size());
                        return redirecionarComErro("Índice de item inválido", pedidoId, redirectAttributes);
                    }

                    PedidoItem item = pedido.getPedidoItens().get(index);
                    log.info("Item encontrado: {} (quantidade: {}, quantidade paga: {})", 
                            item.getItem().getNome(), item.getQuantidade(), quantidadePaga[index]);
                    
                    if (quantidadePaga[index] > item.getQuantidade()) {
                        log.error("Quantidade inválida para o item {}: {} > {}", 
                                item.getItem().getNome(), quantidadePaga[index], item.getQuantidade());
                        return redirecionarComErro("Quantidade inválida para o item: " + item.getItem().getNome(), 
                                pedidoId, redirectAttributes);
                    }

                    totalPago += item.getItem().getPreco() * quantidadePaga[index];
                    log.info("Subtotal do item: {}", item.getItem().getPreco() * quantidadePaga[index]);
                }
            }

            log.info("Total pago antes do serviço: {}", totalPago);

            if (Boolean.TRUE.equals(comServico)) {
                totalPago *= 1.10;
                pedido.setComServico(true);
                log.info("Total pago com serviço: {}", totalPago);
            }

            // Validação forma de pagamento
            Optional<Pedido.FormaPagamento> forma = Arrays.stream(Pedido.FormaPagamento.values())
                    .filter(f -> f.name().equalsIgnoreCase(formaPagamento))
                    .findFirst();

            if (forma.isEmpty()) {
                log.error("Forma de pagamento inválida: {}", formaPagamento);
                return redirecionarComErro("Forma de pagamento inválida", pedidoId, redirectAttributes);
            }

            // Dinheiro: verifica valor recebido
            if (forma.get() == Pedido.FormaPagamento.DINHEIRO) {
                if (valorRecebido == null || valorRecebido < totalPago) {
                    log.error("Valor recebido insuficiente: {} < {}", valorRecebido, totalPago);
                    return redirecionarComErro("Valor recebido insuficiente", pedidoId, redirectAttributes);
                }
            }

            // Atualiza o valor pago e forma de pagamento
            pedido.setValorPago((pedido.getValorPago() != null ? pedido.getValorPago() : 0) + totalPago);
            pedido.setFormaPagamento(forma.get());

            // Calcula o total do pedido original
            double totalPedido = pedido.getPedidoItens().stream()
                    .mapToDouble(pi -> pi.getItem().getPreco() * pi.getQuantidade())
                    .sum();

            if (pedido.getComServico()) {
                totalPedido *= 1.10;
            }

            log.info("Total do pedido: {}, Total pago: {}", totalPedido, pedido.getValorPago());

            // Atualiza o status do pedido
            if (pedido.getValorPago() >= totalPedido) {
                pedido.setStatusPagamento(StatusPagamento.PAGO);
                if (pedido.getMesa() != null) {
                    pedido.getMesa().setOcupada(false);
                }
                log.info("Pedido {} marcado como PAGO", pedidoId);
            } else if (totalPago > 0) {
                pedido.setStatusPagamento(StatusPagamento.PARCIAL);
                log.info("Pedido {} marcado como PARCIAL", pedidoId);
            }

            pedidoService.salvar(pedido);
            log.info("Pagamento processado com sucesso. Total pago: {}", totalPago);
            redirectAttributes.addFlashAttribute("mensagem", "Pagamento processado com sucesso!");
            return "redirect:/pedidos";

        } catch (Exception e) {
            log.error("Erro ao processar pagamento", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar pagamento: " + e.getMessage());
            return "redirect:/pagamentos/novo/" + pedidoId;
        }
    }

    private String redirecionarComErro(String mensagem, Long pedidoId, RedirectAttributes redirectAttributes) {
        log.error(mensagem);
        redirectAttributes.addFlashAttribute("erro", mensagem);
        return "redirect:/pagamentos/novo/" + pedidoId;
    }
}
