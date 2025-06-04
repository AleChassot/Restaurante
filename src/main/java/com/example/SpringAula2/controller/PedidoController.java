package com.example.SpringAula2.controller;

import com.example.SpringAula2.exception.ResourceNotFoundException;
import com.example.SpringAula2.model.Pedido;
import com.example.SpringAula2.model.PedidoItem;
import com.example.SpringAula2.model.StatusPagamento;
import com.example.SpringAula2.service.ItemCardapioService;
import com.example.SpringAula2.service.MesaService;
import com.example.SpringAula2.service.PedidoService;
import com.example.SpringAula2.service.UsuarioService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ItemCardapioService itemCardapioService;


@Autowired
private MesaService mesaService;
@Autowired
private UsuarioService usuarioService;


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) Boolean apenasPendentes,
                        Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Pedido> pedidos;
        
        if (Boolean.TRUE.equals(apenasPendentes)) {
            pedidos = pedidoService.listarPendentesPaginado(pageable);
            model.addAttribute("filtroPendentes", true);
        } else {
            pedidos = pedidoService.listarPaginado(pageable);
            model.addAttribute("filtroPendentes", false);
        }
        
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", itemCardapioService.listarTodos());
        model.addAttribute("mesas", mesaService.listarTodas());



        return "pedidos/form";
    }

@PostMapping("/salvar")
public String salvar(@ModelAttribute Pedido pedido, @RequestParam Map<String, String> params) {
    // Define a data atual antes de salvar
    pedido.setData(LocalDate.now());
    
    List<PedidoItem> itensSelecionados = new ArrayList<>();
    for (int i = 0; ; i++) {
        String selecionado = params.get("pedidoItens[" + i + "].selecionado");
        String itemIdStr = params.get("pedidoItens[" + i + "].item.id");
        String quantidadeStr = params.get("pedidoItens[" + i + "].quantidade");
        if (itemIdStr == null) break;
        if ("true".equals(selecionado) && quantidadeStr != null && Integer.parseInt(quantidadeStr) > 0) {
            PedidoItem pi = new PedidoItem();
            pi.setItem(itemCardapioService.buscarPorId(Long.parseLong(itemIdStr))
                    .orElseThrow(() -> new ResourceNotFoundException("Item do Cardápio", "id", itemIdStr)));
            pi.setQuantidade(Integer.parseInt(quantidadeStr));
            pi.setPedido(pedido);
            itensSelecionados.add(pi);
        }
    }
    pedido.setPedidoItens(itensSelecionados);
    pedidoService.salvar(pedido);
    return "redirect:/pedidos";
}

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id);
        model.addAttribute("pedido", pedido);
        model.addAttribute("itens", itemCardapioService.listarTodos());
        return "pedidos/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
        return "redirect:/pedidos";
    }

    @GetMapping("/filtro")
    public String filtroForm() {
        return "pedidos/busca";
    }



@GetMapping("/pagamento/{id}")
public String pagamento(@PathVariable Long id) {
    return "redirect:/pagamentos/novo/" + id;
}









