package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Compra;
import com.seuprojeto.lojadesktop.model.ComPro;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.CompraService;
import com.seuprojeto.lojadesktop.service.ComProService;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import com.seuprojeto.lojadesktop.service.PdfGeneratorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import com.seuprojeto.lojadesktop.SpringContextHolder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.io.File;
import java.awt.Desktop;
import java.util.function.Consumer;

@Component
public class HistoricoVendasController {

    @FXML
    private TableView<Compra> vendasTable;

    @FXML
    private TableColumn<Compra, String> colCliente;

    @FXML
    private TableColumn<Compra, String> colFuncionario;

    @FXML
    private TableColumn<Compra, String> colData;

    @FXML
    private TableColumn<Compra, Double> colValorTotal;

    // COMPONENTES FXML PARA O RELATÓRIO DE VENDAS POR PERÍODO (EXISTENTES)
    @FXML
    private DatePicker datePickerInicio;

    @FXML
    private DatePicker datePickerFim;

    @FXML
    private TableView<RelatorioVendasItem> tabelaRelatorioVendas;

    @FXML
    private TableColumn<RelatorioVendasItem, LocalDate> colDataVenda;

    @FXML
    private TableColumn<RelatorioVendasItem, Double> colTotalVendas;

    // COMPONENTES FXML PARA O RELATÓRIO DE PRODUTOS MAIS VENDIDOS (EXISTENTES)
    @FXML
    private DatePicker datePickerInicioProdutos;

    @FXML
    private DatePicker datePickerFimProdutos;

    @FXML
    private TableView<ProdutoMaisVendidoItem> tabelaProdutosMaisVendidos;

    @FXML
    private TableColumn<ProdutoMaisVendidoItem, String> colNomeProdutoVendido;

    @FXML
    private TableColumn<ProdutoMaisVendidoItem, String> colTipoProdutoVendido;

    @FXML
    private TableColumn<ProdutoMaisVendidoItem, Integer> colQuantidadeVendida;

    // NOVOS COMPONENTES FXML PARA OS RELATÓRIOS DE ESTOQUE
    @FXML
    private TextField txtLimiteBaixoEstoque;

    @FXML
    private TableView<EstoqueBaixoItem> tabelaBaixoEstoque;

    @FXML
    private TableColumn<EstoqueBaixoItem, String> colNomeBaixoEstoque;

    @FXML
    private TableColumn<EstoqueBaixoItem, String> colTipoBaixoEstoque;

    @FXML
    private TableColumn<EstoqueBaixoItem, Double> colQuantidadeAtual;

    @FXML
    private TextField txtDiasVencimento;

    @FXML
    private TableView<ProximoVencimentoItem> tabelaProximoVencimento;

    @FXML
    private TableColumn<ProximoVencimentoItem, String> colNomeVencimento;

    @FXML
    private TableColumn<ProximoVencimentoItem, String> colTipoVencimento;

    @FXML
    private TableColumn<ProximoVencimentoItem, LocalDate> colDataVencimento;


    @Autowired
    private CompraService compraService;

    @Autowired
    private ComProService comProService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;


