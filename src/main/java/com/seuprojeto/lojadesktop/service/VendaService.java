package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Venda;
import com.seuprojeto.lojadesktop.Repository.VendaRepository;
import org.springframework.stereotype.Service;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    public VendaService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    public Venda salvar(Venda venda) {
        return vendaRepository.save(venda);
    }
}
