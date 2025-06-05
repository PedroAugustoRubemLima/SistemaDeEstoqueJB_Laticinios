package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Getters e setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    @PrePersist
    public void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = dataCriacao;
    }

    @PreUpdate
    public void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        // Adicionado uma verificação para evitar NullPointerException se o CPF for nulo
        return nome + (cpf != null && !cpf.isEmpty() ? " (" + cpf + ")" : "");
    }
}
