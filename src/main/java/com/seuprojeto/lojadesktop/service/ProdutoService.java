package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // Importação necessária

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Modificado para retornar apenas produtos ativos
    public List<Produto> findAll() {
        return produtoRepository.findByAtivoTrue();
    }

    // Modificado para buscar apenas produtos ativos
    public Optional<Produto> findById(Integer id) {
        // Ao buscar por ID, podemos querer encontrar produtos inativos também,
        // caso a UI precise de uma tela de "produtos inativos" ou para edição.
        // Se você quiser que findById retorne APENAS ativos, mude para:
        // return produtoRepository.findById(id).filter(Produto::getAtivo);
        return produtoRepository.findById(id);
    }

    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    // MODIFICADO: Implementa o soft delete
    @Transactional // Garante que a operação seja atômica
    public void deleteById(Integer id) {
        Optional<Produto> produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            produto.setAtivo(false); // Marca o produto como inativo
            produtoRepository.save(produto); // Salva a alteração
        } else {
            throw new RuntimeException("Produto não encontrado com ID: " + id);
        }
    }

    // Modificado para buscar apenas produtos ativos
    public List<Produto> buscarPorNome(String nome){
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    // Opcional: Método para buscar produtos inativos (se você quiser uma tela para isso)
    public List<Produto> findAllInativos() {
        return produtoRepository.findByAtivoFalse();
    }

    // Opcional: Método para reativar um produto
    @Transactional
    public void reativarProduto(Integer id) {
        Optional<Produto> produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            produto.setAtivo(true);
            produtoRepository.save(produto);
        } else {
            throw new RuntimeException("Produto não encontrado com ID: " + id);
        }
    }
}