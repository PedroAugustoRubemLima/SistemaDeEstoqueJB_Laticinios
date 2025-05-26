package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.service.ClienteService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClienteCadastroController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private TextField telefoneField;

    @Autowired
    private ClienteService clienteService;

    // Método para salvar o cliente
    @FXML
    private void salvarCliente() {
        if (validarCampos()) {
            try {
                Cliente cliente = new Cliente();
                cliente.setNome(nomeField.getText());
                cliente.setCpf(cpfField.getText());
                cliente.setTelefone(telefoneField.getText());

                clienteService.save(cliente);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente cadastrado com sucesso!");

                fecharJanela();

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar cliente: " + e.getMessage());
            }
        }
    }

    // Método para cancelar e fechar a janela
    @FXML
    private void cancelar() {
        fecharJanela();
    }

    // Validação dos campos
    private boolean validarCampos() {
        if (nomeField.getText().isEmpty() || cpfField.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", "Nome e CPF são obrigatórios.");
            return false;
        }
        return true;
    }

    // Método para mostrar alertas
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Fechar a janela atual
    private void fecharJanela() {
        Stage stage = (Stage) nomeField.getScene().getWindow();
        stage.close();
    }
}
