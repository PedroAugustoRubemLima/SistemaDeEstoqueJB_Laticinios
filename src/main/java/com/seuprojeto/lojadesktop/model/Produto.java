package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Produto")
    private Integer idProduto;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "preco", nullable = false)
    private Double preco;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    // Construtor padrão
    public Produto() {
    }
    // Construtor com campos principais
    public Produto(String nome, String tipo, Double preco, Integer quantidade, String codigoBarras) {
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.codigoBarras = codigoBarras;
    }

    // Getters e Setters

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    @PrePersist
    public void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    @PreUpdate
    public void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
