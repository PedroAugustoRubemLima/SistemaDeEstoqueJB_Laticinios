package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.ComPro;
import com.seuprojeto.lojadesktop.model.ComPro.ComProId;
import com.seuprojeto.lojadesktop.repository.ComProRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate; // Importe LocalDate
import java.util.List;
import java.util.Optional;

@Service
public class ComProService {

    @Autowired
    private ComProRepository comProRepository;

    public List<ComPro> findAll() {
        return comProRepository.findAll();
    }

    public Optional<ComPro> findById(ComProId id) {
        return comProRepository.findById(id);
    }

    public ComPro save(ComPro comPro) {
        return comProRepository.save(comPro);
    }

    public void deleteById(ComProId id) {
        comProRepository.deleteById(id);
    }

    // NOVO MÉTODO: Busca itens de compra (ComPro) por data da Compra
    public List<ComPro> findByCompraDataBetween(LocalDate dataInicio, LocalDate dataFim) {
        return comProRepository.findByCompraDataBetween(dataInicio, dataFim);
    }
}