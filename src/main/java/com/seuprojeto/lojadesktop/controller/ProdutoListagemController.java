package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.net.URL;
import java.util.Optional;

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
    private Label lblValorTotalEstoque;

    // NOVO CAMINHO PADRÃO PARA AS IMAGENS DE PRODUTOS
    private static final String DEFAULT_IMAGE_PATH = "/assets/product_images/default_product.png";


    @FXML
    public void initialize() {
        carregarProdutos();
        txtPesquisar.setOnAction(e -> pesquisarProduto());
        verificarProdutosVencidos();
        calcularEExibirValorTotalEstoque();
    }

    private void calcularEExibirValorTotalEstoque() {
        List<Produto> todosOsProdutos = produtoService.findAll();
        double valorTotalEstoque = 0.0;

        for (Produto produto : todosOsProdutos) {
            valorTotalEstoque += produto.getQuantidadeCaixas() * produto.getPreco();
        }

        String valorFormatado = String.format("R$ %.2f", valorTotalEstoque);
        lblValorTotalEstoque.setText("Valor Total do Estoque: " + valorFormatado);
    }

    private void adicionarProduto(String nome, String tipo, Double preco, String vencimento, String imagePath, Integer id, Integer quantidadeCaixas) {
        HBox card = new HBox(15);
        card.getStyleClass().add("product-card");

        VBox infos = new VBox(5);
        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("product-title");

        Label lblTipo = new Label("Tipo: " + tipo);
        Label lblPreco = new Label("Preço: R$ " + preco);
        Label lblVencimento = new Label("Vencimento: " + vencimento);
        Label lblCaixas = new Label(String.format("Caixas: %d", quantidadeCaixas));

        lblTipo.getStyleClass().add("product-info");
        lblPreco.getStyleClass().add("product-info");
        lblVencimento.getStyleClass().add("product-info");
        lblCaixas.getStyleClass().add("product-info");

        infos.getChildren().addAll(lblNome, lblTipo, lblPreco, lblVencimento, lblCaixas);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        ImageView imagem = new ImageView();
        imagem.setFitHeight(60);
        imagem.setPreserveRatio(true);

        // Tenta carregar a imagem do produto
        URL imageUrl = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageUrl = getClass().getResource(imagePath);
            } catch (Exception e) {
                System.err.println("Erro ao obter URL da imagem para o produto " + nome + " no caminho: " + imagePath + " - " + e.getMessage());
            }
        }

        if (imageUrl != null) {
            imagem.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            // Se não houver caminho de imagem, ou a imagem não for encontrada, usa a imagem padrão
            try {
                URL defaultImageUrl = getClass().getResource(DEFAULT_IMAGE_PATH);
                if (defaultImageUrl != null) {
                    imagem.setImage(new Image(defaultImageUrl.toExternalForm()));
                } else {
                    System.err.println("Imagem padrão não encontrada: " + DEFAULT_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem padrão: " + e.getMessage());
            }
        }

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
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();
            listaProdutos.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void voltarParaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/Login.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();
            txtPesquisar.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void carregarProdutos() {
        listaProdutos.getChildren().clear();
        List<Produto> produtos = produtoService.findAll();
        for (Produto produto : produtos) {
            String vencimento = produto.getDataVencimento() != null ? produto.getDataVencimento().toString() : "Sem Data";
            Integer quantidadeCaixas = produto.getQuantidadeCaixas();
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(), vencimento, produto.getImagePath(), produto.getIdProduto(), quantidadeCaixas);
        }
        calcularEExibirValorTotalEstoque();
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
        for (Produto produto : produtos) {
            String vencimento = produto.getDataVencimento() != null ? produto.getDataVencimento().toString() : "Sem Data";
            Integer quantidadeCaixas = produto.getQuantidadeCaixas();
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(), vencimento, produto.getImagePath(), produto.getIdProduto(), quantidadeCaixas);
        }
    }

    @FXML
    public void deletarProduto(Integer id) {
        // Adicione uma confirmação para o usuário antes de "deletar"
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Tem certeza que deseja inativar este produto?");
        confirmAlert.setContentText("O produto será marcado como inativo e não aparecerá mais na listagem, mas seu histórico de vendas será preservado.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produtoService.deleteById(id); // Agora faz o soft delete
                carregarProdutos(); // Recarrega a lista após a "exclusão"
                mostrarAlerta("Sucesso", "Produto inativado com sucesso!");
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao inativar produto: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void editarProduto(Integer id) {
        try {
            Produto produto = produtoService.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutoCadastro.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            ProdutoCadastroController cadastroController = loader.getController();
            cadastroController.preencherCampos(produto);

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
    private void abrirHistoricoVendas() {
        try {
            URL fxmlLocation = getClass().getResource("/view/telas/HistoricoVendas.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            fxmlLoader.setControllerFactory(SpringContextHolder.getContext()::getBean);

            AnchorPane historicoPane = fxmlLoader.load();
            txtPesquisar.getScene().setRoot(historicoPane);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao abrir o histórico de vendas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void verificarProdutosVencidos() {
        LocalDate hoje = LocalDate.now();
        List<Produto> produtos = produtoService.findAll();
        for (Produto produto : produtos) {
            if (produto.getDataVencimento() != null && produto.getDataVencimento().isBefore(hoje)) {
                mostrarAlerta("Produto Vencido", "O produto '" + produto.getNome() + "' com vencimento em " + produto.getDataVencimento() + " está vencido!");
            } else if (produto.getDataVencimento() != null && produto.getDataVencimento().isEqual(hoje)) {
                mostrarAlerta("Produto Vencendo Hoje", "O produto '" + produto.getNome() + "' vence hoje (" + produto.getDataVencimento() + ")!");
            } else if (produto.getDataVencimento() != null && produto.getDataVencimento().isBefore(hoje.plusDays(7))) {
                mostrarAlerta("Produto Próximo do Vencimento", "O produto '" + produto.getNome() + "' vencerá em breve (" + produto.getDataVencimento() + ")!");
            }
        }
    }
}