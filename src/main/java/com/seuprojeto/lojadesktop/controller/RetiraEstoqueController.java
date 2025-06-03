package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.service.ClienteService;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.service.VendaService; // Embora VendaService exista, vamos usar CompraService para o histórico
import com.seuprojeto.lojadesktop.model.Compra; // Importar a entidade Compra
import com.seuprojeto.lojadesktop.model.ComPro; // Importar a entidade ComPro
import com.seuprojeto.lojadesktop.model.Funcionario; // Importar a entidade Funcionario
import com.seuprojeto.lojadesktop.service.CompraService; // Importar o serviço de Compra
import com.seuprojeto.lojadesktop.service.ComProService; // Importar o serviço de ComPro
import com.seuprojeto.lojadesktop.service.FuncionarioService; // Importar o serviço de Funcionario

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
import java.util.List;
import java.util.Optional; // Para lidar com Optional de findById
import java.net.URL;

@Component
public class RetiraEstoqueController {

    @Autowired
    private ClienteService clienteService;
    @Autowired // Adicionado
    private FuncionarioService funcionarioService; // Adicionado
    @Autowired // Adicionado
    private CompraService compraService; // Adicionado
    @Autowired // Adicionado
    private ComProService comProService; // Adicionado

    @FXML private ComboBox<String> clienteComboBox;
    @FXML private ComboBox<String> funcionarioComboBox;
    @FXML private DatePicker dataPicker;
    @FXML private TextField formaPagamentoField;

    @FXML private ComboBox<Produto> produtoComboBox;
    @FXML private TextField quantidadeField; // Este campo agora receberá a quantidade em CAIXAS

    @FXML private TableView<ItemVenda> produtosTable;
    @FXML private TableColumn<ItemVenda, String> produtoColumn;
    @FXML private TableColumn<ItemVenda, Double> quantidadeColumn; // ALTERADO: Agora é Double para KG
    @FXML private TableColumn<ItemVenda, Void> acaoColumn;

    @FXML private Label lblNomeProduto;

    @Resource
    private ProdutoService produtoService;
    @Resource private VendaService vendaService; // Mantido, mas não será usado para salvar a Compra

    private final ObservableList<ItemVenda> itensVenda = FXCollections.observableArrayList();
    private Produto produtoSelecionado;

