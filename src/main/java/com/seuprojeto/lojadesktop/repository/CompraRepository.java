package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
  List<Compra> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
}
