package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
    // Métodos prontos para acesso ao banco.
}
