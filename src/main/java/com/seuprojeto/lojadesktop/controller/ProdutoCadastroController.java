package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.repository.ProdutoRepository;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

@Component
public class ProdutoCadastroController {

    @Resource
    private ProdutoRepository produtoRepository;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtTipo;

    @FXML
    private TextField txtPreco;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private Label lblMensagem;

    @FXML
    private TextField codigoBarrasField;

    @FXML
    private DatePicker dataVencimentoPicker;

    @FXML
    private ImageView imageViewProduto;
    @FXML
    private Button btnAnexarImagem;

    private String selectedImagePath;

    public static Produto produtoEditado;

    public void preencherCampos(Produto produto) {
        if (produto != null) {
            produtoEditado = produto;
            txtNome.setText(produto.getNome());
            txtTipo.setText(produto.getTipo());
            txtPreco.setText(String.valueOf(produto.getPreco()));
            txtQuantidade.setText(String.valueOf(produto.getQuantidade()));
            codigoBarrasField.setText(produto.getCodigoBarras());
            dataVencimentoPicker.setValue(produto.getDataVencimento());
            if (produto.getDataVencimento() != null) {
                dataVencimentoPicker.setValue(produto.getDataVencimento());
            }

            // Carregar e exibir a imagem existente
            if (produto.getImagePath() != null && !produto.getImagePath().isEmpty()) {
                try {
                    URL imageUrl = getClass().getResource(produto.getImagePath());
                    if (imageUrl != null) {
                        Image image = new Image(imageUrl.toExternalForm());
                        imageViewProduto.setImage(image);
                        this.selectedImagePath = produto.getImagePath();
                    } else {
                        System.err.println("Imagem não encontrada no caminho: " + produto.getImagePath());
                        imageViewProduto.setImage(null);
                        this.selectedImagePath = null;
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar imagem para edição: " + e.getMessage());
                    imageViewProduto.setImage(null);
                    this.selectedImagePath = null;
                }
            } else {
                imageViewProduto.setImage(null);
                this.selectedImagePath = null;
            }
        }
    }

    @FXML
    public void cadastrarProduto() {
        System.out.println("Salvando produto...");
        try {
            String nome = txtNome.getText();
            String tipo = txtTipo.getText();
            Double preco = Double.parseDouble(txtPreco.getText());
            Double quantidade = Double.parseDouble(txtQuantidade.getText());
            String codigoBarras = codigoBarrasField.getText();
            LocalDate dataVencimento = dataVencimentoPicker.getValue();

            if (produtoEditado != null) {
                produtoEditado.setNome(nome);
                produtoEditado.setTipo(tipo);
                produtoEditado.setPreco(preco);
                produtoEditado.setQuantidade(quantidade);
                produtoEditado.setCodigoBarras(codigoBarras);
                produtoEditado.setDataVencimento(dataVencimentoPicker.getValue());
                produtoEditado.setImagePath(selectedImagePath);

                produtoRepository.save(produtoEditado);
                lblMensagem.setText("Produto atualizado com sucesso!");
            } else {
                Produto novoProduto = new Produto();
                novoProduto.setNome(nome);
                novoProduto.setTipo(tipo);
                novoProduto.setPreco(preco);
                novoProduto.setQuantidade(quantidade);
                novoProduto.setCodigoBarras(codigoBarras);
                novoProduto.setDataVencimento(dataVencimentoPicker.getValue());
                novoProduto.setImagePath(selectedImagePath);

                produtoRepository.save(novoProduto);
                lblMensagem.setText("Produto salvo com sucesso!");
            }

            txtNome.clear();
            txtTipo.clear();
            txtPreco.clear();
            txtQuantidade.clear();
            codigoBarrasField.clear();
            dataVencimentoPicker.setValue(null);
            imageViewProduto.setImage(null);
            selectedImagePath = null;

            produtoEditado = null;

        } catch (NumberFormatException e) {
            lblMensagem.setText("Erro de formato: Verifique Preço e Quantidade.");
            e.printStackTrace();
        } catch (Exception e) {
            lblMensagem.setText("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnexarImagem() {
        System.out.println("Botão Anexar Imagem clicado! Tentando abrir a galeria...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telas/ImageGallery.fxml"));
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);

            AnchorPane root = loader.load();
            ImageGalleryController galleryController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Galeria de Imagens");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initOwner(btnAnexarImagem.getScene().getWindow());
            stage.showAndWait();

            String returnedPath = galleryController.getSelectedImagePath();
            if (returnedPath != null && !returnedPath.isEmpty()) {
                this.selectedImagePath = returnedPath;
                URL imageUrl = getClass().getResource(this.selectedImagePath);
                if (imageUrl != null) {
                    imageViewProduto.setImage(new Image(imageUrl.toExternalForm()));
                    lblMensagem.setText("Imagem selecionada da galeria.");
                } else {
                    lblMensagem.setText("Erro: Imagem selecionada não encontrada nos recursos.");
                    imageViewProduto.setImage(null);
                    this.selectedImagePath = null;
                }
            } else {
                lblMensagem.setText("Nenhuma imagem selecionada.");
            }

        } catch (IOException e) {
            lblMensagem.setText("Erro ao abrir a galeria de imagens: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void voltarParaListagem() {
        try {
            URL fxmlLocation = getClass().getResource("/view/telas/ProdutoListagem.fxml");
            System.out.println("Localização do FXML: " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            txtNome.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}