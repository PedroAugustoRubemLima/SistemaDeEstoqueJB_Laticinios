package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.Repository.*;
import com.seuprojeto.lojadesktop.model.*;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
public class RetiraEstoqueController {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private FuncionarioRepository funcionarioRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private CompraRepository compraRepository;
    @Autowired private ComProRepository comProRepository;

    @FXML private ComboBox<Cliente> clienteComboBox;
    @FXML private ComboBox<Funcionario> funcionarioComboBox;
    @FXML private DatePicker dataPicker;
    @FXML private TextField formaPagamentoField;
    @FXML private ComboBox<Produto> produtoComboBox;
    @FXML private TextField quantidadeField;
    @FXML private TableView<ComProItem> produtosTable;
    @FXML private TableColumn<ComProItem, String> produtoColumn;
    @FXML private TableColumn<ComProItem, Integer> quantidadeColumn;
    @FXML private TableColumn<ComProItem, Void> acaoColumn;

    private ObservableList<ComProItem> produtosAdicionados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        clienteComboBox.setItems(FXCollections.observableArrayList(clienteRepository.findAll()));
        funcionarioComboBox.setItems(FXCollections.observableArrayList(funcionarioRepository.findAll()));
        produtoComboBox.setItems(FXCollections.observableArrayList(produtoRepository.findAll()));

        produtoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduto().getNome()));
        quantidadeColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantidade()).asObject());
        produtosTable.setItems(produtosAdicionados);

        adicionarColunaRemover();
    }

    private void adicionarColunaRemover() {
        acaoColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ComProItem, Void> call(final TableColumn<ComProItem, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Remover");

                    {
                        btn.setOnAction(event -> {
                            ComProItem item = getTableView().getItems().get(getIndex());
                            produtosAdicionados.remove(item);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });
    }

    @FXML
    public void onAdicionarProduto() {
        Produto produto = produtoComboBox.getValue();
        String qtdTexto = quantidadeField.getText();

        if (produto == null || qtdTexto == null || qtdTexto.isEmpty()) {
            mostrarAlerta("Erro", "Selecione um produto e informe a quantidade.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(qtdTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Quantidade inválida.");
            return;
        }

        if (quantidade <= 0) {
            mostrarAlerta("Erro", "A quantidade deve ser maior que zero.");
            return;
        }

        if (produto.getQuantidade() < quantidade) {
            mostrarAlerta("Erro", "Estoque insuficiente para o produto selecionado.");
            return;
        }

        produtosAdicionados.add(new ComProItem(produto, quantidade));
        quantidadeField.clear();
    }


    @FXML
    public void onFinalizarCompra() {
        Cliente cliente = clienteComboBox.getValue();
        Funcionario funcionario = funcionarioComboBox.getValue();
        LocalDate data = dataPicker.getValue();
        String formaPagamento = formaPagamentoField.getText();

        if (cliente == null || funcionario == null || data == null || formaPagamento.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos obrigatórios.");
            return;
        }

        // Revalidar se ainda há estoque suficiente antes de registrar
        for (ComProItem item : produtosAdicionados) {
            Produto produtoAtual = produtoRepository.findById(item.getProduto().getIdProduto()).orElse(null);
            if (produtoAtual == null || produtoAtual.getQuantidade() < item.getQuantidade()) {
                mostrarAlerta("Erro", "Estoque insuficiente para o produto: " + item.getProduto().getNome());
                return;
            }
        }

        double total = produtosAdicionados.stream()
                .mapToDouble(item -> item.getProduto().getPreco() * item.getQuantidade())
                .sum();

        Compra compra = new Compra(formaPagamento, total, data, cliente, funcionario);
        compraRepository.save(compra);

        for (ComProItem item : produtosAdicionados) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto); // Atualiza o estoque

            ComPro comPro = new ComPro();
            ComPro.ComProId id = new ComPro.ComProId();
            id.setIdCompra(compra.getIdCompra());
            id.setIdPro(produto.getIdProduto());

            comPro.setId(id);
            comPro.setCompra(compra);
            comPro.setProduto(produto);
            comPro.setQuantidade(item.getQuantidade()); // Se tiver o campo quantidade

            comProRepository.save(comPro);
        }

        mostrarAlerta("Sucesso", "Compra cadastrada e estoque atualizado!");
        limparCampos();
    }

    private void limparCampos() {
        clienteComboBox.getSelectionModel().clearSelection();
        funcionarioComboBox.getSelectionModel().clearSelection();
        formaPagamentoField.clear();
        dataPicker.setValue(null);
        produtoComboBox.getSelectionModel().clearSelection();
        quantidadeField.clear();
        produtosAdicionados.clear();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static class ComProItem {
        private final Produto produto;
        private final int quantidade;

        public ComProItem(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public Produto getProduto() {
            return produto;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
}



