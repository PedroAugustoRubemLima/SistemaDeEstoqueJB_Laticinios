package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.Repository.ProdutoRepository;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
    private Label lblMensagem;

    @FXML
    public void cadastrarProduto() {
        try {
            String nome = txtNome.getText();
            String tipo = txtTipo.getText();
            double preco = Double.parseDouble(txtPreco.getText());

            Produto produto = new Produto(nome, tipo, preco);
            produtoRepository.save(produto);

            lblMensagem.setText("Produto salvo com sucesso!");

            txtNome.clear();
            txtTipo.clear();
            txtPreco.clear();
        } catch (Exception e) {
            lblMensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    public void voltarParaListagem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/ProdutoListagem.fxml"));
            AnchorPane pane = loader.load();
            txtNome.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



