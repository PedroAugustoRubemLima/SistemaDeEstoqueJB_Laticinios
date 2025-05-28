package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {
}
