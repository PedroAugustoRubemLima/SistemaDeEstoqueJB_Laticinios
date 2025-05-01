package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    // Aqui você herda todos os métodos de CRUD (save, findById, findAll, deleteById)
}

