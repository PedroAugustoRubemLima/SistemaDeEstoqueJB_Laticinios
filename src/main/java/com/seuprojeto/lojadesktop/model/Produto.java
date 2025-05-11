package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Pro")
    private Integer idPro;

    @Column(name = "Nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "Tipo", length = 50)
    private String tipo;

    @Column(name = "Preco", nullable = false)
    private Double preco;

    // Construtor vazio
    public Produto() {
    }

    // Construtor sem quantidade
    public Produto(String nome, String tipo, Double preco) {
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
    }

    // Getters e Setters

    public Integer getIdPro() {
        return idPro;
    }

    public void setIdPro(Integer idPro) {
        this.idPro = idPro;
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


}

