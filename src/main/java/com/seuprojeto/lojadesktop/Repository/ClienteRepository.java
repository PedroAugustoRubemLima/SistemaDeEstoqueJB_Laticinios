package com.seuprojeto.lojadesktop.Repository;

import com.seuprojeto.lojadesktop.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCpf(String cpf);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}

