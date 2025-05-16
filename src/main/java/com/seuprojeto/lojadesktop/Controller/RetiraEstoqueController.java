package com.seuprojeto.lojadesktop.Controller;

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
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

@Component
public class RetiraEstoqueController {

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
    public void voltarParaListagemdoEstoqueVendas() {
        try {
            URL fxmlLocation = this.getClass().getResource("/view/telas/ProdutoListagem.fxml");
            System.out.println("Localização do FXML: " + String.valueOf(fxmlLocation));
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            ConfigurableApplicationContext var10001 = SpringContextHolder.getContext();
            Objects.requireNonNull(var10001);
            loader.setControllerFactory(var10001::getBean);
            AnchorPane pane = (AnchorPane)loader.load();
            clienteComboBox.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
