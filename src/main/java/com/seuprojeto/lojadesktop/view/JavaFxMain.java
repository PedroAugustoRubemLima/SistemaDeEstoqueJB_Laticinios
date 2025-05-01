package com.seuprojeto.lojadesktop.view;

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
        springContext = new SpringApplicationBuilder(SpringContext.class).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/login.fxml"));
        loader.setControllerFactory(springContext::getBean); // injeta Spring nos controllers
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Cadastro de Produto");
        stage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}
