package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.SpringContextHolder;
import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.model.Venda;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.service.VendaService;
import com.seuprojeto.lojadesktop.service.ClienteService;
import com.seuprojeto.lojadesktop.service.FuncionarioService;
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
    @Resource private ClienteService clienteService;
    @Resource private FuncionarioService funcionarioService;


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
        // Carrega clientes reais
        ObservableList<String> nomesClientes = FXCollections.observableArrayList(
                clienteService.findAll().stream().map(Cliente::getNome).toList()
        );
        clienteComboBox.setItems(nomesClientes);
        clienteComboBox.setEditable(true); // Permite digitação

        // Carrega funcionários reais
        ObservableList<String> nomesFuncionarios = FXCollections.observableArrayList(
                funcionarioService.findAll().stream().map(Funcionario::getNome).toList()
        );
        funcionarioComboBox.setItems(nomesFuncionarios);
        funcionarioComboBox.setEditable(true); // Permite digitação para novo funcionário
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

        String clienteNome = clienteComboBox.getValue();
        String funcionarioNome = funcionarioComboBox.getValue();
        LocalDate data = dataPicker.getValue();
        String formaPagamento = formaPagamentoField.getText();

        if (clienteNome == null || clienteNome.isBlank() ||
                funcionarioNome == null || funcionarioNome.isBlank() ||
                formaPagamento == null || formaPagamento.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos da venda.");
            return;
        }

        try {
            // Busca cliente pelo nome; cria novo se não existir
            Cliente cliente = buscarClientePorNome(clienteNome);

            // Atualiza comboBox cliente caso tenha criado novo cliente
            if (!clienteComboBox.getItems().contains(cliente.getNome())) {
                clienteComboBox.getItems().add(cliente.getNome());
            }

            // Busca funcionário pelo nome; lança exceção se não encontrar
            Funcionario funcionario = buscarFuncionarioPorNome(funcionarioNome);
            if (!funcionarioComboBox.getItems().contains(funcionario.getNome())) {
                funcionarioComboBox.getItems().add(funcionario.getNome());
            }
            // Verifica e atualiza estoque
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

            // Cria a venda com itens convertidos
            Venda venda = new Venda();
            venda.setCliente(cliente);
            venda.setFuncionario(funcionario);
            venda.setFormaPagamento(formaPagamento);
            venda.setDataVenda(data != null ? data.atStartOfDay() : null);

            // Converte itensVenda para o tipo esperado em Venda
            venda.setItens(itensVenda.stream().map(item -> {
                com.seuprojeto.lojadesktop.model.ItemVenda itemVenda = new com.seuprojeto.lojadesktop.model.ItemVenda();
                itemVenda.setProduto(item.getProduto());
                itemVenda.setQuantidade(item.getQuantidade());
                return itemVenda;
            }).toList());

            vendaService.salvarVenda(venda);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Venda finalizada com sucesso!");
            limparTela();

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Funcionário não encontrado: " + funcionarioNome);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao finalizar a venda: " + e.getMessage());
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
    private Cliente buscarClientePorNome(String nome) {
        return clienteService.findByNome(nome)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    // Se não existir, cria novo cliente
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(nome);
                    return clienteService.save(novoCliente);
                });
    }

    private Funcionario buscarFuncionarioPorNome(String nome) {
        return funcionarioService.findByNome(nome)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    // Se não existir, cria novo Funcionario
                    Funcionario novoFuncionario = new Funcionario();
                    novoFuncionario.setNome(nome);
                    return funcionarioService.save(novoFuncionario);
                });
    }

}
