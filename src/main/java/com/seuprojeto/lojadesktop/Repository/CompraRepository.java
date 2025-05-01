package com.seuprojeto.lojadesktop.Repository;

import com.seuprojeto.lojadesktop.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Integer> {
    // Métodos prontos para acesso ao banco.
}
