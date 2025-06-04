package com.example.SpringAula2.controller;

import com.example.SpringAula2.model.Mesa;
import com.example.SpringAula2.service.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mesas", mesaService.listarTodas());
        return "mesa-list";
    }

    @GetMapping("/nova")
    public String novaMesa(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesa-form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Mesa mesa) {
        mesaService.salvar(mesa);
        return "redirect:/mesas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Mesa mesa = mesaService.buscarPorId(id);
        model.addAttribute("mesa", mesa);
        return "mesa-form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        mesaService.excluir(id);
        return "redirect:/mesas";
    }
}