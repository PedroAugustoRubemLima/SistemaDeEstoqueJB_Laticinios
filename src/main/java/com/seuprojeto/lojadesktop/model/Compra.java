package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Compra")
    private Integer idCompra;

    @Column(name = "forma_pagamento", length = 50, nullable = false)
    private String formaPagamento;

    @Column(name = "Valor_total", nullable = false)
    private Double valorTotal;

    @Column(name = "Data", nullable = false)
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "id_Cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_Funcionario", nullable = false)
    private Funcionario funcionario;

    // Construtor padrão
    public Compra() {}

    // Construtor com todos os campos (exceto ID gerado)
    public Compra(String formaPaga, Double valorTotal, LocalDate data, Cliente cliente, Funcionario funcionario) {
        this.formaPagamento = formaPaga;
        this.valorTotal = valorTotal;
        this.data = data;
        this.cliente = cliente;
        this.funcionario = funcionario;
    }

    // Getters e Setters
    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", formaPaga='" + formaPagamento + '\'' +
                ", valorTotal=" + valorTotal +
                ", data=" + data +
                ", cliente=" + (cliente != null ? cliente.getId() : "null") +
                ", funcionario=" + (funcionario != null ? funcionario.getIdFuncionario() : "null") +
                '}';
    }
}
