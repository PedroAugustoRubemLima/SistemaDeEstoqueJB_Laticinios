package com.seuprojeto.lojadesktop.Repository;

import com.seuprojeto.lojadesktop.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
}
