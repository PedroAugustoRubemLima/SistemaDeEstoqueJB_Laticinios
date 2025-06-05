package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.service.FuncionarioService;
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
public class FuncionarioCadastroController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private TextField telefoneField;

    @FXML
    private TextField txtPesquisar; // Campo de pesquisa

    @FXML
    private TableView<Funcionario> funcionariosTable; // Tabela para exibir funcionários

    @FXML
    private TableColumn<Funcionario, String> colNome; // Coluna Nome
    @FXML
    private TableColumn<Funcionario, String> colCpf; // Coluna CPF
    @FXML
    private TableColumn<Funcionario, String> colTelefone; // Coluna Telefone
    @FXML
    private TableColumn<Funcionario, Void> colAcao; // Coluna para o botão de ação (excluir)

    @Autowired
    private FuncionarioService funcionarioService;

    private ObservableList<Funcionario> listaFuncionarios = FXCollections.observableArrayList();

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
                    Funcionario funcionario = getTableView().getItems().get(getIndex());
                    deletarFuncionario(funcionario);
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

        funcionariosTable.setItems(listaFuncionarios); // Vincula a lista observável à tabela
        carregarFuncionarios(); // Carrega os funcionários ao iniciar a tela
    }

    // Método para salvar o funcionário
    @FXML
    private void salvarFuncionario() {
        if (validarCampos()) {
            try {
                Funcionario funcionario = new Funcionario();
                funcionario.setNome(nomeField.getText());
                // CPF e Telefone são opcionais, então não há validação de preenchimento aqui
                funcionario.setCpf(cpfField.getText().isEmpty() ? null : cpfField.getText());
                funcionario.setTelefone(telefoneField.getText().isEmpty() ? null : telefoneField.getText());

                funcionarioService.save(funcionario);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Funcionário cadastrado com sucesso!");
                limparCampos(); // Limpa os campos após o cadastro
                carregarFuncionarios(); // Recarrega a lista na tabela
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar funcionário: " + e.getMessage());
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

    // Método para carregar e exibir funcionários na tabela
    private void carregarFuncionarios() {
        listaFuncionarios.clear();
        List<Funcionario> todosFuncionarios = funcionarioService.findAll();
        listaFuncionarios.addAll(todosFuncionarios);
    }

    // Método para pesquisar funcionários
    @FXML
    private void pesquisarFuncionario() {
        String termoPesquisa = txtPesquisar.getText().toLowerCase().trim();
        listaFuncionarios.clear();
        if (termoPesquisa.isEmpty()) {
            listaFuncionarios.addAll(funcionarioService.findAll());
        } else {
            List<Funcionario> resultados = funcionarioService.findAll().stream()
                    .filter(funcionario -> funcionario.getNome().toLowerCase().contains(termoPesquisa) ||
                            (funcionario.getCpf() != null && funcionario.getCpf().toLowerCase().contains(termoPesquisa)))
                    .toList();
            listaFuncionarios.addAll(resultados);
        }
    }

    // Método para deletar um funcionário
    private void deletarFuncionario(Funcionario funcionario) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Tem certeza que deseja excluir o funcionário " + funcionario.getNome() + "?");
        confirmAlert.setContentText("Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                funcionarioService.deleteById(funcionario.getIdFuncionario());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Funcionário excluído com sucesso!");
                carregarFuncionarios(); // Recarrega a lista após a exclusão
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir funcionário: " + e.getMessage());
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