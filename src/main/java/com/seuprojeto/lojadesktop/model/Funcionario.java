package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario")
    private Integer idFuncionario;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    // CPF agora é opcional (nullable = true por padrão) e não precisa ser único se for nulo
    @Column(name = "cpf", length = 14) // Removido nullable = false e unique = true
    private String cpf;

    // Telefone já é opcional por padrão
    @Column(name = "telefone", length = 20)
    private String telefone;

    // Getters e setters
    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return nome;
    }
}