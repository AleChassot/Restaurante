package com.example.SpringAula2.service;
import com.example.SpringAula2.model.Usuario;
import com.example.SpringAula2.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Primary
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Salvar novo usuário ou atualizar existente
    public void salvar(Usuario usuario) {
        // Verifica se é novo usuário ou se a senha foi alterada antes de recriptografar
        if (usuario.getId() == null || !usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        usuarioRepository.save(usuario);
    }

    // Buscar por username (para autenticação)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getSenha())
                .roles(usuario.getRole())
                .build();
    }

    public List<Usuario> listarTodos() {return usuarioRepository.findAll();}

    public Usuario buscarPorId(Long id) {return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    public void excluir(Long id) {usuarioRepository.deleteById(id);}
}
