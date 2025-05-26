package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Compra;
import com.seuprojeto.lojadesktop.service.CompraService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        Stage stage = (Stage) vendasTable.getScene().getWindow();
        stage.close();
    }
}
