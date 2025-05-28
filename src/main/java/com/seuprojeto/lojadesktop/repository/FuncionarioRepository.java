package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    // Métodos prontos para acesso ao banco.
}