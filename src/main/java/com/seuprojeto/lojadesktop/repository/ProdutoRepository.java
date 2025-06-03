package com.seuprojeto.lojadesktop.repository;

import java.time.LocalDate; // Importe LocalDate
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

    // NOVO MÉTODO: Busca produtos ativos com quantidade menor que um dado limite
    List<Produto> findByQuantidadeLessThanAndAtivoTrue(Double quantidade);

    // NOVO MÉTODO: Busca produtos ativos com data de vencimento entre duas datas
    List<Produto> findByDataVencimentoBetweenAndAtivoTrue(LocalDate dataInicio, LocalDate dataFim);

    // Aqui você herda todos os métodos de CRUD (save, findById, findAll, deleteById)
}