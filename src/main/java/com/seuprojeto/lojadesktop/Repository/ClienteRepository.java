package com.seuprojeto.lojadesktop.Repository;

import com.seuprojeto.lojadesktop.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Métodos prontos para acesso ao banco.
}
