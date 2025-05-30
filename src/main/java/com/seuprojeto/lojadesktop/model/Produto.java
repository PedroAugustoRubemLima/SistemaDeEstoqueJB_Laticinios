package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;
import java.time.LocalDate;
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
    private Double quantidade;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "peso_por_caixa")
    private Double pesoPorCaixa; // em kg

    @Column(name = "image_path", length = 255)
    private String imagePath;

    // NOVO CAMPO PARA SOFT DELETE
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true; // Por padrão, o produto é ativo

    // Construtor padrão
    public Produto() {
    }

    // Construtor com campos principais
    public Produto(String nome, String tipo, Double preco, Double quantidade, String codigoBarras) {
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.codigoBarras = codigoBarras;
    }

    // Getters e Setters (EXISTENTES)
    public Integer getIdProduto() { return idProduto; }
    public void setIdProduto(Integer idProduto) { this.idProduto = idProduto; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public Double getPesoPorCaixa() { return pesoPorCaixa; }
    public void setPesoPorCaixa(Double pesoPorCaixa) { this.pesoPorCaixa = pesoPorCaixa; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // NOVO GETTER E SETTER PARA 'ativo'
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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

    @Override
    public String toString() {
        return nome + " - " + codigoBarras;
    }

    public Integer getQuantidadeCaixas() {
        if (this.quantidade == null || this.quantidade <= 0) {
            return 0;
        }
        return (int) Math.floor(this.quantidade / 25.0);
    }
}