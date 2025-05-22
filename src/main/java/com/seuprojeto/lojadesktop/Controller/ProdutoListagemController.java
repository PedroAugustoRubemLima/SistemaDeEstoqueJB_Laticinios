package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

@Component
public class ProdutoListagemController {

    @FXML
    private TextField txtPesquisar;

    @FXML
    private VBox listaProdutos;

    @Resource
    private ProdutoService produtoService;

    @FXML
    private Label lblNomeProduto;

    @FXML
    private Label lblSomaQtdEstoque;

    @FXML
    private Label lblSomaValorEstoque;

    @FXML
    public void initialize() {
        carregarProdutos();
        txtPesquisar.setOnAction(e -> pesquisarProduto());
// organiza melhor o código
    }


    private void adicionarProduto(String nome, String tipo, Double preco, String imagemPath, Integer id, Integer quantidade) {
        HBox card = new HBox(15);
        card.getStyleClass().add("product-card");

        VBox infos = new VBox(5);
        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("product-title");

        Label lblTipo = new Label("Tipo: " + tipo);
        Label lblPreco = new Label("Preço: R$ " + preco);
        Label lblQuantidade = new Label("Quantidade:"+ quantidade);

        lblTipo.getStyleClass().add("product-info");
        lblPreco.getStyleClass().add("product-info");
        lblQuantidade.getStyleClass().add("product-info");
        infos.getChildren().addAll(lblNome, lblTipo, lblPreco,lblQuantidade);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        ImageView imagem = new ImageView(new Image(getClass().getResourceAsStream(imagemPath)));
        imagem.setFitHeight(60);
        imagem.setPreserveRatio(true);

        // Botões de ação
        Button btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> editarProduto(id));

        Button btnDeletar = new Button("Deletar");
        btnDeletar.setOnAction(e -> deletarProduto(id));

        Button btnVender = new Button("Vender");
        btnVender.setOnAction(e -> abrirVendaComProduto(id));

        HBox botoes = new HBox(10, btnEditar, btnDeletar, btnVender);
        botoes.getStyleClass().add("product-buttons");

        card.getChildren().addAll(infos, espacador, imagem, botoes);
        listaProdutos.getChildren().add(card);
    }



    @FXML
    public void abrirCadastro() {
        try {
            ProdutoCadastroController.produtoEditado = null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutoCadastro.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/Login.fxml"));
            AnchorPane pane = loader.load();
            txtPesquisar.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void carregarProdutos() {
        listaProdutos.getChildren().clear();
        List<Produto> produtos = produtoService.findAll();
        double somaTotalvalor=0;
        int somaQuantidade = 0;
        for (Produto produto : produtos) {
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(), "/view/imagens/queijo1.png", produto.getIdProduto(), produto.getQuantidade());
            somaQuantidade += produto.getQuantidade();
            somaTotalvalor+=produto.getPreco() * produto.getQuantidade();
        }

        lblSomaQtdEstoque.setText(String.format("Quantidade total em estoque: %dg",somaQuantidade));
        lblSomaValorEstoque.setText("   Valor total no estoque: R$" + somaTotalvalor );
    }

    @FXML
    public void pesquisarProduto() {
        String termo = txtPesquisar.getText();
        List<Produto> produtos;

        if (termo == null || termo.isBlank()) {
            produtos = produtoService.findAll();
        } else {
            produtos = produtoService.buscarPorNome(termo);
        }

        listaProdutos.getChildren().clear();
        int somaQuantidade = 0;
        double somaValorTotal = 0.0;

        for (Produto produto : produtos) {
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(), "/view/imagens/queijo1.png", produto.getIdProduto(), produto.getQuantidade());
            somaQuantidade += produto.getQuantidade();
            somaValorTotal += produto.getPreco() * produto.getQuantidade();
        }

        lblSomaQtdEstoque.setText(String.format("Quantidade total: %dg",somaQuantidade));
        lblSomaValorEstoque.setText(String.format("  Valor total: R$" + somaValorTotal));
    }


    @FXML
    public void deletarProduto(Integer id) {
        produtoService.deleteById(id);
        carregarProdutos(); // Recarrega a lista após a exclusão
    }
    @FXML
    public void editarProduto(Integer id) {
        try {
            Produto produto = produtoService.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutoCadastro.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            ProdutoCadastroController cadastroController = loader.getController();
            cadastroController.preencherCampos(produto); // Passa o produto para o controller de cadastro

            // Troca a cena para o cadastro de produto
            listaProdutos.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void abrirVendaComProduto(Integer idProduto) {
        try {
            System.out.println("Abrindo venda para produto ID: " + idProduto);
            Produto produto = produtoService.findById(idProduto)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            System.out.println("Produto carregado: " + produto.getNome());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/RetiraEstoque.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            RetiraEstoqueController controller = loader.getController();
            controller.setProdutoSelecionado(produto);

            listaProdutos.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar FXML: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro geral: " + e.getMessage());
        }
    }
    @FXML
    public void abrirProdutosVendidos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutosVendidos.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();
            this.listaProdutos.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir a tela de Produtos Vendidos: " + e.getMessage());
        }
    }





}