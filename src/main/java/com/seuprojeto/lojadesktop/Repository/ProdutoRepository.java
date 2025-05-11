package com.seuprojeto.lojadesktop.Repository;
import java.util.List;
import com.seuprojeto.lojadesktop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByNomeContainingIgnoreCase(String nome);
// Aqui você herda todos os métodos de CRUD (save, findById, findAll, deleteById)
}

