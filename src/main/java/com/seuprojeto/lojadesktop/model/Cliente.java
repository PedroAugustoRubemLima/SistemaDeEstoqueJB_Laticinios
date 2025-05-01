package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Cli")
    private Integer idCli;

    @Column(name = "CPF", length = 11, nullable = false)
    private String cpf;

    @Column(name = "Nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "Telefone", length = 15)
    private String telefone;

    // Getters e setters
    public Integer getIdCli() { return idCli; }
    public void setIdCli(Integer idCli) { this.idCli = idCli; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}