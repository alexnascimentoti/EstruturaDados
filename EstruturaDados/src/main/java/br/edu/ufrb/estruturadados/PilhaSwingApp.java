package br.edu.ufrb.estruturadados;

import java.awt.*;
import javax.swing.*;

public class PilhaSwingApp extends JPanel {

    private static class Node {

        String data;
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }

    // Na Pilha, 'head' será nosso 'TOPO'.
    private Node head; // Ponteiro para o TOPO da pilha

    private JTextField campoValor;
    private JButton botaoPush, botaoPop;
    private VisualizacaoPanel painelDesenho;

    public PilhaSwingApp() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Tema.BACKGROUND);

        JPanel painelControles = ComponentesUI.criarPainelModerno();
        painelControles.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        campoValor = ComponentesUI.criarCampoTextoModerno(8);
        botaoPush = ComponentesUI.criarBotaoModerno("PUSH (inserir)", Tema.SUCCESS);
        botaoPop = ComponentesUI.criarBotaoModerno("POP (remover do topo)", Tema.ERROR);

        painelControles.add(ComponentesUI.criarLabelEstilizado("Valor:", "subheading"));
        painelControles.add(campoValor);
        painelControles.add(botaoPush);
        painelControles.add(botaoPop);

        add(painelControles, BorderLayout.NORTH);

        painelDesenho = new VisualizacaoPanel();
        add(new JScrollPane(painelDesenho), BorderLayout.CENTER);

        JTextArea textoDefinicao = ComponentesUI.criarAreaTextoEstilizada();
        textoDefinicao.setText(getDefinicao());
        JScrollPane scrollPane = new JScrollPane(textoDefinicao);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(Tema.FIELD_WIDTH_CODE, Tema.FIELD_HEIGHT_INFO));
        add(scrollPane, BorderLayout.SOUTH);

        botaoPush.addActionListener(e -> push());
        botaoPop.addActionListener(e -> pop());

    }

    private String getDefinicao() {
        return "PILHA (LIFO - Last-In, First-Out)\n\n"
                + "\u2713 Estrutura de dados abastrata que organiza elementos seguindo o princípio do 'Ultimo a entrar, Primeiro a sair'\n"
                + "\u2713 A inserção (PUSH) e remoção (POP) ocorrem apenas no TOPO (HEAD). \n"
                + "\u2713 A referência 'next' (ponteiro) é usada para apontar para o elemento que está abaixo na pilha.\n"
                + "\u2713 Exemplos práticos: histórico de navegação de um navegador e a funcionalidade de \"desfazer/refazer\" em editores de texto.\n ";

    }

    // --- LÓGICA DE MANIPULAÇÃO DA PILHA ---
    private void push() {
        String valor = campoValor.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um valor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Node novoNo = new Node(valor);

        // Novo nó aponta para o antigo topo
        novoNo.next = head;

        // O topo (head) é atualizado para o novo nó
        head = novoNo;

        campoValor.setText("");
        painelDesenho.repaint();
    }

    private void pop() {
        if (head == null) {
            JOptionPane.showMessageDialog(this, "A pilha está vazia!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // O dado do topo é salvo para ser exibido
        String dadoRemovido = head.data;

        // O topo (head) avança para o próximo nó (o antigo segundo elemento)
        head = head.next;

        painelDesenho.repaint();

        JOptionPane.showMessageDialog(this, "Elemento removido (POP): " + dadoRemovido, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- CLASSE INTERNA DE DESENHO (Visualização LIFO) ---
    private class VisualizacaoPanel extends JPanel {

        public VisualizacaoPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Tema.BORDER));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (head == null) {
                g2d.setFont(Tema.FONT_SUBHEADING);
                g2d.setColor(Tema.TEXT_SECONDARY);
                g2d.drawString("Pilha Vazia.", getWidth() / 2 - 40, getHeight() / 2);
                return;
            }

            int width = 120;
            int height = 45;
            int x = (getWidth() - width) / 2;
            int y = 30;

            // Desenha indicação de Topo
            g2d.setColor(Tema.STACK_TOP);
            g2d.setFont(Tema.FONT_HEADING);
            g2d.drawString("TOPO (HEAD)", x - 150, y + 30);

            Node atual = head;
            while (atual != null) {
                // Caixa
                Color cor = (atual == head) ? Tema.STACK_TOP : Tema.STACK_BODY;

                GradientPaint gp = new GradientPaint(x, y, cor, x, y + height, cor.darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(x, y, width, height, 10, 10);

                g2d.setColor(Tema.PRIMARY_DARK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(x, y, width, height, 10, 10);

                // Texto
                g2d.setColor(Color.WHITE);
                g2d.setFont(Tema.FONT_HEADING);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(atual.data, x + (width - fm.stringWidth(atual.data)) / 2, y + 30);

                // Conector visual
                if (atual.next != null) {
                    g2d.setColor(Tema.TEXT_SECONDARY);
                    g2d.drawLine(x + width / 2, y + height, x + width / 2, y + height + 10);
                }

                y += height + 10;
                atual = atual.next;
            }
            setPreferredSize(new Dimension(200, y));
            revalidate();
        }
    }
}