    @FXML
    public void initialize() {
        // Configuração da tabela de Histórico Detalhado (EXISTENTE)
        colCliente.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCliente().getNome())
        );
        colFuncionario.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFuncionario().getNome())
        );
        colData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getData().toString())
        );
        colValorTotal.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getValorTotal())
        );

        carregarVendas(); // Carrega o histórico detalhado ao iniciar

        // Configuração da tabela de Relatório por Período (EXISTENTE)
        colDataVenda.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataVenda"));
        colTotalVendas.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalVendas"));

        // Opcional: Define as datas padrão (ex: último mês) para o relatório de vendas por período
        datePickerFim.setValue(LocalDate.now());
        datePickerInicio.setValue(LocalDate.now().minusMonths(1));
        gerarRelatorioVendasPorPeriodo(); // Gera o relatório inicial ao carregar a tela

        // Configuração da tabela de Produtos Mais Vendidos (EXISTENTE)
        colNomeProdutoVendido.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nomeProduto"));
        colTipoProdutoVendido.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tipoProduto"));
        colQuantidadeVendida.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantidadeVendida"));

        // Opcional: Define as datas padrão (ex: último mês) para o relatório de produtos mais vendidos
        datePickerFimProdutos.setValue(LocalDate.now());
        datePickerInicioProdutos.setValue(LocalDate.now().minusMonths(1));
        gerarRelatorioProdutosMaisVendidos(); // Gera o relatório inicial ao carregar a tela

        // Configuração da tabela de Produtos com Baixo Estoque (NOVO)
        colNomeBaixoEstoque.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nomeProduto"));
        colTipoBaixoEstoque.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tipoProduto"));
        colQuantidadeAtual.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantidadeAtual"));

        // Configuração da tabela de Produtos Próximos do Vencimento (NOVO)
        colNomeVencimento.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nomeProduto"));
        colTipoVencimento.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tipoProduto"));
        colDataVencimento.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataVencimento"));

        // Define valores padrão e gera relatórios de estoque iniciais
        txtLimiteBaixoEstoque.setText("50.0"); // Exemplo: limite de 50 KG
        gerarRelatorioBaixoEstoque();

        txtDiasVencimento.setText("30"); // Exemplo: produtos que vencem em 30 dias
        gerarRelatorioProximoVencimento();
    }

    private void carregarVendas() {
        vendasTable.setItems(FXCollections.observableArrayList(compraService.findAll()));
    }

    @FXML
    private void fechar() {
        try {
            URL fxmlLocation = getClass().getResource("/view/telas/ProdutoListagem.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();
            vendasTable.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Erro ao voltar para a listagem de produtos: " + e.getMessage());
        }
    }

    // MÉTODO: Gerar Relatório de Vendas por Período (EXISTENTE)
    @FXML
    public void gerarRelatorioVendasPorPeriodo() {
        LocalDate dataInicio = datePickerInicio.getValue();
        LocalDate dataFim = datePickerFim.getValue();

        if (dataInicio == null || dataFim == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datas Inválidas", "Selecione as datas de início e fim para gerar o relatório de vendas por período.");
            return;
        }

        if (dataInicio.isAfter(dataFim)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datas Inválidas", "A data inicial não pode ser posterior à data final no relatório de vendas por período.");
            return;
        }

        List<Compra> comprasNoPeriodo = compraService.findByDataBetween(dataInicio, dataFim);

        Map<LocalDate, Double> vendasPorData = comprasNoPeriodo.stream()
                .collect(Collectors.groupingBy(
                        Compra::getData,
                        Collectors.summingDouble(Compra::getValorTotal)
                ));

        ObservableList<RelatorioVendasItem> dadosRelatorio = FXCollections.observableArrayList();
        vendasPorData.forEach((data, total) -> dadosRelatorio.add(new RelatorioVendasItem(data, total)));

        dadosRelatorio.sort((item1, item2) -> item1.getDataVenda().compareTo(item2.getDataVenda()));

        tabelaRelatorioVendas.setItems(dadosRelatorio);

        if (dadosRelatorio.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Nenhum Resultado", "Nenhuma venda encontrada para o período selecionado no relatório de vendas por período.");
        }
    }

    // MÉTODO: Gerar Relatório de Produtos Mais Vendidos (EXISTENTE)
    @FXML
    public void gerarRelatorioProdutosMaisVendidos() {
        LocalDate dataInicio = datePickerInicioProdutos.getValue();
        LocalDate dataFim = datePickerFimProdutos.getValue();

        if (dataInicio == null || dataFim == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datas Inválidas", "Selecione as datas de início e fim para gerar o relatório de produtos mais vendidos.");
            return;
        }

        if (dataInicio.isAfter(dataFim)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datas Inválidas", "A data inicial não pode ser posterior à data final no relatório de produtos mais vendidos.");
            return;
        }

        List<ComPro> itensCompradosNoPeriodo = comProService.findByCompraDataBetween(dataInicio, dataFim);

        Map<Integer, Integer> quantidadeVendidaPorProdutoId = itensCompradosNoPeriodo.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProduto().getIdProduto(),
                        Collectors.summingInt(ComPro::getQuantidade)
                ));

        ObservableList<ProdutoMaisVendidoItem> dadosRelatorio = FXCollections.observableArrayList();
        quantidadeVendidaPorProdutoId.forEach((produtoId, quantidadeTotal) -> {
            produtoService.findById(produtoId).ifPresent(produto -> {
                dadosRelatorio.add(new ProdutoMaisVendidoItem(
                        produto.getNome(),
                        produto.getTipo(),
                        quantidadeTotal
                ));
            });
        });

        dadosRelatorio.sort(Comparator.comparingInt(ProdutoMaisVendidoItem::getQuantidadeVendida).reversed());

        tabelaProdutosMaisVendidos.setItems(dadosRelatorio);

        if (dadosRelatorio.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Nenhum Resultado", "Nenhum produto encontrado para o período selecionado no relatório de produtos mais vendidos.");
        }
    }

    // NOVO MÉTODO: Gerar Relatório de Produtos com Baixo Estoque
    @FXML
    public void gerarRelatorioBaixoEstoque() {
        double limiteEstoque;
        try {
            limiteEstoque = Double.parseDouble(txtLimiteBaixoEstoque.getText());
            if (limiteEstoque < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada Inválida", "Por favor, insira um valor numérico positivo para o limite de estoque.");
            return;
        }

        // Busca todos os produtos ativos com quantidade abaixo do limite
        List<Produto> produtosBaixoEstoque = produtoService.findByQuantidadeLessThanAndAtivoTrue(limiteEstoque);

        ObservableList<EstoqueBaixoItem> dadosRelatorio = FXCollections.observableArrayList();
        produtosBaixoEstoque.forEach(produto -> {
            dadosRelatorio.add(new EstoqueBaixoItem(
                    produto.getNome(),
                    produto.getTipo(),
                    produto.getQuantidade()
            ));
        });

        tabelaBaixoEstoque.setItems(dadosRelatorio);

        if (dadosRelatorio.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Nenhum Resultado", "Nenhum produto com estoque abaixo de " + limiteEstoque + " KG encontrado.");
        }
    }

    // NOVO MÉTODO: Gerar Relatório de Produtos Próximos do Vencimento
    @FXML
    public void gerarRelatorioProximoVencimento() {
        int diasParaVencer;
        try {
            diasParaVencer = Integer.parseInt(txtDiasVencimento.getText());
            if (diasParaVencer < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada Inválida", "Por favor, insira um número inteiro positivo para os dias para vencer.");
            return;
        }

        LocalDate dataLimiteVencimento = LocalDate.now().plusDays(diasParaVencer);

        // Busca todos os produtos ativos que vencem até a data limite
        List<Produto> produtosProximoVencimento = produtoService.findByDataVencimentoBetweenAndAtivoTrue(LocalDate.now(), dataLimiteVencimento);

        ObservableList<ProximoVencimentoItem> dadosRelatorio = FXCollections.observableArrayList();
        produtosProximoVencimento.forEach(produto -> {
            dadosRelatorio.add(new ProximoVencimentoItem(
                    produto.getNome(),
                    produto.getTipo(),
                    produto.getDataVencimento()
            ));
        });

        tabelaProximoVencimento.setItems(dadosRelatorio);

        if (dadosRelatorio.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Nenhum Resultado", "Nenhum produto vencendo nos próximos " + diasParaVencer + " dias encontrado.");
        }
    }

    // ====================================================================================================
    // MÉTODOS PARA GERAR PDF
    // ====================================================================================================

    private void gerarPdf(String titulo, String nomeArquivoPadrao, Consumer<String> geradorPdfAction) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório " + titulo);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf"));
        fileChooser.setInitialFileName(nomeArquivoPadrao + ".pdf");

        Stage stage = (Stage) vendasTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                geradorPdfAction.accept(file.getAbsolutePath());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Relatório " + titulo + " gerado com sucesso em:\n" + file.getAbsolutePath());

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao gerar relatório " + titulo + ":\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onGerarPdfHistoricoDetalhado() {
        List<Compra> vendas = vendasTable.getItems();
        gerarPdf("Histórico Detalhado", "historico_vendas", (filePath) -> {
            try {
                pdfGeneratorService.generateHistoricoVendasPdf(vendas, filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void onGerarPdfVendasPorPeriodo() {
        List<RelatorioVendasItem> vendasPorPeriodo = tabelaRelatorioVendas.getItems();
        gerarPdf("Vendas por Período", "vendas_por_periodo", (filePath) -> {
            try {
                pdfGeneratorService.generateRelatorioVendasPorPeriodoPdf(vendasPorPeriodo, filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void onGerarPdfProdutosMaisVendidos() {
        List<ProdutoMaisVendidoItem> produtosMaisVendidos = tabelaProdutosMaisVendidos.getItems();
        gerarPdf("Produtos Mais Vendidos", "produtos_mais_vendidos", (filePath) -> {
            try {
                pdfGeneratorService.generateProdutosMaisVendidosPdf(produtosMaisVendidos, filePath);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void onGerarPdfBaixoEstoque() {
        List<EstoqueBaixoItem> estoqueBaixo = tabelaBaixoEstoque.getItems();
        gerarPdf("Estoque Baixo", "estoque_baixo", (filePath) -> {
            try {
                pdfGeneratorService.generateEstoqueBaixoPdf(estoqueBaixo, filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void onGerarPdfProximoVencimento() {
        List<ProximoVencimentoItem> proximoVencimento = tabelaProximoVencimento.getItems();
        gerarPdf("Próximo Vencimento", "proximo_vencimento", (filePath) -> {
            try {
                pdfGeneratorService.generateProximoVencimentoPdf(proximoVencimento, filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // CLASSE INTERNA PARA REPRESENTAR UM ITEM NA TABELA DE RELATÓRIO DE VENDAS POR PERÍODO (EXISTENTE)
    public static class RelatorioVendasItem {
        private final LocalDate dataVenda;
        private final Double totalVendas;

        public RelatorioVendasItem(LocalDate dataVenda, Double totalVendas) {
            this.dataVenda = dataVenda;
            this.totalVendas = totalVendas;
        }

        public LocalDate getDataVenda() {
            return dataVenda;
        }

        public Double getTotalVendas() {
            return totalVendas;
        }
    }

    // CLASSE INTERNA PARA REPRESENTAR UM ITEM NA TABELA DE PRODUTOS MAIS VENDIDOS (EXISTENTE)
    public static class ProdutoMaisVendidoItem {
        private final String nomeProduto;
        private final String tipoProduto;
        private final Integer quantidadeVendida; // Campo final

        public ProdutoMaisVendidoItem(String nomeProduto, String tipoProduto, Integer quantidadeVendida) {
            this.nomeProduto = nomeProduto;
            this.tipoProduto = tipoProduto;
            this.quantidadeVendida = quantidadeVendida; // Sua correção!
        }

        public String getNomeProduto() {
            return nomeProduto;
        }

        public String getTipoProduto() {
            return tipoProduto;
        }

        public Integer getQuantidadeVendida() {
            return quantidadeVendida;
        }
    }

    // NOVA CLASSE INTERNA PARA REPRESENTAR UM ITEM NA TABELA DE PRODUTOS COM BAIXO ESTOQUE
    public static class EstoqueBaixoItem {
        private final String nomeProduto;
        private final String tipoProduto;
        private final Double quantidadeAtual; // Campo final

        public EstoqueBaixoItem(String nomeProduto, String tipoProduto, Double quantidadeAtual) {
            this.nomeProduto = nomeProduto;
            this.tipoProduto = tipoProduto;
            this.quantidadeAtual = quantidadeAtual; // Sua correção!
        }

        public String getNomeProduto() {
            return nomeProduto;
        }

        public String getTipoProduto() {
            return tipoProduto;
        }

        public Double getQuantidadeAtual() {
            return quantidadeAtual;
        }
    }

    // NOVA CLASSE INTERNA PARA REPRESENTAR UM ITEM NA TABELA DE PRODUTOS PRÓXIMOS DO VENCIMENTO
    public static class ProximoVencimentoItem {
        private final String nomeProduto;
        private final String tipoProduto;
        private final LocalDate dataVencimento; // Campo final

        public ProximoVencimentoItem(String nomeProduto, String tipoProduto, LocalDate dataVencimento) {
            this.nomeProduto = nomeProduto;
            this.tipoProduto = tipoProduto;
            this.dataVencimento = dataVencimento; // Sua correção!
        }

        public String getNomeProduto() {
            return nomeProduto;
        }

        public String getTipoProduto() {
            return tipoProduto;
        }

        public LocalDate getDataVencimento() {
            return dataVencimento;
        }
    }
}