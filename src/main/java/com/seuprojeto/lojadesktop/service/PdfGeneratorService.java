package com.seuprojeto.lojadesktop.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

// Importe as classes de modelo que serão usadas nos relatórios
import com.seuprojeto.lojadesktop.model.Compra;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.controller.HistoricoVendasController.RelatorioVendasItem;
import com.seuprojeto.lojadesktop.controller.HistoricoVendasController.ProdutoMaisVendidoItem;
import com.seuprojeto.lojadesktop.controller.HistoricoVendasController.EstoqueBaixoItem;
import com.seuprojeto.lojadesktop.controller.HistoricoVendasController.ProximoVencimentoItem;


@Service
public class PdfGeneratorService {

  // Método auxiliar para adicionar uma nova página e iniciar o content stream
  private PDPageContentStream startNewPage(PDDocument document, PDPage currentPage, PDPageContentStream currentStream) throws IOException {
    if (currentStream != null) {
      currentStream.close(); // Fecha o stream da página anterior
    }
    PDPage newPage = new PDPage();
    document.addPage(newPage);
    return new PDPageContentStream(document, newPage);
  }

  // Método para gerar o PDF do Histórico Detalhado de Vendas
  public void generateHistoricoVendasPdf(List<Compra> vendas, String filePath) throws IOException {
    try (PDDocument document = new PDDocument()) {
      PDPageContentStream contentStream = null;
      float y = 0; // Inicializa y para controle de nova página
      float margin = 50;
      float rowHeight = 20;
      float cellMargin = 5;
      float startY = 750; // Posição inicial Y para o conteúdo

      try {
        contentStream = startNewPage(document, null, null); // Inicia a primeira página
        y = startY;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Relatório de Histórico Detalhado de Vendas");
        contentStream.endText();
        y -= 30; // Espaço após o título

        // Cabeçalho da tabela
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        String[] headers = {"Cliente", "Funcionário", "Data", "Valor Total"};
        float[] columnWidths = {150, 150, 100, 100};

        float nextX;
        // Desenha o cabeçalho
        nextX = margin;
        for (int i = 0; i < headers.length; i++) {
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(headers[i]);
          contentStream.endText();
          nextX += columnWidths[i];
        }
        y -= rowHeight; // Espaço após o cabeçalho

        // Desenha as linhas da tabela
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        for (Compra venda : vendas) {
          if (y < margin) { // Se a página estiver cheia, cria uma nova
            contentStream = startNewPage(document, null, contentStream);
            y = startY; // Reinicia a posição Y na nova página

            // Redesenha o cabeçalho na nova página
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            nextX = margin;
            for (int i = 0; i < headers.length; i++) {
              contentStream.beginText();
              contentStream.newLineAtOffset(nextX + cellMargin, y);
              contentStream.showText(headers[i]);
              contentStream.endText();
              nextX += columnWidths[i];
            }
            y -= rowHeight;
            contentStream.setFont(PDType1Font.HELVETICA, 9);
          }

          nextX = margin;
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(venda.getCliente().getNome());
          nextX += columnWidths[0];
          contentStream.newLineAtOffset(columnWidths[0], 0);
          contentStream.showText(venda.getFuncionario().getNome());
          nextX += columnWidths[1];
          contentStream.newLineAtOffset(columnWidths[1], 0);
          contentStream.showText(venda.getData().toString());
          nextX += columnWidths[2];
          contentStream.newLineAtOffset(columnWidths[2], 0);
          contentStream.showText(String.format("R$ %.2f", venda.getValorTotal()));
          contentStream.endText();
          y -= rowHeight;
        }
      } finally {
        if (contentStream != null) {
          contentStream.close(); // Garante que o último stream seja fechado
        }
      }
      document.save(filePath);
    }
  }

