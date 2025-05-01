package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Estoque")
public class Estoque {

    @Id
    @Column(name = "ID_Pro")
    private Integer idPro;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ID_Pro")
    private Produto produto;

    @Column(name = "Loc", length = 50, nullable = false)
    private String loc;

    @Column(name = "Qtde", nullable = false)
    private Integer qtde;

    // Getters e setters
    public Integer getIdPro() { return idPro; }
    public void setIdPro(Integer idPro) { this.idPro = idPro; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public String getLoc() { return loc; }
    public void setLoc(String loc) { this.loc = loc; }

    public Integer getQtde() { return qtde; }
    public void setQtde(Integer qtde) { this.qtde = qtde; }
}
