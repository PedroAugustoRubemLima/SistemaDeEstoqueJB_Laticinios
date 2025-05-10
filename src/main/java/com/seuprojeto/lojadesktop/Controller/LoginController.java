package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.view.SpringContextHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @FXML
    public void logar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String senha = txtSenha.getText();

        if ("admin".equals(usuario) && "123".equals(senha)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/ProdutoListagem.fxml"));
                loader.setControllerFactory(SpringContextHolder.getContext()::getBean); // agora usando o contexto Spring
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



