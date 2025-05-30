package com.seuprojeto.lojadesktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ImageGalleryController {

  @FXML
  private FlowPane imageContainer;

  @FXML
  private Button btnSelect;

  @FXML
  private Button btnCancel;

  private String selectedImagePath;
  private ImageView selectedImageView;

  // NOVO CAMINHO PADRÃO PARA AS IMAGENS DE PRODUTOS
  private static final String IMAGE_RESOURCE_PATH = "/assets/product_images/";

  @FXML
  public void initialize() {
    loadImagesFromResources();
    btnSelect.setDisable(true);
  }

  private void loadImagesFromResources() {
    List<String> imageNames = new ArrayList<>();

    // Tenta listar arquivos do diretório de recursos
    try {
      URL folderUrl = getClass().getResource(IMAGE_RESOURCE_PATH);
      if (folderUrl != null && folderUrl.getProtocol().equals("file")) {
        File folder = new File(folderUrl.toURI());
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".gif")
        );
        if (files != null) {
          for (File file : files) {
            imageNames.add(file.getName());
          }
        }
      } else {
        System.out.println("Não foi possível listar arquivos diretamente do recurso. Certifique-se de que as imagens estão no diretório " + IMAGE_RESOURCE_PATH + " e o aplicativo não está em um JAR.");
        // Fallback: Adicione nomes de imagens manualmente se a listagem automática falhar
        // imageNames.add("queijo1.png");
        // imageNames.add("default_product.png");
        // Adicione outros nomes de imagens que você sabe que existem na pasta
      }
    } catch (Exception e) {
      System.err.println("Erro ao tentar listar imagens do diretório de recursos: " + e.getMessage());
      // Fallback em caso de erro
      // imageNames.add("queijo1.png");
      // imageNames.add("default_product.png");
    }

    if (imageNames.isEmpty()) {
      System.out.println("Nenhuma imagem encontrada em " + IMAGE_RESOURCE_PATH + ". Verifique o diretório e os nomes dos arquivos.");
      // Adicione uma imagem padrão se nenhuma for encontrada
      imageNames.add("default_product.png"); // Certifique-se que esta imagem existe no diretório
    }


    for (String imageName : imageNames) {
      try {
        URL imageUrl = getClass().getResource(IMAGE_RESOURCE_PATH + imageName);
        if (imageUrl != null) {
          Image image = new Image(imageUrl.toExternalForm());
          ImageView imageView = new ImageView(image);
          imageView.setFitWidth(100);
          imageView.setFitHeight(100);
          imageView.setPreserveRatio(true);
          imageView.setStyle("-fx-border-color: lightgray; -fx-border-width: 2; -fx-cursor: hand;");

          String fullPath = IMAGE_RESOURCE_PATH + imageName;
          imageView.setUserData(fullPath);

          imageView.setOnMouseClicked(event -> {
            if (selectedImageView != null) {
              selectedImageView.setStyle("-fx-border-color: lightgray; -fx-border-width: 2; -fx-cursor: hand;");
            }
            imageView.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-cursor: hand;");
            selectedImageView = imageView;
            selectedImagePath = (String) imageView.getUserData();
            btnSelect.setDisable(false);
          });

          imageContainer.getChildren().add(imageView);
        } else {
          System.err.println("Imagem não encontrada: " + IMAGE_RESOURCE_PATH + imageName);
        }
      } catch (Exception e) {
        System.err.println("Erro ao carregar imagem " + imageName + ": " + e.getMessage());
      }
    }
  }

  @FXML
  private void handleSelect() {
    Stage stage = (Stage) btnSelect.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void handleCancel() {
    selectedImagePath = null;
    Stage stage = (Stage) btnCancel.getScene().getWindow();
    stage.close();
  }

  public String getSelectedImagePath() {
    return selectedImagePath;
  }
}