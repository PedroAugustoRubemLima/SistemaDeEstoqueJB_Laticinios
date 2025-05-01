package com.seuprojeto.lojadesktop.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProdutoListagemController {

    @FXML
    private TextField txtPesquisar;

    @FXML
    private VBox listaProdutos;

    @FXML
    public void initialize() {
        // Exemplo: Adiciona 3 produtos estáticos ao iniciar
        adicionarProduto("QUEIJO LANCHE", "1 CAIXAS (60KG)", "30KG", "1KG", "XXX", "XXX", "/imagens/queijo1.png");
        adicionarProduto("QUEIJO CHEDDAR", "0,1 CAIXAS (3KG) ⚠", "30KG", "1KG", "XXX", "XXX", "/imagens/queijo2.png");
        adicionarProduto("QUEIJO GORGONZOLA", "4 CAIXAS (120KG)", "30KG", "1KG", "XXX", "XXX", "/imagens/queijo3.png");
    }

    private void adicionarProduto(String nome, String estoque, String pesoCaixa, String pesoUnidade,
                                  String fornecedor, String marca, String imagemPath) {

        HBox card = new HBox(15);
        card.getStyleClass().add("product-card");

        VBox infos = new VBox(5);

        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("product-title");

        Label lblEstoque = new Label("EM ESTOQUE: " + estoque);
        Label lblCaixa = new Label("PESO POR CAIXA: " + pesoCaixa + "     Fornecedor: " + fornecedor);
        Label lblUnidade = new Label("PESO POR UNIDADE: " + pesoUnidade + "     Marca: " + marca);

        lblEstoque.getStyleClass().add("product-info");
        lblCaixa.getStyleClass().add("product-info");
        lblUnidade.getStyleClass().add("product-info");

        infos.getChildren().addAll(lblNome, lblEstoque, lblCaixa, lblUnidade);

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
            AnchorPane pane = loader.load();
            listaProdutos.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
