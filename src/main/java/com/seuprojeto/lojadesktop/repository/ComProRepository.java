package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.ComPro;
import com.seuprojeto.lojadesktop.model.ComPro.ComProId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Importe LocalDate
import java.util.List;

@Repository
public interface ComProRepository extends JpaRepository<ComPro, ComProId> {
  // Métodos prontos para acesso ao banco.

  // NOVO MÉTODO: Busca itens de compra (ComPro) por data da Compra
  List<ComPro> findByCompraDataBetween(LocalDate dataInicio, LocalDate dataFim);
}