    public void initialize() {
        configurarTabela();
        configurarBotaoRemover();
        carregarProdutos();
        carregarClientesEFucionarios();
        dataPicker.setValue(LocalDate.now());

        produtoComboBox.setEditable(false);

        produtoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblNomeProduto.setText("Produto selecionado: " + newVal.getNome());
            } else {
                lblNomeProduto.setText("Produto Selecionado aparecerá aqui");
            }
        });
    }

    private void configurarTabela() {
        produtoColumn.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade")); // Agora exibe KG
        produtosTable.setItems(itensVenda);
    }

    private void carregarProdutos() {
        List<Produto> allProducts = produtoService.findAll();
        if (allProducts.isEmpty()) {
            System.out.println("Nenhum produto encontrado no banco de dados. Verifique se há produtos cadastrados.");
        }
        produtoComboBox.setItems(FXCollections.observableArrayList(allProducts));
    }

    private void carregarClientesEFucionarios() {
        // Carrega clientes do serviço
        List<String> nomesClientes = clienteService.findAll().stream()
                .map(Cliente::getNome)
                .toList();
        clienteComboBox.setItems(FXCollections.observableArrayList(nomesClientes));

        // Carrega funcionários do serviço
        List<String> nomesFuncionarios = funcionarioService.findAll().stream()
                .map(Funcionario::getNome)
                .toList();
        funcionarioComboBox.setItems(FXCollections.observableArrayList(nomesFuncionarios));
    }

    public void setProdutoSelecionado(Produto produto) {
        this.produtoSelecionado = produto;
        produtoComboBox.setValue(produto);
    }

    @FXML
    public void onAdicionarProduto() {
        Produto produto = produtoComboBox.getValue();

        if (produto == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione um produto válido.");
            return;
        }

        int quantidadeCaixas; // Agora esperamos a quantidade em CAIXAS
        try {
            quantidadeCaixas = Integer.parseInt(quantidadeField.getText());
            if (quantidadeCaixas <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Digite uma quantidade válida de caixas.");
            return;
        }

        // CONVERSÃO CRÍTICA: Converte a quantidade de caixas para quilogramas
        double quantidadeEmKgParaAdicionar = quantidadeCaixas * 25.0; // Assumindo 25.0 kg por caixa

        if (quantidadeEmKgParaAdicionar > produto.getQuantidade()) { // Compara em KG
            mostrarAlerta(Alert.AlertType.ERROR, "Estoque insuficiente para o produto: " + produto.getNome() + ". Quantidade em estoque: " + String.format("%.2f", produto.getQuantidade()) + " kg.");       return;
        }

        // Verifica se o produto já está na lista para atualizar a quantidade
        boolean foundInList = false;
        for (ItemVenda item : itensVenda) {
            if (item.getProduto().getIdProduto().equals(produto.getIdProduto())) {
                // Atualiza a quantidade do item existente (em KG)
                double novaQuantidadeTotalEmKg = item.getQuantidade() + quantidadeEmKgParaAdicionar;
                if (novaQuantidadeTotalEmKg > produto.getQuantidade()) { // Verifica o estoque total
                    mostrarAlerta(Alert.AlertType.ERROR, "Estoque insuficiente para o produto: " + produto.getNome() + ". Quantidade em estoque: " + produto.getQuantidade() + " kg.");
                    return;
                }
                item.setQuantidade(novaQuantidadeTotalEmKg); // Adiciona em KG
                produtosTable.refresh(); // Atualiza a tabela para mostrar a quantidade atualizada
                foundInList = true;
                break;
            }
        }

        if (!foundInList) {
            itensVenda.add(new ItemVenda(produto, quantidadeEmKgParaAdicionar)); // Adiciona novo item (quantidade em KG)
        }

        quantidadeField.clear();
        produtoComboBox.setValue(null);
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

        if (clienteNome == null || funcionarioNome == null || formaPagamento.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos da venda (Cliente, Funcionário, Forma de Pagamento).");
            return;
        }

        try {
            // 1. Buscar Cliente e Funcionário pelo nome (ou ID, se disponível)
            // Para simplificar, vamos buscar o primeiro cliente/funcionário com o nome correspondente.
            // Em um sistema real, você teria um mecanismo mais robusto (ex: ComboBox de objetos, não strings).
            Optional<Cliente> clienteOpt = clienteService.findAll().stream()
                    .filter(c -> c.getNome().equals(clienteNome))
                    .findFirst();
            if (clienteOpt.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Cliente não encontrado.");
                return;
            }
            Cliente cliente = clienteOpt.get();

            Optional<Funcionario> funcionarioOpt = funcionarioService.findAll().stream()
                    .filter(f -> f.getNome().equals(funcionarioNome))
                    .findFirst();
            if (funcionarioOpt.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Funcionário não encontrado.");
                return;
            }
            Funcionario funcionario = funcionarioOpt.get();

            double valorTotalVenda = 0.0;

            // 2. Atualizar estoque e calcular valor total
            for (ItemVenda item : itensVenda) {
                Produto produto = item.getProduto();
                double quantidadeVendidaEmKg = item.getQuantidade(); // Já está em KG

                if (quantidadeVendidaEmKg > produto.getQuantidade()) { // Compara em KG
                    mostrarAlerta(Alert.AlertType.ERROR, "Estoque insuficiente para: " + produto.getNome());
                    return;
                }

                produto.setQuantidade(produto.getQuantidade() - quantidadeVendidaEmKg); // Atualiza o estoque em KG
                produtoService.save(produto); // Salva o produto com a nova quantidade

                double numeroDeCaixasVendidas = quantidadeVendidaEmKg / 25.0;
                valorTotalVenda += produto.getPreco() * numeroDeCaixasVendidas; // Calcula o valor para este item
            }

            // 3. Criar e Salvar a Compra
            Compra novaCompra = new Compra();
            novaCompra.setCliente(cliente);
            novaCompra.setFuncionario(funcionario);
            novaCompra.setData(data);
            novaCompra.setFormaPagamento(formaPagamento);
            novaCompra.setValorTotal(valorTotalVenda);

            Compra compraSalva = compraService.save(novaCompra); // Salva a compra principal

            // 4. Criar e Salvar os itens ComPro para a compra
            for (ItemVenda item : itensVenda) {
                ComPro comPro = new ComPro();
                ComPro.ComProId comProId = new ComPro.ComProId();
                comProId.setIdCompra(compraSalva.getIdCompra());
                comProId.setIdPro(item.getProduto().getIdProduto());
                comPro.setId(comProId);
                comPro.setCompra(compraSalva);
                comPro.setProduto(item.getProduto());
                // Convertendo KG para "caixas" (inteiro), arredondando para o mais próximo
                // Isso é uma adaptação devido à inconsistência do tipo Integer em ComPro.quantidade
                int quantidadeComPro = (int) Math.round(item.getQuantidade() / 25.0);
                if (quantidadeComPro == 0 && item.getQuantidade() > 0) { // Garante que pelo menos 1 unidade seja registrada se houver venda
                    quantidadeComPro = 1;
                }
                comPro.setQuantidade(quantidadeComPro);
                comProService.save(comPro); // Salva o item da compra
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Venda finalizada com sucesso!");
            limparTela();

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

    // CLASSE INTERNA ItemVenda - ALTERADA PARA TRABALHAR COM DOUBLE (KG)
    public static class ItemVenda {
        private final Produto produto;
        private double quantidade; // ALTERADO: Agora em quilogramas (KG)

        public ItemVenda(Produto produto, double quantidade) { // ALTERADO: Construtor aceita double
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public String getNomeProduto() {
            return produto.getNome();
        }

        public double getQuantidade() { // ALTERADO: Retorna double
            return quantidade;
        }

        public void setQuantidade(double quantidade) { // ALTERADO: Aceita double
            this.quantidade = quantidade;
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

            // Recarrega a lista de clientes após o cadastro
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
            // Carrega o FXML do histórico de vendas
            URL fxmlLocation = getClass().getResource("/view/telas/HistoricoVendas.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);

            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);

            AnchorPane pane = loader.load();

            // Obtém a cena atual e define o novo painel como raiz
            // Usamos lblNomeProduto.getScene() para obter a cena atual
            lblNomeProduto.getScene().setRoot(pane);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir o histórico de vendas: " + e.getMessage());
        }
    }
}