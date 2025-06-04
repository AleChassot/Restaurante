package com.example.SpringAula2.config;

import com.example.SpringAula2.model.Mesa;
import com.example.SpringAula2.repository.MesaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MesaInitializer {

    @Bean
    public CommandLineRunner carregarMesas(MesaRepository mesaRepository) {
        return args -> {
            if (mesaRepository.count() == 0) {
                mesaRepository.save(new Mesa(null, 1, 4, false));
                mesaRepository.save(new Mesa(null, 2, 2, false));
                mesaRepository.save(new Mesa(null, 3, 6, false));
            }
        };
    }
}