package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.Repository.ProdutoRepository;
import com.seuprojeto.lojadesktop.SpringContextHolder;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

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

    public static Produto produtoEditado;

    public void preencherCampos(Produto produto) {
        this.produtoEditado = produto;
        txtNome.setText(produto.getNome());
        txtTipo.setText(produto.getTipo());
        txtPreco.setText(String.valueOf(produto.getPreco()));
    }

    @FXML
    public void cadastrarProduto() {
        System.out.println("Salvando produto...");
        try {
            String nome = txtNome.getText();
            String tipo = txtTipo.getText();
            double preco = Double.parseDouble(txtPreco.getText());

            // Verifica se estamos editando um produto
            if (produtoEditado != null) {
                // Atualiza o produto existente
                produtoEditado.setNome(nome);
                produtoEditado.setTipo(tipo);
                produtoEditado.setPreco(preco);

                // Atualiza no banco de dados
                produtoRepository.save(produtoEditado);

                lblMensagem.setText("Produto atualizado com sucesso!");
            } else {
                // Cria um novo produto
                Produto produto = new Produto(nome, tipo, preco);

                // Salva o novo produto no banco de dados
                produtoRepository.save(produto);

                lblMensagem.setText("Produto salvo com sucesso!");
            }

            // Limpa os campos de entrada
            txtNome.clear();
            txtTipo.clear();
            txtPreco.clear();

            produtoEditado = null;

        } catch (Exception e) {
            lblMensagem.setText("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    public void voltarParaListagem() {
        try {
            URL fxmlLocation = getClass().getResource("/view/telas/ProdutoListagem.fxml");
            System.out.println("Localização do FXML: " + fxmlLocation); // debug importante

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
            AnchorPane pane = loader.load();

            txtNome.getScene().setRoot(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}



