package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Compra;
import com.seuprojeto.lojadesktop.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    public List<Compra> findAll() {
        return compraRepository.findAll();
    }

    public Optional<Compra> findById(Integer id) {
        return compraRepository.findById(id);
    }

    public Compra save(Compra compra) {
        return compraRepository.save(compra);
    }

    public void deleteById(Integer id) {
        compraRepository.deleteById(id);
    }
}