  // Método para gerar o PDF do Relatório de Vendas por Período
  public void generateRelatorioVendasPorPeriodoPdf(List<RelatorioVendasItem> vendasPorPeriodo, String filePath) throws IOException {
    try (PDDocument document = new PDDocument()) {
      PDPageContentStream contentStream = null;
      float y = 0;
      float margin = 50;
      float rowHeight = 20;
      float cellMargin = 5;
      float startY = 750;

      try {
        contentStream = startNewPage(document, null, null);
        y = startY;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Relatório de Vendas por Período");
        contentStream.endText();
        y -= 30;

        // Cabeçalho da tabela
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        String[] headers = {"Data da Venda", "Total de Vendas (R$)"};
        float[] columnWidths = {150, 150};

        float nextX;
        nextX = margin;
        for (int i = 0; i < headers.length; i++) {
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(headers[i]);
          contentStream.endText();
          nextX += columnWidths[i];
        }
        y -= rowHeight;

        // Desenha as linhas da tabela
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        for (RelatorioVendasItem item : vendasPorPeriodo) {
          if (y < margin) {
            contentStream = startNewPage(document, null, contentStream);
            y = startY;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            nextX = margin;
            for (int i = 0; i < headers.length; i++) {
              contentStream.beginText();
              contentStream.newLineAtOffset(nextX + cellMargin, y);
              contentStream.showText(headers[i]);
              contentStream.endText();
              nextX += columnWidths[i];
            }
            y -= rowHeight;
            contentStream.setFont(PDType1Font.HELVETICA, 9);
          }

          nextX = margin;
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(item.getDataVenda().toString());
          nextX += columnWidths[0];
          contentStream.newLineAtOffset(columnWidths[0], 0);
          contentStream.showText(String.format("R$ %.2f", item.getTotalVendas()));
          contentStream.endText();
          y -= rowHeight;
        }
      } finally {
        if (contentStream != null) {
          contentStream.close();
        }
      }
      document.save(filePath);
    }
  }

  // Método para gerar o PDF do Relatório de Produtos Mais Vendidos
  public void generateProdutosMaisVendidosPdf(List<ProdutoMaisVendidoItem> produtosMaisVendidos, String filePath) throws IOException {
    try (PDDocument document = new PDDocument()) {
      PDPageContentStream contentStream = null;
      float y = 0;
      float margin = 50;
      float rowHeight = 20;
      float cellMargin = 5;
      float startY = 750;

      try {
        contentStream = startNewPage(document, null, null);
        y = startY;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Relatório de Produtos Mais Vendidos");
        contentStream.endText();
        y -= 30;

        // Cabeçalho da tabela
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        String[] headers = {"Produto", "Tipo", "Qtd. Vendida (Caixas)"};
        float[] columnWidths = {200, 150, 150};

        float nextX;
        nextX = margin;
        for (int i = 0; i < headers.length; i++) {
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(headers[i]);
          contentStream.endText();
          nextX += columnWidths[i];
        }
        y -= rowHeight;

        // Desenha as linhas da tabela
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        for (ProdutoMaisVendidoItem item : produtosMaisVendidos) {
          if (y < margin) {
            contentStream = startNewPage(document, null, contentStream);
            y = startY;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            nextX = margin;
            for (int i = 0; i < headers.length; i++) {
              contentStream.beginText();
              contentStream.newLineAtOffset(nextX + cellMargin, y);
              contentStream.showText(headers[i]);
              contentStream.endText();
              nextX += columnWidths[i];
            }
            y -= rowHeight;
            contentStream.setFont(PDType1Font.HELVETICA, 9);
          }

          nextX = margin;
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(item.getNomeProduto());
          nextX += columnWidths[0];
          contentStream.newLineAtOffset(columnWidths[0], 0);
          contentStream.showText(item.getTipoProduto());
          nextX += columnWidths[1];
          contentStream.newLineAtOffset(columnWidths[1], 0);
          contentStream.showText(String.valueOf(item.getQuantidadeVendida()));
          contentStream.endText();
          y -= rowHeight;
        }
      } finally {
        if (contentStream != null) {
          contentStream.close();
        }
      }
      document.save(filePath);
    }
  }

