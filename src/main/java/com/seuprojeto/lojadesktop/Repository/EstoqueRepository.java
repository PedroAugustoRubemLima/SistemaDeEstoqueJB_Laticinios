package com.seuprojeto.lojadesktop.Repository;

import com.seuprojeto.lojadesktop.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {
    // Métodos prontos para acesso ao banco.
}
