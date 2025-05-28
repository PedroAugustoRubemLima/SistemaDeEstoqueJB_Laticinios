package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.service.ClienteService;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.service.VendaService;
import jakarta.annotation.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

@Component
public class RetiraEstoqueController {

    @Autowired
    private ClienteService clienteService;

    @FXML private ComboBox<String> clienteComboBox;
    @FXML private ComboBox<String> funcionarioComboBox;
    @FXML private DatePicker dataPicker;
    @FXML private TextField formaPagamentoField;

    @FXML private ComboBox<Produto> produtoComboBox;
    @FXML private TextField quantidadeField;

    @FXML private TableView<ItemVenda> produtosTable;
    @FXML private TableColumn<ItemVenda, String> produtoColumn;
    @FXML private TableColumn<ItemVenda, Integer> quantidadeColumn;
    @FXML private TableColumn<ItemVenda, Void> acaoColumn;

    @FXML private Label lblNomeProduto;

    @Resource private ProdutoService produtoService;
    @Resource private VendaService vendaService;

    private final ObservableList<ItemVenda> itensVenda = FXCollections.observableArrayList();
    private Produto produtoSelecionado;

    public void initialize() {
        configurarTabela();
        configurarBotaoRemover();
        carregarProdutos();
        carregarClientesEFucionarios();
        dataPicker.setValue(LocalDate.now());
        produtoComboBox.setEditable(true);
        produtoComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            for (Produto p : produtoComboBox.getItems()) {
                if (p.getCodigoBarras().equalsIgnoreCase(newVal.trim())) {
                    produtoComboBox.setValue(p);
                    break;
                }
            }
        });

    }

    private void configurarTabela() {
        produtoColumn.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        produtosTable.setItems(itensVenda);
    }

    private void carregarProdutos() {
        produtoComboBox.setItems(FXCollections.observableArrayList(produtoService.findAll()));
    }

    private void carregarClientesEFucionarios() {
        clienteComboBox.setItems(FXCollections.observableArrayList("João", "Maria", "Pedro"));
        funcionarioComboBox.setItems(FXCollections.observableArrayList("Atendente 1", "Atendente 2"));
    }

    public void setProdutoSelecionado(Produto produto) {
        this.produtoSelecionado = produto;
        lblNomeProduto.setText("Produto selecionado: " + produto.getNome());
        produtoComboBox.setValue(produto);
    }

    @FXML
    public void onAdicionarProduto() {
        Produto produto = produtoComboBox.getValue();

        if (produto == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione um produto.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeField.getText());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Digite uma quantidade válida em gramas.");
            return;
        }

        if (quantidade > produto.getQuantidade()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Estoque insuficiente para o produto: " + produto.getNome());
            return;
        }

        itensVenda.add(new ItemVenda(produto, quantidade));
        quantidadeField.clear();
    }

    private void configurarBotaoRemover() {
        acaoColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btnRemover = new Button("Remover");

            {
                btnRemover.setOnAction(e -> {
                    ItemVenda item = getTableView().getItems().get(getIndex());
                    itensVenda.remove(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRemover);
            }
        });
    }

    @FXML
    public void onFinalizarCompra() {
        if (itensVenda.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum produto adicionado.");
            return;
        }

        String cliente = clienteComboBox.getValue();
        String funcionario = funcionarioComboBox.getValue();
        LocalDate data = dataPicker.getValue();
        String formaPagamento = formaPagamentoField.getText();

        if (cliente == null || funcionario == null || formaPagamento.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos da venda.");
            return;
        }

        try {
            for (ItemVenda item : itensVenda) {
                Produto produto = item.getProduto();
                int quantidadeVendida = item.getQuantidade();

                if (quantidadeVendida > produto.getQuantidade()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Estoque insuficiente para: " + produto.getNome());
                    return;
                }

                produto.setQuantidade(produto.getQuantidade() - quantidadeVendida);
                produtoService.save(produto);
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Venda finalizada com sucesso!");
            limparTela();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao finalizar a venda.");
        }
    }

    private void limparTela() {
        clienteComboBox.setValue(null);
        funcionarioComboBox.setValue(null);
        formaPagamentoField.clear();
        itensVenda.clear();
        lblNomeProduto.setText("Produto Selecionado aparecerá aqui");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Aviso");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class ItemVenda {
        private final Produto produto;
        private final int quantidade;

        public ItemVenda(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public String getNomeProduto() {
            return produto.getNome();
        }

        public int getQuantidade() {
            return quantidade;
        }

        public Produto getProduto() {
            return produto;
        }
    }

    @FXML
    public void voltarParaListagem() {
        try {
            URL fxmlLocation = getClass().getResource("/view/telas/ProdutoListagem.fxml");
            System.out.println("Localização do FXML: " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            lblNomeProduto.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirTelaCadastroCliente() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/telas/ClienteCadastro.fxml"));
            fxmlLoader.setControllerFactory(SpringContextHolder.getContext()::getBean);

            Stage stage = new Stage();
            stage.setTitle("Cadastro de Cliente");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);
            stage.showAndWait();

            // Atualiza o ComboBox de cliente depois que fechar o cadastro
            clienteComboBox.setItems(FXCollections.observableArrayList(
                    clienteService.findAll().stream()
                            .map(Cliente::getNome)
                            .toList()
            ));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirHistoricoVendas() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/telas/HistoricoVendas.fxml"));
            fxmlLoader.setControllerFactory(SpringContextHolder.getContext()::getBean);

            Stage stage = new Stage();
            stage.setTitle("Histórico de Vendas");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
