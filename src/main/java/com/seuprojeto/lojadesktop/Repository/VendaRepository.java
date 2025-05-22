package com.seuprojeto.lojadesktop.Repository;
import java.util.List;
import com.seuprojeto.lojadesktop.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query("SELECT DISTINCT v FROM Venda v LEFT JOIN FETCH v.itens")
    List<Venda> findAllWithItens();
}

