package br.edu.ufrb.estruturadados;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ArvoreAVL extends JPanel {

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

    private AVLNode root; // A raiz da Árvore AVL

    // Componentes Swing
    private JTextField campoValor;
    private JButton botaoInserir, botaoLimpar;
    private VisualizacaoPanel painelDesenho;

    public ArvoreAVL() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Tema.BACKGROUND);

        JPanel painelControles = ComponentesUI.criarPainelModerno();
        painelControles.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        campoValor = ComponentesUI.criarCampoTextoModerno(8);
        botaoInserir = ComponentesUI.criarBotaoModerno("Inserir", Tema.PRIMARY);
        botaoLimpar = ComponentesUI.criarBotaoModerno("Limpar", Tema.WARNING);

        painelControles.add(ComponentesUI.criarLabelEstilizado("Valor:", "subheading"));
        painelControles.add(campoValor);
        painelControles.add(botaoInserir);
        painelControles.add(botaoLimpar);

        add(painelControles, BorderLayout.NORTH);

        painelDesenho = new VisualizacaoPanel();

        add(new JScrollPane(painelDesenho), BorderLayout.CENTER);

        JTextArea textoDefinicao = ComponentesUI.criarAreaTextoEstilizada();
        textoDefinicao.setText(getDefinicao());
        JScrollPane scrollDef = new JScrollPane(textoDefinicao);
        scrollDef.setBorder(null);
        scrollDef.setPreferredSize(new Dimension(Tema.FIELD_WIDTH_CODE, Tema.FIELD_HEIGHT_INFO));
        add(scrollDef, BorderLayout.SOUTH);

        botaoInserir.addActionListener(e -> inserirValor());
        botaoLimpar.addActionListener(e -> {
            root = null;
            painelDesenho.repaint();
        });
    }

    private void inserirValor() {
        try {
            int value = Integer.parseInt(campoValor.getText());
            root = insertRecursive(root, value); // Insere e balanceia
            campoValor.setText("");
            painelDesenho.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, digite um valor inteiro válido.",
                    "Erro de Entrada",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getDefinicao() {
        return "ÁRVORE AVL (Adelson-Velsky e Landis)\n\n"
                + "\u2713 É uma Árvore de Busca Binária (BST) autobalanceável.\n"
                + "\u2713 O Fator de Balanceamento (diferença de altura entre subárvores) deve ser no máximo 1 .\n"
                + "\u2713 Se a propriedade AVL for violada, rotações (simples ou duplas) são aplicadas para restaurar o balanceamento.";
    }

    // --- LÓGICA DE MANIPULAÇÃO DA ÁRVORE AVL (Com Rotações) ---
    private int getHeight(AVLNode node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalanceFactor(AVLNode node) {
        return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    private void updateHeight(AVLNode node) {
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    // Rotação Simples à Direita
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

    // Rotação Simples à Esquerda
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

        // 2. Atualiza a altura do nó
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

    private class VisualizacaoPanel extends JPanel {

        private static final int RADIUS = 20;
        private static final int V_GAP = 60;
        private final Map<AVLNode, Point> positions = new HashMap<>();
        private int minX, maxX, minY, maxY;

        public VisualizacaoPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Tema.BORDER));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            positions.clear();
            minX = Integer.MAX_VALUE;
            maxX = Integer.MIN_VALUE;
            minY = Integer.MAX_VALUE;
            maxY = Integer.MIN_VALUE;

            if (root != null) {
                calculatePositions(root, getWidth() / 2, 40, getWidth() / 4);
                drawEdges(g2d, root);
                drawNodes(g2d, root);
            } else {
                g2d.setFont(Tema.FONT_SUBHEADING);
                g2d.setColor(Tema.TEXT_SECONDARY);
                g2d.drawString("Árvore vazia. Adicione elementos para visualizar.", getWidth() / 2 - 40, getHeight() / 2);
            }

            int width = (maxX - minX) + 200;
            int height = (maxY - minY) + 200;

            setPreferredSize(new Dimension(width, height));
            revalidate();
        }

        private void calculatePositions(AVLNode node, int x, int y, int offset) {
            if (node == null) {
                return;
            }
            positions.put(node, new Point(x, y));

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            calculatePositions(node.left, x - offset, y + V_GAP, offset / 2);
            calculatePositions(node.right, x + offset, y + V_GAP, offset / 2);
        }

        private void drawEdges(Graphics2D g, AVLNode node) {
            if (node == null) {
                return;
            }
            Point p = positions.get(node);
            if (node.left != null) {
                Point pl = positions.get(node.left);
                g.setColor(Tema.TREE_EDGE);
                g.setStroke(new BasicStroke(2));
                g.drawLine(p.x, p.y, pl.x, pl.y);
                drawEdges(g, node.left);
            }
            if (node.right != null) {
                Point pr = positions.get(node.right);
                g.setColor(Tema.TREE_EDGE);
                g.setStroke(new BasicStroke(2));
                g.drawLine(p.x, p.y, pr.x, pr.y);
                drawEdges(g, node.right);
            }
        }

        private void drawNodes(Graphics2D g, AVLNode node) {
            if (node == null) {
                return;
            }
            Point p = positions.get(node);

            // Gradiente no nó
            GradientPaint gp = new GradientPaint(
                    p.x - RADIUS, p.y - RADIUS, Tema.TREE_NODE,
                    p.x + RADIUS, p.y + RADIUS, Tema.TREE_NODE.darker()
            );
            g.setPaint(gp);
            g.fillOval(p.x - RADIUS, p.y - RADIUS, 2 * RADIUS, 2 * RADIUS);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2));
            g.drawOval(p.x - RADIUS, p.y - RADIUS, 2 * RADIUS, 2 * RADIUS);

            g.setColor(Color.WHITE);
            g.setFont(Tema.FONT_BUTTON);
            String val = String.valueOf(node.value);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(val, p.x - fm.stringWidth(val) / 2, p.y + fm.getAscent() / 2 - 2);

            int balance = getBalanceFactor(node);
            Color fbColor = balance == 0 ? Color.DARK_GRAY : (Math.abs(balance) > 1 ? Color.RED : Color.ORANGE);
            g.setColor(fbColor);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String fbStr = "FB: " + balance;
            g.drawString(fbStr, p.x - fm.stringWidth(fbStr) / 2, p.y + RADIUS + 10);

            drawNodes(g, node.left);
            drawNodes(g, node.right);
        }
    }
}
