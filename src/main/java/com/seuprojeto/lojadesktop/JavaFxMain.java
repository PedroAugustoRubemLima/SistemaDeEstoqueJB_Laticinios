package com.seuprojeto.lojadesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class JavaFxMain extends Application {

    private ConfigurableApplicationContext springContext;
    private Stage primaryStage;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SpringContext.class).run();
        SpringContextHolder.setContext(springContext);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        // Carregar a primeira tela
        loadLoginScreen();

        // Configurações de tela cheia
        configureFullscreenBehavior();

        stage.show();
    }

    private void loadLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("JB Laticínios - Sistema de Gestão");
    }

    private void configureFullscreenBehavior() {
        // Iniciar em tela cheia
        primaryStage.setFullScreen(true);

        // Remover dicas e atalhos para sair do fullscreen
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(null);

        // Obter dimensões da tela
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        // Forçar permanência em fullscreen
        primaryStage.setResizable(false);

        // Listener para manter fullscreen se o usuário tentar alterar
        primaryStage.iconifiedProperty().addListener((obs, wasIconified, isIconified) -> {
            if (!isIconified) {
                primaryStage.setFullScreen(true);
            }
        });
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Método para trocar telas mantendo o fullscreen
    public void switchScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(springContext::getBean);
        Scene newScene = new Scene(loader.load());
        primaryStage.setScene(newScene);
        primaryStage.setFullScreen(true); // Reforçar fullscreen
    }
}