package br.edu.ufrb.estruturadados;

import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class VisualizadorFrame extends JFrame {

    private JComboBox<String> seletorDeEstrutura;
    private JPanel painelDeVisualizacao;
    private CardLayout cardLayout;

    private static final int LARGURA_JANELA = 1000;
    private static final int ALTURA_JANELA = 700;
    private static final String TITULO_APLICACAO = "Visualizador de Estruturas de Dados";

    public VisualizadorFrame() {
        configurarJanela();
        inicializarComponentes();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle(TITULO_APLICACAO);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Tema.BACKGROUND);
    }

    private void inicializarComponentes() {
        add(criarPainelSuperior(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarPainelRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelSuperior() {
        JPanel painelSuperior = ComponentesUI.criarPainelGradiente(
                Tema.PRIMARY_DARK, Tema.PRIMARY
        );
        painelSuperior.setLayout(new BorderLayout(15, 0));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setOpaque(false);

        JLabel titulo = ComponentesUI.criarLabelEstilizado(TITULO_APLICACAO, "heading");
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelTitulo.add(Box.createVerticalGlue());
        painelTitulo.add(titulo);
        painelTitulo.add(Box.createVerticalGlue());

        painelSuperior.add(painelTitulo, BorderLayout.WEST);
        painelSuperior.add(criarPainelSeletor(), BorderLayout.EAST);

        return painelSuperior;
    }

    private JPanel criarPainelSeletor() {
        JPanel painelSeletor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelSeletor.setOpaque(false);

        JLabel labelSeletor = ComponentesUI.criarLabelEstilizado(
                "Selecione a Estrutura:", "subheading");
        labelSeletor.setForeground(Color.WHITE);
        painelSeletor.add(labelSeletor);

        seletorDeEstrutura = ComponentesUI.criarComboBoxModerno(obterNomesEstruturas());
        seletorDeEstrutura.setBackground(Color.WHITE);
        seletorDeEstrutura.setForeground(Tema.TEXT_PRIMARY);
        seletorDeEstrutura.setPreferredSize(new Dimension(280, 35));

        seletorDeEstrutura.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String estruturaSelecionada = (String) e.getItem();
                cardLayout.show(painelDeVisualizacao, estruturaSelecionada);
            }
        });

        painelSeletor.add(seletorDeEstrutura);
        return painelSeletor;
    }

    private JPanel criarPainelCentral() {
        cardLayout = new CardLayout();
        painelDeVisualizacao = new JPanel(cardLayout);
        painelDeVisualizacao.setBackground(Tema.BACKGROUND);

        registrarPaineisDeEstruturas();

        return painelDeVisualizacao;
    }

    private void registrarPaineisDeEstruturas() {
        // Mapa de estruturas (nome -> painel)
        String[] nomes = obterNomesEstruturas();
        JPanel[] paineis = {
            new ArrayPanel(),
            new ListaLigadaPanel(),
            new ListaDuplamenteEncadeadaPanel(),
            new PilhaSwingApp(),
            new FilaSwingApp(),
            new OrdenacaoPanel(),
            new BuscaBinariaPanel(),
            new ArvoreBinariaNaoOrdenadaPanel(),
            new ArvoreBinariaNaoBalanceadaPanel(),
            new HashMapPanel(),
            new HashMapDinamicoPanel(),
            new ArvoreAVL(),
            new GrafosDijkstraPanel()
        };

        for (int i = 0; i < nomes.length; i++) {
            painelDeVisualizacao.add(paineis[i], nomes[i]);
        }
    }

    private String[] obterNomesEstruturas() {
        return new String[]{
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
            "Hash Map Dinâmico (Linear Probing)",
            "Árvore AVL",
            "Grafos (BFS e Dijkstra)"
        };
    }

    private JPanel criarPainelRodape() {
        JPanel painelRodape = ComponentesUI.criarPainelModerno();
        painelRodape.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel labelInfo = ComponentesUI.criarLabelEstilizado(
                "© 2025 | UFRB | Programa de Pós-graduação em Engenharia Elétrica e Computação | Estrutura de Dados | Versão 1.0",
                 "body");
        labelInfo.setForeground(Tema.TEXT_SECONDARY);

        painelRodape.add(labelInfo);
        return painelRodape;
    }
}
