package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.SpringContextHolder;
import com.seuprojeto.lojadesktop.util.HashUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Label lblMensagem;

    // 🔒 Hash da senha
    private static final String HASH_SENHA_SALVA = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3";

    @FXML
    public void logar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String senha = txtSenha.getText();

        String hashDigitado = HashUtil.gerarHash(senha);

        if ("admin".equals(usuario) && HASH_SENHA_SALVA.equals(hashDigitado)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ProdutoListagem.fxml"));
                loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
                Parent root = loader.load();

                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Listagem de Produtos");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                lblMensagem.setText("Erro ao abrir a tela de listagem.");
            }

        } else {
            lblMensagem.setText("Usuário ou senha inválidos");
        }
    }
}




