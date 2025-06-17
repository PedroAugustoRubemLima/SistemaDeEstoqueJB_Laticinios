package com.seuprojeto.lojadesktop.model;

import com.seuprojeto.lojadesktop.util.CryptoUtil;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @Column(name = "cpf", length = 200)
    private String cpf;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "telefone", length = 200)
    private String telefone;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCpf() {
        return cpf != null ? CryptoUtil.decrypt(cpf) : null;
    }

    public void setCpf(String cpf) {
        this.cpf = (cpf != null && !cpf.isBlank()) ? CryptoUtil.encrypt(cpf) : null;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() {
        return telefone != null ? CryptoUtil.decrypt(telefone) : null;
    }

    public void setTelefone(String telefone) {
        this.telefone = (telefone != null && !telefone.isBlank()) ? CryptoUtil.encrypt(telefone) : null;
    }

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
        return nome + (getCpf() != null && !getCpf().isEmpty() ? " (" + getCpf() + ")" : "");
    }
}
