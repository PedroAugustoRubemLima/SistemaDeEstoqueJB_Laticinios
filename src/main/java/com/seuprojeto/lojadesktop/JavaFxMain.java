package com.seuprojeto.lojadesktop;

import com.seuprojeto.lojadesktop.config.SpringContext;
import com.seuprojeto.lojadesktop.config.SpringContextHolder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class JavaFxMain extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Inicializa o contexto Spring com o SpringBoot
        springContext = new SpringApplicationBuilder(SpringContext.class).run();
        SpringContextHolder.setContext(springContext); // Salva o contexto no holder
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o FXML e injeta os beans do Spring nos controllers
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/login.fxml"));
        loader.setControllerFactory(springContext::getBean); // Aqui ocorre a injeção dos controllers

        // Carrega a interface
        Scene scene = new Scene(loader.load());

        // Define e exibe o stage
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    @Override
    public void stop() {
        // Fecha o contexto Spring ao parar a aplicação
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args); // Inicia o JavaFX
    }
}
