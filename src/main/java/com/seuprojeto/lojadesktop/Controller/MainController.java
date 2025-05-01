package com.seuprojeto.lojadesktop.view;

import com.seuprojeto.lojadesktop.service.ClienteService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    @Autowired
    private ClienteService clienteService;

    @FXML
    private Button helloButton;

    @FXML
    protected void onHelloButtonClick() {
        // Lógica para o botão "Hello"
    }
}
