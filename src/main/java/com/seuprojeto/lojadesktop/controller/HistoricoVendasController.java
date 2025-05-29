package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Compra;
import com.seuprojeto.lojadesktop.service.CompraService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.seuprojeto.lojadesktop.SpringContextHolder;
import javafx.fxml.FXMLLoader; // Para carregar o FXML
import javafx.scene.Parent; // Para o nó raiz do FXML
import java.io.IOException; // Para tratar exceções de I/O
import java.net.URL; // Para localizar o arquivo FXML
import javafx.scene.layout.AnchorPane; // Se o root do ProdutoListagem.fxml for um AnchorPane
import javafx.scene.control.Alert; // Para o alerta de erro

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

    @Autowired
    private CompraService compraService;

    @FXML
    public void initialize() {
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

        carregarVendas();
    }

    private void carregarVendas() {
        vendasTable.setItems(FXCollections.observableArrayList(compraService.findAll()));
    }

    @FXML
    private void fechar() {
        try {
            // Localiza o FXML da tela de listagem de produtos
            URL fxmlLocation = getClass().getResource("/view/telas/ProdutoListagem.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);

            // Garante que o Spring injete o controller da tela de listagem
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);

            // Carrega o painel da tela de listagem
            AnchorPane pane = loader.load(); // Assumindo que ProdutoListagem.fxml tem AnchorPane como root

            // Obtém a cena atual (usando vendasTable que está na cena do histórico)
            // e define o novo painel como o nó raiz da cena, voltando à tela anterior
            vendasTable.getScene().setRoot(pane);

        } catch (IOException e) {
            e.printStackTrace();
            // Exibe um alerta se houver um erro ao tentar voltar
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao voltar para a listagem de produtos: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
