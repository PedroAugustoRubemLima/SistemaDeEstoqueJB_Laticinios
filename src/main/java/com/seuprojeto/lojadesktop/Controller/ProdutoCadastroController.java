package com.seuprojeto.lojadesktop.Controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.Repository.ProdutoRepository;
import com.seuprojeto.lojadesktop.config.SpringContextHolder;
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

    @FXML private TextField codigoBarrasField;

    @FXML
    private DatePicker dataVencimentoPicker;



    public static Produto produtoEditado;

    public void preencherCampos(Produto produto) {
        produtoEditado = produto;
        txtNome.setText(produto.getNome());
        txtTipo.setText(produto.getTipo());
        txtPreco.setText(String.valueOf(produto.getPreco()));
        txtQuantidade.setText(String.valueOf(produto.getQuantidade()));
        codigoBarrasField.setText(produto.getCodigoBarras());
        dataVencimentoPicker.setValue(produto.getDataVencimento());
    }


    @FXML
    public void cadastrarProduto() {
        System.out.println("Salvando produto...");
        try {
            String nome = txtNome.getText();
            String tipo = txtTipo.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            int quantidade = Integer.parseInt(txtQuantidade.getText());
            String codigoBarras = codigoBarrasField.getText();

            if (produtoEditado != null) {
                produtoEditado.setNome(nome);
                produtoEditado.setTipo(tipo);
                produtoEditado.setPreco(preco);
                produtoEditado.setQuantidade(quantidade);
                produtoEditado.setCodigoBarras(codigoBarras);
                produtoEditado.setDataVencimento(dataVencimentoPicker.getValue()); //<-- Aqui adiciona

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

                produtoRepository.save(novoProduto);
                lblMensagem.setText("Produto salvo com sucesso!");
            }

            // Limpa os campos após salvar
            txtNome.clear();
            txtTipo.clear();
            txtPreco.clear();
            txtQuantidade.clear();
            codigoBarrasField.clear(); // <-- limpar também o campo novo

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
