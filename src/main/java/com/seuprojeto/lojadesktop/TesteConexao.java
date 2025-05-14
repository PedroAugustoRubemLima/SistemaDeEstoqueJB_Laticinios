package com.seuprojeto.lojadesktop;

import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ClienteService;
import com.seuprojeto.lojadesktop.service.FuncionarioService;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TesteConexao implements CommandLineRunner {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ProdutoService produtoService;

    @Override
    public void run(String... args) throws Exception {
        // CRUD Cliente
        System.out.println("----- CRUD CLIENTE -----");
        Cliente cli = new Cliente();
        cli.setNome("Fernando da Silva");
        cli.setCpf("12345678900");
        cli.setTelefone("11999999999");
        clienteService.save(cli);
        System.out.println("Cliente criado: " + cli);

        Optional<Cliente> achadoCli = clienteService.findById(cli.getId());
        achadoCli.ifPresent(value -> System.out.println("Cliente encontrado: " + value));

        cli.setNome("Fernando de Souza");
        clienteService.save(cli);
        System.out.println("Cliente atualizado: " + clienteService.findById(cli.getId()).get());

        clienteService.deleteById(cli.getId());
        System.out.println("Cliente deletado!");

        System.out.println("----- Clientes atualmente no banco: -----");
        List<Cliente> todosClientes = clienteService.findAll();
        todosClientes.forEach(System.out::println);

        // CRUD Funcionário
        System.out.println("\n----- CRUD FUNCIONARIO -----");
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Paula Oliveira");
        funcionarioService.save(funcionario);
        System.out.println("Funcionário criado: " + funcionario);

        Optional<Funcionario> achadoFunc = funcionarioService.findById(funcionario.getIdFuncionario());
        achadoFunc.ifPresent(value -> System.out.println("Funcionário encontrado: " + value));

        funcionario.setNome("Paula da Costa");
        funcionarioService.save(funcionario);
        System.out.println("Funcionário atualizado: " + funcionarioService.findById(funcionario.getIdFuncionario()).get());

        funcionarioService.deleteById(funcionario.getIdFuncionario());
        System.out.println("Funcionário deletado!");

        System.out.println("----- Funcionários atualmente no banco: -----");
        List<Funcionario> todosFuncionarios = funcionarioService.findAll();
        todosFuncionarios.forEach(System.out::println);

        // CRUD Produto
        System.out.println("\n----- CRUD PRODUTO -----");
        Produto prod = new Produto();
        prod.setNome("Teclado Redragon");
        prod.setTipo("Periférico");
        prod.setPreco(119.90);
        prod.setQuantidade(10);
        produtoService.save(prod);
        System.out.println("Produto criado: " + prod);

        Optional<Produto> achadoProd = produtoService.findById(prod.getIdProduto());
        achadoProd.ifPresent(value -> System.out.println("Produto encontrado: " + value));

        prod.setPreco(99.90);
        prod.setQuantidade(10);
        produtoService.save(prod);
        System.out.println("Produto atualizado: " + produtoService.findById(prod.getIdProduto()).get());

        produtoService.deleteById(prod.getIdProduto());
        System.out.println("Produto deletado!");

        System.out.println("----- Produtos atualmente no banco: -----");
        List<Produto> todosProdutos = produtoService.findAll();
        todosProdutos.forEach(System.out::println);

    }
}