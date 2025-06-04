package com.example.SpringAula2.service;

import com.example.SpringAula2.exception.ResourceNotFoundException;
import com.example.SpringAula2.model.ItemCardapio;
import com.example.SpringAula2.repository.ItemCardapioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemCardapioService {

    @Autowired
    private ItemCardapioRepository repository;

    public List<ItemCardapio> listarTodos() {
        log.info("Listando todos os itens do cardápio");
        return repository.findAll();
    }

    public Page<ItemCardapio> listarPaginado(Pageable pageable) {
        log.info("Listando itens do cardápio paginados");
        return repository.findAll(pageable);
    }

    public Optional<ItemCardapio> buscarPorId(Long id) {
        log.info("Buscando item do cardápio por ID: {}", id);
        return repository.findById(id);
    }

    @Transactional
    public ItemCardapio salvar(ItemCardapio item) {
        log.info("Salvando item do cardápio: {}", item.getNome());
        
        // Verifica se já existe um item com o mesmo nome (exceto se for o mesmo item)
        if (item.getId() == null) {
            if (repository.existsByNome(item.getNome())) {
                throw new IllegalArgumentException("Já existe um item com este nome");
            }
        } else {
            Optional<ItemCardapio> itemExistente = repository.findById(item.getId());
            if (itemExistente.isPresent() && !itemExistente.get().getNome().equals(item.getNome())) {
                if (repository.existsByNome(item.getNome())) {
                    throw new IllegalArgumentException("Já existe um item com este nome");
                }
            }
        }
        
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo item do cardápio com ID: {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Item do Cardápio", "id", id);
        }
        repository.deleteById(id);
    }

    public List<ItemCardapio> buscarPorPrecoEntre(Double min, Double max) {
        log.info("Buscando itens do cardápio com preço entre {} e {}", min, max);
        if (min == null || max == null) {
            throw new IllegalArgumentException("Preço mínimo e máximo são obrigatórios");
        }
        if (min > max) {
            throw new IllegalArgumentException("Preço mínimo não pode ser maior que o preço máximo");
        }
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Preços não podem ser negativos");
        }
        return repository.findByPrecoBetween(min, max);
    }
}

