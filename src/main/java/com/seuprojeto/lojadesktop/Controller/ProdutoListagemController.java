package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.Repository.ProdutoRepository;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.view.SpringContextHolder;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ProdutoListagemController {

    @FXML
    private TextField txtPesquisar;

    @FXML
    private VBox listaProdutos;

    @Resource
    private ProdutoRepository produtoRepository;

    @FXML
    public void initialize() {
        carregarProdutos(); // organiza melhor o código
    }


    private void adicionarProduto(String nome, String tipo, Double preco, String imagemPath) {
        HBox card = new HBox(15);
        card.getStyleClass().add("product-card");

        VBox infos = new VBox(5);
        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("product-title");

        Label lblTipo = new Label("Tipo: " + tipo);
        Label lblPreco = new Label("Preço: R$ " + preco);

        lblTipo.getStyleClass().add("product-info");
        lblPreco.getStyleClass().add("product-info");

        infos.getChildren().addAll(lblNome, lblTipo, lblPreco);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        ImageView imagem = new ImageView(new Image(getClass().getResourceAsStream(imagemPath)));
        imagem.setFitHeight(60);
        imagem.setPreserveRatio(true);

        card.getChildren().addAll(infos, espacador, imagem);
        listaProdutos.getChildren().add(card);
    }

    @FXML
    public void abrirCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/ProdutoCadastro.fxml"));
            // Garantir que o Spring injete o controller
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            // Carrega a tela
            AnchorPane pane = loader.load();
            // Troca a cena
            listaProdutos.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void voltarParaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/Login.fxml"));
            AnchorPane pane = loader.load();
            txtPesquisar.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void carregarProdutos() {
        listaProdutos.getChildren().clear(); // para não duplicar
        List<Produto> produtos = produtoRepository.findAll();
        for (Produto produto : produtos) {
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(), "/imagens/queijo1.png");
        }
    }

}