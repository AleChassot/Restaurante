package com.example.SpringAula2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.SpringAula2.model.Usuario;
import com.example.SpringAula2.repository.UsuarioRepository;

@Configuration
public class AdminUserInitializer {

    @Bean
    public CommandLineRunner initAdminUser(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // verifica se já existe o admin
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setSenha(passwordEncoder.encode("admin123")); // senha já encriptada
                admin.setRole("ADMIN");

                usuarioRepository.save(admin);

                System.out.println("Usuário admin criado com sucesso!");
            } else {
                System.out.println("Usuário admin já existe!");
            }
        };
    }
}
