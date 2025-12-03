package br.edu.ufrb.estruturadados;

import java.awt.*;
import javax.swing.*;

public class FilaSwingApp extends JPanel {

    private static class Node {

        String data;
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front; // INÍCIO da fila (quem será removido)
    private Node rear;  // FIM da fila (onde insere)

    private JTextField campoValor;
    private JButton botaoEnqueue, botaoDequeue;
    private VisualizacaoPanel painelDesenho;

    public FilaSwingApp() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Tema.BACKGROUND);

        JPanel painelControles = ComponentesUI.criarPainelModerno();
        painelControles.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        campoValor = ComponentesUI.criarCampoTextoModerno(8);
        botaoEnqueue = ComponentesUI.criarBotaoModerno("ENQUEUE (inserir)", Tema.SUCCESS);
        botaoDequeue = ComponentesUI.criarBotaoModerno("DEQUEUE (remover)", Tema.ERROR);

        painelControles.add(ComponentesUI.criarLabelEstilizado("Valor:", "subheading"));
        painelControles.add(campoValor);
        painelControles.add(botaoEnqueue);
        painelControles.add(botaoDequeue);

        add(painelControles, BorderLayout.NORTH);

        painelDesenho = new VisualizacaoPanel();
        add(new JScrollPane(painelDesenho), BorderLayout.CENTER);

        JTextArea textoDefinicao = ComponentesUI.criarAreaTextoEstilizada();
        textoDefinicao.setText(getDefinicao());
        JScrollPane scrollPane = new JScrollPane(textoDefinicao);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(Tema.FIELD_WIDTH_CODE, Tema.FIELD_HEIGHT_INFO));
        add(scrollPane, BorderLayout.SOUTH);

        botaoEnqueue.addActionListener(e -> enfileirar());
        botaoDequeue.addActionListener(e -> desenfileirar());

    }

    private String getDefinicao() {
        return "FILA (FIFO - First-In, First-Out)\n\n"
                + "\u2713 Estrutura de dados que organiza elementos seguindo o princípio do 'Primeiro a entrar, Primeiro a sair'\n"
                + "\u2713 Inserção (ENQUEUE) ocorre no FIM (REAR) e remoção (DEQUEUE) ocorre no INÍCIO (FRONT).\n"
                + "\u2713 Exemplos práticos: filas de banco, impressão de documentos, atendimento em serviços.";
    }

    // --- LÓGICA DA FILA ---
    private void enfileirar() {
        String valor = campoValor.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um valor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Node novoNo = new Node(valor);
        if (rear == null) {
            front = novoNo;
            rear = novoNo;
        } else {
            rear.next = novoNo;
            rear = novoNo;
        }

        campoValor.setText("");
        painelDesenho.repaint();
    }

    private void desenfileirar() {
        if (front == null) {
            JOptionPane.showMessageDialog(this, "A fila está vazia!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dadoRemovido = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }

        painelDesenho.repaint();

        JOptionPane.showMessageDialog(this, "Elemento removido (DEQUEUE): " + dadoRemovido, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- VISUALIZAÇÃO DA FILA ---
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

            if (front == null) {
                g2d.setFont(Tema.FONT_SUBHEADING);
                g2d.setColor(Tema.TEXT_SECONDARY);
                g2d.drawString("Fila Vazia.", getWidth() / 2 - 40, getHeight() / 2);
                return;
            }

            int nodeWidth = 90;
            int nodeHeight = 45;
            int gap = 20;
            int x = 30;
            int y = getHeight() / 2 - nodeHeight / 2;

            Node atual = front;
            while (atual != null) {
                // Corpo do Nó
                Color cor = (atual == front) ? Tema.ACCENT : Tema.PRIMARY_LIGHT;
                if (atual == rear && front != rear) {
                    cor = Tema.SECONDARY; // Destaque para Rear
                }
                g2d.setColor(cor);
                g2d.fillRoundRect(x, y, nodeWidth, nodeHeight, 10, 10);

                g2d.setColor(cor.darker());
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(x, y, nodeWidth, nodeHeight, 10, 10);

                // Texto
                g2d.setColor(Color.WHITE);
                g2d.setFont(Tema.FONT_BUTTON);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(atual.data, x + (nodeWidth - fm.stringWidth(atual.data)) / 2, y + 28);

                g2d.setFont(Tema.FONT_CODE);
                g2d.setColor(Tema.TEXT_PRIMARY);
                if (atual == front) {
                    g2d.drawString("Início (FRONT)", x - 4, y - 5);
                }
                if (atual == rear) {
                    g2d.drawString("Fim (REAR)", x + 12, y + nodeHeight + 15);
                }

                // Seta
                if (atual.next != null) {
                    g2d.setColor(Tema.TEXT_SECONDARY);
                    g2d.drawLine(x + nodeWidth, y + nodeHeight / 2, x + nodeWidth + gap, y + nodeHeight / 2);
                    g2d.fillPolygon(new int[]{x + nodeWidth + gap, x + nodeWidth + gap - 5, x + nodeWidth + gap - 5},
                            new int[]{y + nodeHeight / 2, y + nodeHeight / 2 - 3, y + nodeHeight / 2 + 3}, 3);
                }

                x += nodeWidth + gap;
                atual = atual.next;
            }
            setPreferredSize(new Dimension(x, 200));
            revalidate();
        }
    }
}
