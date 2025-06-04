package com.example.SpringAula2.service;

import com.example.SpringAula2.exception.ResourceNotFoundException;
import com.example.SpringAula2.model.Mesa;
import com.example.SpringAula2.repository.MesaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> listarTodas() {
        log.info("Listando todas as mesas");
        return mesaRepository.findAll();
    }

    public Mesa buscarPorId(Long id) {
        log.info("Buscando mesa por ID: {}", id);
        Optional<Mesa> mesa = mesaRepository.findById(id);
        return mesa.orElseThrow(() -> new ResourceNotFoundException("Mesa", "id", id));
    }

    @Transactional
    public Mesa salvar(Mesa mesa) {
        log.info("Salvando mesa: {}", mesa.getNumero());
        validarMesa(mesa);
        return mesaRepository.save(mesa);
    }

    private void validarMesa(Mesa mesa) {
        if (mesa.getNumero() == null || mesa.getNumero() <= 0) {
            throw new IllegalArgumentException("Número da mesa deve ser maior que zero");
        }
        if (mesa.getCapacidade() == null || mesa.getCapacidade() <= 0) {
            throw new IllegalArgumentException("Capacidade da mesa deve ser maior que zero");
        }
        // Verificar se já existe uma mesa com o mesmo número
        if (mesa.getId() == null && mesaRepository.existsByNumero(mesa.getNumero())) {
            throw new IllegalArgumentException("Já existe uma mesa com este número");
        }
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo mesa com ID: {}", id);
        if (!mesaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa", "id", id);
        }
        mesaRepository.deleteById(id);
    }
}