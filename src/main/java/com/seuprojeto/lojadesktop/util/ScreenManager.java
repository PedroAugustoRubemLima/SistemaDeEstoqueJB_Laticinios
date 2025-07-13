package com.seuprojeto.lojadesktop.util;

import javafx.stage.Stage;

public class ScreenManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void enforceFullscreen() {
        if (primaryStage != null) {
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(null);
        }
    }
}