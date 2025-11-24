/*
 * Adaptado de Lista Duplamente Encadeada para Árvore AVL
 */
package br.edu.ufrb.estruturadados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementação de uma Árvore AVL Interativa.
 * Substitui ListaDuplamenteEncadeadaPanel.
 * @author 2401210 
 */
public class ArvoreAVL extends JPanel {

    // 1. CLASSE DO NÓ DA ÁRVORE AVL (Substitui o Nó da Pilha)
    private static class AVLNode {
        int value;
        int height;
        AVLNode left;
        AVLNode right;

        AVLNode(int value) {
            this.value = value;
            this.height = 1; // Novos nós têm altura 1
        }
    }

    // 2. VARIÁVEIS DA ÁRVORE E DO SWING (Substitui head e size da Pilha)
    private AVLNode root; // A raiz da Árvore AVL
    
    // Componentes Swing
    private JTextField campoValor;
    private JButton botaoInserir;
    private VisualizacaoPanel painelDesenho;

    public ArvoreAVL() {
        root = null; // A árvore começa vazia
        
        // --- Configuração do Layout e Controles ---
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        campoValor = new JTextField(8);
        
        // Botão para a operação de Inserção na AVL
        botaoInserir = new JButton("INSERIR E BALANCEAR");
        
        painelControles.add(new JLabel("Valor (Inteiro):"));
        painelControles.add(campoValor);
        painelControles.add(botaoInserir);
        
        // O botão de "Remover" (POP) foi removido, pois a AVL foca na inserção e balanceamento automáticos.
        
        add(painelControles, BorderLayout.NORTH);

        painelDesenho = new VisualizacaoPanel();
        
        // Usamos um JScrollPane para a visualização, caso a árvore fique muito grande
        JScrollPane scrollPane = new JScrollPane(painelDesenho);
        add(scrollPane, BorderLayout.CENTER);

        JTextArea textoDefinicao = new JTextArea(getDefinicao());
        textoDefinicao.setEditable(false);
        textoDefinicao.setFont(new Font("Serif", Font.ITALIC, 14));
        textoDefinicao.setLineWrap(true);
        textoDefinicao.setWrapStyleWord(true);
        JScrollPane definicaoScrollPane = new JScrollPane(textoDefinicao);
        definicaoScrollPane.setPreferredSize(new Dimension(100, 80)); 
        add(definicaoScrollPane, BorderLayout.SOUTH);

        // --- Listeners do Botão ---
        ActionListener insertListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inserirValor();
            }
        };

        botaoInserir.addActionListener(insertListener);
        campoValor.addActionListener(insertListener); // Permite inserir pressionando Enter
    }
    
    private void inserirValor() {
        try {
            int value = Integer.parseInt(campoValor.getText());
            root = insertRecursive(root, value); // Insere e balanceia
            campoValor.setText("");
            // Força o redesenho do painel após a inserção
            painelDesenho.repaint(); 
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, digite um valor inteiro válido.",
                    "Erro de Entrada",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getDefinicao() {
        return "ÁRVORE AVL (Adelson-Velsky e Landis)\n\n" +
                "\u2713 É uma Árvore de Busca Binária (BST) **autobalanceável**.\n" +
                "\u2713 O Fator de Balanceamento (diferença de altura entre subárvores) deve ser no máximo **1** .\n" +
                "\u2713 Se a propriedade AVL for violada, **rotações** (simples ou duplas) são aplicadas para restaurar o balanceamento.";
    }

    // --- 3. LÓGICA DE MANIPULAÇÃO DA ÁRVORE AVL (Com Rotações) ---

    private int getHeight(AVLNode node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalanceFactor(AVLNode node) {
        return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    private void updateHeight(AVLNode node) {
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    // Rotação Simples à Direita (Right Rotation)
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;
        
        // Executa a rotação
        x.right = y;
        y.left = T2;
        
        // Atualiza as alturas em ordem ascendente (filho 'y', depois pai 'x')
        updateHeight(y);
        updateHeight(x);
        
        return x;
    }

    // Rotação Simples à Esquerda (Left Rotation)
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Executa a rotação
        y.left = x;
        x.right = T2;

        // Atualiza as alturas em ordem ascendente (filho 'x', depois pai 'y')
        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Inserção e Balanceamento Principal
    private AVLNode insertRecursive(AVLNode node, int value) {
        // 1. Inserção normal da BST
        if (node == null) {
            return new AVLNode(value);
        }

        if (value < node.value) {
            node.left = insertRecursive(node.left, value);
        } else if (value > node.value) {
            node.right = insertRecursive(node.right, value);
        } else {
            return node; // Valores duplicados não são permitidos
        }

        // 2. Atualiza a altura do nó ancestral
        updateHeight(node);

        // 3. Obtém o Fator de Balanceamento
        int balance = getBalanceFactor(node);

        // 4. Aplica as Rotações (4 casos)
        
        // Caso LL (Left-Left): Desbalanceamento Esquerda-Esquerda
        if (balance > 1 && value < node.left.value) {
            // Rotação Simples à Direita
            return rotateRight(node);
        }

        // Caso RR (Right-Right): Desbalanceamento Direita-Direita
        if (balance < -1 && value > node.right.value) {
            // Rotação Simples à Esquerda
            return rotateLeft(node);
        }

        // Caso LR (Left-Right): Desbalanceamento Esquerda-Direita
        if (balance > 1 && value > node.left.value) {
            // Rotação Dupla: Rotação à Esquerda no filho esquerdo, depois Rotação à Direita no nó atual
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso RL (Right-Left): Desbalanceamento Direita-Esquerda
        if (balance < -1 && value < node.right.value) {
            // Rotação Dupla: Rotação à Direita no filho direito, depois Rotação à Esquerda no nó atual
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // --- 4. CLASSE INTERNA DE DESENHO (Visualização Hierárquica da ÁRVORE) ---
    private class VisualizacaoPanel extends JPanel {
        
        private static final int NODE_RADIUS = 25;
        private static final int VERTICAL_GAP = 70;
        private static final int HORIZONTAL_GAP = 50;

        private class NodeDrawInfo {
            int x, y;
        }

        private Map<AVLNode, NodeDrawInfo> nodePositions = new HashMap<>();

        public VisualizacaoPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 600)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            if (root != null) {
                // 1. Calcula as posições dos nós
                calculateNodePositions(root, getWidth(), 0);

                // 2. Desenha as arestas
                drawEdges(g2d, root);

                // 3. Desenha os nós
                drawNodes(g2d, root);
            } else {
                 g2d.drawString("Árvore AVL Vazia. Insira um valor para começar.", getWidth() / 2 - 150, getHeight() / 2);
            }
        }
        
        // Algoritmo de layout baseado em BFS para garantir que nós do mesmo nível estejam alinhados
        private void calculateNodePositions(AVLNode root, int panelWidth, int yStart) {
            nodePositions.clear();
            if (root == null) return;

            Queue<AVLNode> queue = new LinkedList<>();
            queue.add(root);
            List<List<AVLNode>> nodesByDepth = new ArrayList<>();
            
            while (!queue.isEmpty()) {
                int levelSize = queue.size();
                List<AVLNode> currentLevel = new ArrayList<>();
                
                for (int i = 0; i < levelSize; i++) {
                    AVLNode node = queue.poll();
                    currentLevel.add(node);
                    if (node.left != null) queue.add(node.left);
                    if (node.right != null) queue.add(node.right);
                }
                nodesByDepth.add(currentLevel);
            }
            
            // Atribui as coordenadas (x, y)
            for (int depth = 0; depth < nodesByDepth.size(); depth++) {
                List<AVLNode> level = nodesByDepth.get(depth);
                int numNodes = level.size();
                
                // Calcula o espaçamento
                int totalWidth = panelWidth - 2 * HORIZONTAL_GAP;
                int spacing = numNodes > 1 ? totalWidth / (numNodes - 1) : 0;
                
                // Posição y
                int y = depth * VERTICAL_GAP + NODE_RADIUS + yStart;
                
                for (int i = 0; i < numNodes; i++) {
                    AVLNode node = level.get(i);
                    
                    // Posição x
                    int x;
                    if (numNodes == 1) {
                        x = panelWidth / 2;
                    } else {
                        x = HORIZONTAL_GAP + i * spacing;
                    }
                    
                    NodeDrawInfo info = new NodeDrawInfo();
                    info.x = x;
                    info.y = y;
                    nodePositions.put(node, info);
                }
            }
            
            // Ajusta o tamanho do painel para caber toda a árvore
            int requiredHeight = nodesByDepth.size() * VERTICAL_GAP + NODE_RADIUS * 2;
            if (requiredHeight > getHeight()) {
                setPreferredSize(new Dimension(getWidth(), requiredHeight));
                revalidate();
            }
        }


        private void drawEdges(Graphics2D g2d, AVLNode node) {
            if (node == null) return;

            NodeDrawInfo parentPos = nodePositions.get(node);

            // Aresta Esquerda
            if (node.left != null) {
                NodeDrawInfo childPos = nodePositions.get(node.left);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(parentPos.x, parentPos.y, childPos.x, childPos.y);
                drawEdges(g2d, node.left);
            }

            // Aresta Direita
            if (node.right != null) {
                NodeDrawInfo childPos = nodePositions.get(node.right);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(parentPos.x, parentPos.y, childPos.x, childPos.y);
                drawEdges(g2d, node.right);
            }
            g2d.setStroke(new BasicStroke(1));
        }

        private void drawNodes(Graphics2D g2d, AVLNode node) {
            if (node == null) return;

            NodeDrawInfo pos = nodePositions.get(node);

            // Desenha o círculo
            g2d.setColor(new Color(173, 216, 230)); // Azul claro
            g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Desenha a borda
            g2d.setColor(Color.BLUE.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
            
            // Desenha o valor
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String valueStr = String.valueOf(node.value);
            FontMetrics fm = g2d.getFontMetrics();
            int x = pos.x - fm.stringWidth(valueStr) / 2;
            int y = pos.y + fm.getAscent() / 2 - 2;
            g2d.drawString(valueStr, x, y);

            // Desenha o Fator de Balanceamento (FB)
            int balance = getBalanceFactor(node);
            Color fbColor = balance == 0 ? Color.DARK_GRAY : (Math.abs(balance) > 1 ? Color.RED : Color.ORANGE);
            g2d.setColor(fbColor);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String fbStr = "FB: " + balance;
            g2d.drawString(fbStr, pos.x - fm.stringWidth(fbStr) / 2, pos.y + NODE_RADIUS + 10);
            
            drawNodes(g2d, node.left);
            drawNodes(g2d, node.right);
        }
    }
    
    // --- 5. MÉTODO MAIN ---

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Cria o JFrame principal
            JFrame frame = new JFrame("Árvore AVL Interativa - br.edu.ufrb.estruturadados");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ArvoreAVL());
            frame.pack(); // Ajusta o tamanho com base nos componentes
            frame.setLocationRelativeTo(null); // Centraliza a janela
            frame.setVisible(true);
        });
    }
}