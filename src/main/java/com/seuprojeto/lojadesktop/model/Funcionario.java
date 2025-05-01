package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Fun")
    private Integer idFun;

    @Column(name = "Nome", length = 100, nullable = false)
    private String nome;

    // Getters e setters
    public Integer getIdFun() { return idFun; }
    public void setIdFun(Integer idFun) { this.idFun = idFun; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