  // Método para gerar o PDF do Relatório de Produtos com Baixo Estoque
  public void generateEstoqueBaixoPdf(List<EstoqueBaixoItem> estoqueBaixo, String filePath) throws IOException {
    try (PDDocument document = new PDDocument()) {
      PDPageContentStream contentStream = null;
      float y = 0;
      float margin = 50;
      float rowHeight = 20;
      float cellMargin = 5;
      float startY = 750;

      try {
        contentStream = startNewPage(document, null, null);
        y = startY;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Relatório de Produtos com Baixo Estoque");
        contentStream.endText();
        y -= 30;

        // Cabeçalho da tabela
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        String[] headers = {"Produto", "Tipo", "Qtd. Atual (KG)"};
        float[] columnWidths = {200, 150, 150};

        float nextX;
        nextX = margin;
        for (int i = 0; i < headers.length; i++) {
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(headers[i]);
          contentStream.endText();
          nextX += columnWidths[i];
        }
        y -= rowHeight;

        // Desenha as linhas da tabela
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        for (EstoqueBaixoItem item : estoqueBaixo) {
          if (y < margin) {
            contentStream = startNewPage(document, null, contentStream);
            y = startY;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            nextX = margin;
            for (int i = 0; i < headers.length; i++) {
              contentStream.beginText();
              contentStream.newLineAtOffset(nextX + cellMargin, y);
              contentStream.showText(headers[i]);
              contentStream.endText();
              nextX += columnWidths[i];
            }
            y -= rowHeight;
            contentStream.setFont(PDType1Font.HELVETICA, 9);
          }

          nextX = margin;
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(item.getNomeProduto());
          nextX += columnWidths[0];
          contentStream.newLineAtOffset(columnWidths[0], 0);
          contentStream.showText(item.getTipoProduto());
          nextX += columnWidths[1];
          contentStream.newLineAtOffset(columnWidths[1], 0);
          contentStream.showText(String.format("%.2f", item.getQuantidadeAtual()));
          contentStream.endText();
          y -= rowHeight;
        }
      } finally {
        if (contentStream != null) {
          contentStream.close();
        }
      }
      document.save(filePath);
    }
  }

  // Método para gerar o PDF do Relatório de Produtos Próximos do Vencimento
  public void generateProximoVencimentoPdf(List<ProximoVencimentoItem> proximoVencimento, String filePath) throws IOException {
    try (PDDocument document = new PDDocument()) {
      PDPageContentStream contentStream = null;
      float y = 0;
      float margin = 50;
      float rowHeight = 20;
      float cellMargin = 5;
      float startY = 750;

      try {
        contentStream = startNewPage(document, null, null);
        y = startY;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Relatório de Produtos Próximos do Vencimento");
        contentStream.endText();
        y -= 30;

        // Cabeçalho da tabela
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        String[] headers = {"Produto", "Tipo", "Data Vencimento"};
        float[] columnWidths = {200, 150, 150};

        float nextX;
        nextX = margin;
        for (int i = 0; i < headers.length; i++) {
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(headers[i]);
          contentStream.endText();
          nextX += columnWidths[i];
        }
        y -= rowHeight;

        // Desenha as linhas da tabela
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        for (ProximoVencimentoItem item : proximoVencimento) {
          if (y < margin) {
            contentStream = startNewPage(document, null, contentStream);
            y = startY;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            nextX = margin;
            for (int i = 0; i < headers.length; i++) {
              contentStream.beginText();
              contentStream.newLineAtOffset(nextX + cellMargin, y);
              contentStream.showText(headers[i]);
              contentStream.endText();
              nextX += columnWidths[i];
            }
            y -= rowHeight;
            contentStream.setFont(PDType1Font.HELVETICA, 9);
          }

          nextX = margin;
          contentStream.beginText();
          contentStream.newLineAtOffset(nextX + cellMargin, y);
          contentStream.showText(item.getNomeProduto());
          nextX += columnWidths[0];
          contentStream.newLineAtOffset(columnWidths[0], 0);
          contentStream.showText(item.getTipoProduto());
          nextX += columnWidths[1];
          contentStream.newLineAtOffset(columnWidths[1], 0);
          contentStream.showText(item.getDataVencimento().toString());
          contentStream.endText();
          y -= rowHeight;
        }
      } finally {
        if (contentStream != null) {
          contentStream.close();
        }
      }
      document.save(filePath);
    }
  }
}