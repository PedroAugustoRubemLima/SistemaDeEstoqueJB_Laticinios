package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;
import java.io.Serializable;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Entity
@Table(name = "Compra_Produto")
public class ComPro {

    @Embeddable
    public static class ComProId implements Serializable {
        @Column(name = "id_Compra")
        private Integer idCompra;

        @Column(name = "id_Produto")
        private Integer idPro;

        // equals & hashCode obrigatórios para chave composta
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ComProId)) return false;
            ComProId that = (ComProId) o;
            return idCompra.equals(that.idCompra) && idPro.equals(that.idPro);
        }

        @Override
        public int hashCode() {
            return idCompra.hashCode() + idPro.hashCode();
        }

        // getters/setters
        public Integer getIdCompra() {
            return idCompra;
        }
        public void setIdCompra(Integer idCompra) {
            this.idCompra = idCompra;
        }

        public Integer getIdPro() {
            return idPro;
        }
        public void setIdPro(Integer idPro) {
            this.idPro = idPro;
        }
    }

    @EmbeddedId
    private ComProId id;

    @ManyToOne
    @MapsId("idCompra")
    @JoinColumn(name = "id_Compra")
    private Compra compra;

    @ManyToOne
    @MapsId("idPro")
    @JoinColumn(name = "id_Produto")
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade = 1; // valor padrão

    // Getters e setters
    public ComProId getId() { return id; }
    public void setId(ComProId id) { this.id = id; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        this.quantidade = quantidade;

    }

    public void adicionarQuantidade(int qtd) {
        this.quantidade += qtd;
    }


}
