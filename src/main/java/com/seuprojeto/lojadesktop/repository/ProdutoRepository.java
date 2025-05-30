package com.seuprojeto.lojadesktop.repository;

import java.util.List;
import com.seuprojeto.lojadesktop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    // Busca produtos por nome, ignorando maiúsculas/minúsculas, e que estejam ativos
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    // Busca todos os produtos que estejam ativos
    List<Produto> findByAtivoTrue();

    List<Produto> findByAtivoFalse();

    // Aqui você herda todos os métodos de CRUD (save, findById, findAll, deleteById)
}