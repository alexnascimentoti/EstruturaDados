/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ufrb.estruturadados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 *
 * @author 2401210
 */
public class VisualizadorFrame extends JFrame {
     private JComboBox<String> seletorDeEstrutura;
    private JPanel painelDeVisualizacao;
    private CardLayout cardLayout;

    public VisualizadorFrame() {
        // Configurações básicas da janela
        setTitle("Visualizador de Estruturas de Dados");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        // Layout principal
        setLayout(new BorderLayout(10, 10));

        // --- Painel Superior: Seletor ---
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painelSuperior.add(new JLabel("Selecione a Estrutura:"));

        String[] estruturas = {
            "Array (Lista Dinâmica)", 
            "Lista Ligada (Simples)", 
            "Lista Duplamente Encadeada",
            "Pilha",
            "Fila",
            "Métodos de Ordenação",
            "Busca Binária",
            "Árvore Binária Não Ordenada",
            "Árvore Binária de Busca (Não Balanceada)",
            "Hash Map Simples (Tabela Hash)",
            "Árvore AVL",
            "Grafos (BFS e Dijkstra)"
        };
        seletorDeEstrutura = new JComboBox<>(estruturas);
        painelSuperior.add(seletorDeEstrutura);

        add(painelSuperior, BorderLayout.NORTH);

        // --- Painel Central: Visualização (com CardLayout) ---
        cardLayout = new CardLayout();
        painelDeVisualizacao = new JPanel(cardLayout);

        // Adiciona os painéis de cada estrutura
        painelDeVisualizacao.add(new ArrayPanel(), "Array (Lista Dinâmica)");
        painelDeVisualizacao.add(new ListaLigadaPanel(), "Lista Ligada (Simples)");
        painelDeVisualizacao.add(new ListaDuplamenteEncadeadaPanel(), "Lista Duplamente Encadeada");
        painelDeVisualizacao.add(new PilhaSwingApp(), "Pilha");
        painelDeVisualizacao.add(new FilaSwingApp(), "Fila");
        painelDeVisualizacao.add(new OrdenacaoPanel(), "Métodos de Ordenação");
        painelDeVisualizacao.add(new BuscaBinariaPanel(), "Busca Binária");
        painelDeVisualizacao.add(new ArvoreBinariaNaoOrdenadaPanel(), "Árvore Binária Não Ordenada");
        painelDeVisualizacao.add(new ArvoreBinariaNaoBalanceadaPanel(), "Árvore Binária de Busca (Não Balanceada)");
        painelDeVisualizacao.add(new HashMapPanel(), "Hash Map Simples (Tabela Hash)");
        painelDeVisualizacao.add(new ArvoreAVL(), "Árvore AVL");
        painelDeVisualizacao.add(new GrafosDijkstraPanel(), "Grafos (BFS e Dijkstra)");

        add(painelDeVisualizacao, BorderLayout.CENTER);

        // --- Lógica para trocar de painel ---
        seletorDeEstrutura.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                cardLayout.show(painelDeVisualizacao, (String) e.getItem());
            }
        });
    }
}
