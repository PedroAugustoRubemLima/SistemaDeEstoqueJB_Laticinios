package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.Repository.ProdutoRepository;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

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

            // Limpar os campos
            txtNome.clear();
            txtTipo.clear();
            txtPreco.clear();
        } catch (Exception e) {
            lblMensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }
}


