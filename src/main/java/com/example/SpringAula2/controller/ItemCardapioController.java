package com.example.SpringAula2.controller;

import com.example.SpringAula2.exception.ResourceNotFoundException;
import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.service.ItemCardapioService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cardapio")
public class ItemCardapioController {

    @Autowired
    private ItemCardapioService service;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("nome").ascending());
        Page<ItemCardapio> pagina = service.listarPaginado(pageable);
        model.addAttribute("itens", pagina);
        return "cardapio/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("item", new ItemCardapio());
        model.addAttribute("categorias", ItemCardapio.Categoria.values());
        return "cardapio/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute ItemCardapio item, 
                        BindingResult result, 
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cardapio/form";
        }
        
        try {
            service.salvar(item);
            redirectAttributes.addFlashAttribute("mensagem", "Item salvo com sucesso!");
            return "redirect:/cardapio";
        } catch (Exception e) {
            result.rejectValue("nome", "error.item", "Erro ao salvar item: " + e.getMessage());
            return "cardapio/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ItemCardapio item = service.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do Cardápio", "id", id));
        model.addAttribute("item", item);
        model.addAttribute("categorias", ItemCardapio.Categoria.values());
        return "cardapio/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Item excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir item: " + e.getMessage());
        }
        return "redirect:/cardapio";
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=cardapio.pdf");

        List<ItemCardapio> itens = service.listarTodos();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Lista de Itens do Cardápio"));
        document.add(new Paragraph(" "));

        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);

        tabela.addCell(new PdfPCell(new Phrase("Nome")));
        tabela.addCell(new PdfPCell(new Phrase("Preço")));
        tabela.addCell(new PdfPCell(new Phrase("Categoria")));
        tabela.addCell(new PdfPCell(new Phrase("Status")));

        for (ItemCardapio item : itens) {
            tabela.addCell(item.getNome());
            tabela.addCell(item.getPreco() != null ? String.format("R$ %.2f", item.getPreco()) : "");
            tabela.addCell(item.getCategoria() != null ? item.getCategoria().getDescricao() : "");
            tabela.addCell(item.getAtivo() ? "Ativo" : "Inativo");
        }

        document.add(tabela);
        document.close();
    }

    @GetMapping("/buscar-por-preco")
    public String buscarPorPrecoForm() {
        return "cardapio/busca-preco";
    }

    @GetMapping("/buscar-por-preco/resultados")
    public String buscarPorPrecoResultados(@RequestParam Double min, 
                                         @RequestParam Double max, 
                                         Model model) {
        if (min > max) {
            model.addAttribute("erro", "O preço mínimo não pode ser maior que o preço máximo");
            return "cardapio/busca-preco";
        }
        
        model.addAttribute("itens", service.buscarPorPrecoEntre(min, max));
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        return "cardapio/busca-preco";
    }
}

