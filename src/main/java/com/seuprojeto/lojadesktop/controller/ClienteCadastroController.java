package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClienteCadastroController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private TextField telefoneField;

    @FXML
    private TextField txtPesquisar; // Campo de pesquisa

    @FXML
    private TableView<Cliente> clientesTable; // Tabela para exibir clientes

    @FXML
    private TableColumn<Cliente, String> colNome; // Coluna Nome
    @FXML
    private TableColumn<Cliente, String> colCpf; // Coluna CPF
    @FXML
    private TableColumn<Cliente, String> colTelefone; // Coluna Telefone
    @FXML
    private TableColumn<Cliente, Void> colAcao; // Coluna para o botão de ação (excluir)

    @Autowired
    private ClienteService clienteService;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configura as colunas da tabela
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        // Configura a coluna de ação com um botão de exclusão
        colAcao.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Excluir");

            {
                deleteButton.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    deletarCliente(cliente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        clientesTable.setItems(listaClientes); // Vincula a lista observável à tabela
        carregarClientes(); // Carrega os clientes ao iniciar a tela
    }

    // Método para salvar o cliente
    @FXML
    private void salvarCliente() {
        if (validarCampos()) {
            // Validação de CPF
            if (!cpfField.getText().isEmpty() && cpfField.getText().length() > 11) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validação", "O CPF não pode ter mais de 11 dígitos.");
                return;
            }

            // Validação de Telefone
            if (!telefoneField.getText().isEmpty() && telefoneField.getText().length() > 11) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validação", "O Telefone não pode ter mais de 11 dígitos.");
                return;
            }

            try {
                Cliente cliente = new Cliente();
                cliente.setNome(nomeField.getText().trim());
                cliente.setCpf(cpfField.getText().isEmpty() ? null : cpfField.getText().trim());
                cliente.setTelefone(telefoneField.getText().isEmpty() ? null : telefoneField.getText().trim());

                clienteService.save(cliente);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente cadastrado com sucesso!");
                limparCampos();
                carregarClientes();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar cliente: " + e.getMessage());
            }
        }
    }


    // Método para limpar os campos do formulário
    @FXML
    private void limparCampos() {
        nomeField.clear();
        cpfField.clear();
        telefoneField.clear();
    }

    // Método para cancelar e fechar a janela
    @FXML
    private void cancelar() {
        fecharJanela();
    }

    // Validação dos campos (apenas nome é obrigatório)
    private boolean validarCampos() {
        if (nomeField.getText().trim().isEmpty()) { // Usar trim() para ignorar espaços em branco
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", "O campo Nome é obrigatório.");
            return false;
        }
        return true;
    }

    // Método para carregar e exibir clientes na tabela
    private void carregarClientes() {
        listaClientes.clear();
        List<Cliente> todosClientes = clienteService.findAll();
        listaClientes.addAll(todosClientes);
    }

    // Método para pesquisar clientes
    @FXML
    private void pesquisarCliente() {
        String termoPesquisa = txtPesquisar.getText().toLowerCase().trim();
        listaClientes.clear();
        if (termoPesquisa.isEmpty()) {
            listaClientes.addAll(clienteService.findAll());
        } else {
            List<Cliente> resultados = clienteService.findAll().stream()
                    .filter(cliente -> cliente.getNome().toLowerCase().contains(termoPesquisa) ||
                            (cliente.getCpf() != null && cliente.getCpf().toLowerCase().contains(termoPesquisa)))
                    .toList();
            listaClientes.addAll(resultados);
        }
    }

    // Método para deletar um cliente
    private void deletarCliente(Cliente cliente) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Tem certeza que deseja excluir o cliente " + cliente.getNome() + "?");
        confirmAlert.setContentText("Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                clienteService.deleteById(cliente.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cliente excluído com sucesso!");
                carregarClientes(); // Recarrega a lista após a exclusão
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir cliente: " + e.getMessage());
            }
        }
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