@PostMapping("/pagamento")
public String processarPagamento(
        @ModelAttribute Pedido pedido,
        @RequestParam(value = "quantidadePaga", required = false) int[] quantidadesPagas,
        @RequestParam("formaPagamento") String formaPagamento,
        @RequestParam(value = "valorRecebido", required = false) Double valorRecebido,
        RedirectAttributes redirectAttributes
) {
    try {
        if (quantidadesPagas == null) {
            redirectAttributes.addFlashAttribute("erro", "Nenhum item selecionado para pagamento");
            return "redirect:/pedidos/pagamento/" + pedido.getId();
        }

        Pedido pedidoOriginal = pedidoService.buscarPorId(pedido.getId());
        if (pedidoOriginal == null) {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado");
            return "redirect:/pedidos";
        }

        double totalPago = 0.0;
        List<PedidoItem> itens = pedidoOriginal.getPedidoItens();
        for (int i = 0; i < itens.size(); i++) {
            int qtdPaga = quantidadesPagas[i];
            double preco = itens.get(i).getItem().getPreco();
            totalPago += qtdPaga * preco;
        }

        // Aplica taxa de serviço se selecionado
        if (Boolean.TRUE.equals(pedido.getComServico())) {
            totalPago *= 1.10;
            pedidoOriginal.setComServico(true);
        } else {
            pedidoOriginal.setComServico(false);
        }

        // Atualiza valores do pedido
        pedidoOriginal.setValorPago(totalPago);
        pedidoOriginal.setFormaPagamento(Pedido.FormaPagamento.valueOf(formaPagamento));

        // Calcula o total do pedido
        double totalPedido = itens.stream()
                .mapToDouble(pi -> pi.getItem().getPreco() * pi.getQuantidade())
                .sum();
        if (pedidoOriginal.getComServico()) {
            totalPedido *= 1.10;
        }

        // Atualiza status do pagamento
        if (totalPago >= totalPedido) {
            pedidoOriginal.setStatusPagamento(StatusPagamento.PAGO);
            // Libera a mesa se o pedido estiver pago
            if (pedidoOriginal.getMesa() != null) {
                pedidoOriginal.getMesa().setOcupada(false);
            }
        } else if (totalPago > 0) {
            pedidoOriginal.setStatusPagamento(StatusPagamento.PARCIAL);
        } else {
            pedidoOriginal.setStatusPagamento(StatusPagamento.PENDENTE);
        }

        pedidoService.salvar(pedidoOriginal);
        redirectAttributes.addFlashAttribute("mensagem", "Pagamento processado com sucesso!");
        return "redirect:/pedidos";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("erro", "Erro ao processar pagamento: " + e.getMessage());
        return "redirect:/pedidos/pagamento/" + pedido.getId();
    }
}
















    @GetMapping("/filtro/resultados")
    public String buscar(@RequestParam(required = false) String cliente,
                         @RequestParam(required = false) String inicio,
                         @RequestParam(required = false) String fim,
                         Model model) {
        try {
            List<Pedido> pedidos;
            double total = 0.0;

            model.addAttribute("clienteParam", cliente != null ? cliente : "");
            model.addAttribute("inicioParam", inicio != null ? inicio : "");
            model.addAttribute("fimParam", fim != null ? fim : "");

            boolean filtroPorClienteAtivo = (cliente != null && !cliente.isBlank());
            boolean filtroPorDataAtivo = (inicio != null && !inicio.isBlank() && fim != null && !fim.isBlank());

            if (!filtroPorClienteAtivo && !filtroPorDataAtivo) {
                model.addAttribute("erro", "Informe o nome do cliente ou um intervalo de datas válido.");
                return "pedidos/busca";
            }

            if (filtroPorClienteAtivo) {
                pedidos = pedidoService.buscarPorCliente(cliente);
            } else if (filtroPorDataAtivo) {
                LocalDate dtInicio = LocalDate.parse(inicio, DATE_FORMATTER);
                LocalDate dtFim = LocalDate.parse(fim, DATE_FORMATTER);
                pedidos = pedidoService.buscarPorData(dtInicio, dtFim);
            } else {
                pedidos = Collections.emptyList();
                model.addAttribute("erro", "Nenhum critério de filtro válido fornecido.");
                return "pedidos/busca";
            }

            total = pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();

            model.addAttribute("pedidos", pedidos);
            model.addAttribute("total", total);
        } catch (DateTimeParseException e) {
            model.addAttribute("erro", "Datas inválidas. Formato esperado: AAAA-MM-DD.");
            model.addAttribute("clienteParam", cliente != null ? cliente : "");
            model.addAttribute("inicioParam", inicio != null ? inicio : "");
            model.addAttribute("fimParam", fim != null ? fim : "");
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar pedidos: " + e.getMessage());
            model.addAttribute("clienteParam", cliente != null ? cliente : "");
            model.addAttribute("inicioParam", inicio != null ? inicio : "");
            model.addAttribute("fimParam", fim != null ? fim : "");
        }
        return "pedidos/busca";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(@RequestParam(required = false) String cliente,
                            @RequestParam(required = false) String inicio,
                            @RequestParam(required = false) String fim,
                            HttpServletResponse response) throws IOException, DocumentException {
        List<Pedido> pedidos;
        double total;

        try {
            boolean filtroPorClienteAtivo = (cliente != null && !cliente.isBlank());
            boolean filtroPorDataAtivo = (inicio != null && !inicio.isBlank() && fim != null && !fim.isBlank());

            if (filtroPorClienteAtivo) {
                pedidos = pedidoService.buscarPorCliente(cliente);
            } else if (filtroPorDataAtivo) {
                LocalDate dtInicio = LocalDate.parse(inicio, DATE_FORMATTER);
                LocalDate dtFim = LocalDate.parse(fim, DATE_FORMATTER);
                pedidos = pedidoService.buscarPorData(dtInicio, dtFim);
            } else {
                pedidos = Collections.emptyList();
            }

            total = pedidos.stream().mapToDouble(Pedido::getValorTotal).sum();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=relatorio_pedidos.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            document.add(new Paragraph("Relatório de Pedidos"));
            if (filtroPorClienteAtivo) {
                document.add(new Paragraph("Cliente: " + cliente));
            } else if (filtroPorDataAtivo) {
                document.add(new Paragraph("De: " + inicio + " Até: " + fim));
            } else {
                document.add(new Paragraph("Filtro: Nenhum critério de filtro válido para o relatório."));
            }
            document.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(5);
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(10f);
            tabela.setSpacingAfter(10f);

            tabela.addCell("ID");
            tabela.addCell("Cliente");
            tabela.addCell("Data");
            tabela.addCell("Valor Total");
            tabela.addCell("Itens");

            for (Pedido p : pedidos) {
                tabela.addCell(p.getId().toString());
                tabela.addCell(p.getCliente());
                tabela.addCell(p.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                tabela.addCell(String.format("R$ %.2f", p.getValorTotal()));

                // Adicionar os itens do pedido no PDF
StringBuilder itensPdf = new StringBuilder();
for (int i = 0; i < p.getPedidoItens().size(); i++) {
    PedidoItem pi = p.getPedidoItens().get(i);
    itensPdf.append(pi.getItem().getNome())
            .append(" (x").append(pi.getQuantidade()).append(")");
    if (i < p.getPedidoItens().size() - 1) {
        itensPdf.append(", ");
    }
}
tabela.addCell(itensPdf.toString());
            }

            document.add(tabela);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Geral: R$ " + String.format("%.2f", total)));

            document.close();

        } catch (DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datas inválidas para exportação. Formato esperado: AAAA-MM-DD.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao gerar PDF: " + e.getMessage());
        }
    }
}