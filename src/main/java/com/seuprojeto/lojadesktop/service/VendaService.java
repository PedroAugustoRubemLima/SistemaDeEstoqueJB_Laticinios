package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Venda;
import com.seuprojeto.lojadesktop.model.ItemVenda;
import com.seuprojeto.lojadesktop.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}
