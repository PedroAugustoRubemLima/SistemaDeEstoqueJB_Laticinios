package com.seuprojeto.lojadesktop.model;

import com.seuprojeto.lojadesktop.util.CryptoUtil;
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

    @Column(name = "cpf", length = 200) // comprimento aumentado para suportar criptografia
    private String cpf;

    @Column(name = "telefone", length = 200)
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
        return cpf != null ? CryptoUtil.decrypt(cpf) : null;
    }

    public void setCpf(String cpf) {
        this.cpf = (cpf != null && !cpf.isBlank()) ? CryptoUtil.encrypt(cpf) : null;
    }

    public String getTelefone() {
        return telefone != null ? CryptoUtil.decrypt(telefone) : null;
    }

    public void setTelefone(String telefone) {
        this.telefone = (telefone != null && !telefone.isBlank()) ? CryptoUtil.encrypt(telefone) : null;
    }

    @Override
    public String toString() {
        return nome + (getCpf() != null && !getCpf().isEmpty() ? " (" + getCpf() + ")" : "");
    }
}
