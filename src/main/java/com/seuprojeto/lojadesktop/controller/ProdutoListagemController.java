package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import com.seuprojeto.lojadesktop.util.ScreenManager;
import jakarta.annotation.Resource;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Component
public class ProdutoListagemController {

    @FXML private TextField txtPesquisar;
    @FXML private VBox listaProdutos;
    @FXML private Label lblValorTotalEstoque;
    @Resource private ProdutoService produtoService;

    // Formato de data para exibição
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DEFAULT_IMAGE_PATH = "/assets/product_images/default_product.png";

    @FXML
    public void initialize() {
        // Configurar para manter tela cheia e centralizada
        Platform.runLater(() -> {
            Stage stage = (Stage) listaProdutos.getScene().getWindow();
            ScreenManager.setPrimaryStage(stage);
            ScreenManager.enforceFullscreen();
            centralizarConteudo();
        });

        carregarProdutos();
        txtPesquisar.setOnAction(e -> pesquisarProduto());
        calcularEExibirValorTotalEstoque();

        // Executar verificação de produtos vencidos em segundo plano
        new Thread(this::verificarProdutosVencidos).start();
    }

    private void centralizarConteudo() {
        VBox vbox = (VBox) listaProdutos.getScene().getRoot().getChildrenUnmodifiable().get(0);
        vbox.setAlignment(Pos.CENTER);
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

    private void adicionarProduto(String nome, String tipo, Double preco, String vencimento,
                                  String imagePath, Integer id, Integer quantidadeCaixas) {
        HBox card = new HBox(15);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infos = new VBox(5);
        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("product-title");

        Label lblTipo = new Label("Tipo: " + tipo);
        Label lblPreco = new Label("Preço: R$ " + String.format("%.2f", preco));
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
        imagem.setFitWidth(60);
        imagem.setPreserveRatio(true);

        // Carregar imagem do produto
        carregarImagemProduto(imagem, imagePath, nome);

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

    private void carregarImagemProduto(ImageView imagem, String imagePath, String nomeProduto) {
        URL imageUrl = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageUrl = getClass().getResource(imagePath);
            } catch (Exception e) {
                System.err.println("Erro ao obter URL da imagem para " + nomeProduto + ": " + e.getMessage());
            }
        }

        if (imageUrl != null) {
            imagem.setImage(new Image(imageUrl.toExternalForm()));
        } else {
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
    }

    @FXML
    public void abrirCadastro() {
        try {
            ProdutoCadastroController.produtoEditado = null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutoCadastro.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            Stage stage = (Stage) listaProdutos.getScene().getWindow();
            stage.getScene().setRoot(pane);
            ScreenManager.enforceFullscreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void voltarParaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/Login.fxml"));
            AnchorPane pane = loader.load();

            Stage stage = (Stage) listaProdutos.getScene().getWindow();
            stage.getScene().setRoot(pane);
            ScreenManager.enforceFullscreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void carregarProdutos() {
        listaProdutos.getChildren().clear();
        List<Produto> produtos = produtoService.findAll();
        for (Produto produto : produtos) {
            String vencimento = produto.getDataVencimento() != null ?
                    produto.getDataVencimento().format(DATE_FORMATTER) : "Sem Data";
            Integer quantidadeCaixas = produto.getQuantidadeCaixas();
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(),
                    vencimento, produto.getImagePath(), produto.getIdProduto(),
                    quantidadeCaixas);
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
            String vencimento = produto.getDataVencimento() != null ?
                    produto.getDataVencimento().format(DATE_FORMATTER) : "Sem Data";
            Integer quantidadeCaixas = produto.getQuantidadeCaixas();
            adicionarProduto(produto.getNome(), produto.getTipo(), produto.getPreco(),
                    vencimento, produto.getImagePath(), produto.getIdProduto(),
                    quantidadeCaixas);
        }
    }

    @FXML
    public void deletarProduto(Integer id) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Tem certeza que deseja inativar este produto?");
        confirmAlert.setContentText("O produto será marcado como inativo e não aparecerá mais na listagem, mas seu histórico de vendas será preservado.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produtoService.deleteById(id);
                carregarProdutos();
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

            Stage stage = (Stage) listaProdutos.getScene().getWindow();
            stage.getScene().setRoot(pane);
            ScreenManager.enforceFullscreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirVendaComProduto(Integer idProduto) {
        try {
            Produto produto = produtoService.findById(idProduto)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/RetiraEstoque.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            RetiraEstoqueController controller = loader.getController();
            controller.setProdutoSelecionado(produto);

            Stage stage = (Stage) listaProdutos.getScene().getWindow();
            stage.getScene().setRoot(pane);
            ScreenManager.enforceFullscreen();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirHistoricoVendas() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/telas/HistoricoVendas.fxml"));
            fxmlLoader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane historicoPane = fxmlLoader.load();

            Stage stage = (Stage) listaProdutos.getScene().getWindow();
            stage.getScene().setRoot(historicoPane);
            ScreenManager.enforceFullscreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao abrir o histórico de vendas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
    }

    private void verificarProdutosVencidos() {
        LocalDate hoje = LocalDate.now();
        List<Produto> produtos = produtoService.findAll();
        boolean encontrouVencidos = false;
        StringBuilder mensagem = new StringBuilder();

        for (Produto produto : produtos) {
            if (produto.getDataVencimento() != null) {
                LocalDate vencimento = produto.getDataVencimento();

                if (vencimento.isBefore(hoje)) {
                    mensagem.append("• ").append(produto.getNome())
                            .append(" venceu em ").append(vencimento.format(DATE_FORMATTER))
                            .append("\n");
                    encontrouVencidos = true;
                } else if (vencimento.isEqual(hoje)) {
                    mensagem.append("• ").append(produto.getNome())
                            .append(" vence hoje!\n");
                    encontrouVencidos = true;
                } else if (vencimento.isBefore(hoje.plusDays(7))) {
                    mensagem.append("• ").append(produto.getNome())
                            .append(" vencerá em ").append(vencimento.format(DATE_FORMATTER))
                            .append("\n");
                    encontrouVencidos = true;
                }
            }
        }

        if (encontrouVencidos) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção a Produtos Vencidos");
                alert.setHeaderText("Produtos com validade próxima ou vencida:");
                alert.setContentText(mensagem.toString());
                alert.showAndWait();
            });
        }
    }
}