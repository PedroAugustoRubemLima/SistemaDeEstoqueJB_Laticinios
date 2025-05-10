package com.seuprojeto.lojadesktop.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Você pode adicionar inicializações ou outras configurações aqui, se necessário
        System.out.println("Aplicação JavaFX Iniciada");
    }

    public static void main(String[] args) {
        launch(args); // Executa o JavaFX
    }
}

