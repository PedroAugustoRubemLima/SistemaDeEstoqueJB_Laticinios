package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.SpringContextHolder;
import com.seuprojeto.lojadesktop.model.ItemVenda;
import com.seuprojeto.lojadesktop.model.Venda;
import com.seuprojeto.lojadesktop.service.VendaService;
import jakarta.annotation.Resource;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProdutosVendidosController {

    @FXML
    private TableView<ProdutoVendidoDTO> tabelaVendas;

    @FXML
    private TableColumn<ProdutoVendidoDTO, String> colunaProduto;

    @FXML
    private TableColumn<ProdutoVendidoDTO, Integer> colunaQuantidade;

    @FXML
    private TableColumn<ProdutoVendidoDTO, String> colunaCliente;

    @FXML
    private TableColumn<ProdutoVendidoDTO, String> colunaFuncionario;

    @FXML
    private TableColumn<ProdutoVendidoDTO, String> colunaData;

    @FXML
    private Label lblMensagem;

    @Resource
    private VendaService vendaService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void initialize() {
        colunaProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colunaFuncionario.setCellValueFactory(new PropertyValueFactory<>("nomeFuncionario"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));

        carregarProdutosVendidos();
    }

    private void carregarProdutosVendidos() {
        try {
            List<Venda> vendas = vendaService.buscarTodasVendas();
            List<ProdutoVendidoDTO> produtosVendidos = new ArrayList<>();

            for (Venda venda : vendas) {
                String nomeCliente = venda.getCliente().getNome();
                String nomeFuncionario = venda.getFuncionario().getNome(); // Supondo que Funcionario tem getNome()
                String dataVenda = venda.getDataVenda().format(formatter);

                for (ItemVenda item : venda.getItens()) {
                    String nomeProduto = item.getProduto().getNome();
                    Integer quantidade = item.getQuantidade();

                    produtosVendidos.add(new ProdutoVendidoDTO(
                            nomeProduto,
                            quantidade,
                            nomeCliente,
                            nomeFuncionario,
                            dataVenda
                    ));
                }
            }

            tabelaVendas.setItems(FXCollections.observableArrayList(produtosVendidos));
        } catch (Exception e) {
            lblMensagem.setText("Erro ao carregar vendas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void voltarParaListagem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutoListagem.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();
            tabelaVendas.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao voltar: " + e.getMessage());
        }
    }

    // DTO interno para exibição dos dados
    public static class ProdutoVendidoDTO {
        private final String nomeProduto;
        private final Integer quantidade;
        private final String nomeCliente;
        private final String nomeFuncionario;
        private final String dataVenda;

        public ProdutoVendidoDTO(String nomeProduto, Integer quantidade, String nomeCliente, String nomeFuncionario, String dataVenda) {
            this.nomeProduto = nomeProduto;
            this.quantidade = quantidade;
            this.nomeCliente = nomeCliente;
            this.nomeFuncionario = nomeFuncionario;
            this.dataVenda = dataVenda;
        }

        public String getNomeProduto() {
            return nomeProduto;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public String getNomeCliente() {
            return nomeCliente;
        }

        public String getNomeFuncionario() {
            return nomeFuncionario;
        }

        public String getDataVenda() {
            return dataVenda;
        }
    }
}


