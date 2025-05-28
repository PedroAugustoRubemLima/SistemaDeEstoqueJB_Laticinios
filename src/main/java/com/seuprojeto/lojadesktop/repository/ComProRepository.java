package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.ComPro;
import com.seuprojeto.lojadesktop.model.ComPro.ComProId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComProRepository extends JpaRepository<ComPro, ComProId> {
    // Métodos prontos para acesso ao banco.
}
