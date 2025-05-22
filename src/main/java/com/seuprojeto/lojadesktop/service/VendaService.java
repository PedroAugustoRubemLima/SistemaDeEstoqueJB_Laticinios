package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Venda;
import com.seuprojeto.lojadesktop.model.ItemVenda;
import com.seuprojeto.lojadesktop.Repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    public VendaService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    @Transactional
    public void salvarVenda(Venda venda) {
        for (ItemVenda item : venda.getItens()) {
            item.setVenda(venda); // vincula os itens à venda
        }
        vendaRepository.save(venda);
    }
    public List<Venda> buscarTodasVendas() {
        return vendaRepository.findAllWithItens(); // Usa o JOIN FETCH
    }

}
