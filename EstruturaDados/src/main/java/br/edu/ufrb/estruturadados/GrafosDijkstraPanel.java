/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ufrb.estruturadados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 *
 * @author 2401210
 */
public class GrafosDijkstraPanel extends JPanel {

    // Estruturas de Dados do Grafo
    private List<Node> nos;
    private Map<Node, Map<Node, Integer>> adjacencia; // Map<Origem, Map<Destino, Peso>>

    // Controle de Interação
    private Node noSelecionado;
    private Node noInicio;
    private Node noFim;

    // Controle Visual
    private List<Node> caminhoDestacado; // Para pintar o resultado do algoritmo
    private VisualizacaoPanel painelDesenho;
    private JTextArea textoLog;

    public GrafosDijkstraPanel() {
        nos = new ArrayList<>();
        adjacencia = new HashMap<>();
        caminhoDestacado = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Painel Superior (Controles) ---
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnLimpar = new JButton("Limpar Grafo");
        JButton btnBFS = new JButton("Executar Busca (BFS)");
        JButton btnDijkstra = new JButton("Executar Dijkstra");
        JLabel lblInstrucao = new JLabel(
                "<html><small>Click esquerdo: Criar Nó / Conectar Nós<br>Click direito: Definir Início (Verde) / Fim (Vermelho)</small></html>");

        painelControles.add(btnLimpar);
        painelControles.add(new JSeparator(SwingConstants.VERTICAL));
        painelControles.add(btnBFS);
        painelControles.add(btnDijkstra);
        painelControles.add(new JSeparator(SwingConstants.VERTICAL));
        painelControles.add(lblInstrucao);

        add(painelControles, BorderLayout.NORTH);

        // --- Painel Central (Desenho) ---
        painelDesenho = new VisualizacaoPanel();
        add(painelDesenho, BorderLayout.CENTER);

        // --- Painel Inferior (Explicação/Log) ---
        textoLog = new JTextArea();
        textoLog.setEditable(false);
        textoLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textoLog.setText(getDefinicao());
        JScrollPane scrollLog = new JScrollPane(textoLog);
        scrollLog.setPreferredSize(new Dimension(100, 120));
        add(scrollLog, BorderLayout.SOUTH);

        // --- Listeners ---
        btnLimpar.addActionListener(e -> limparGrafo());
        btnBFS.addActionListener(e -> executarBFS());
        btnDijkstra.addActionListener(e -> executarDijkstra());
    }

    private String getDefinicao() {
        return "GRAFOS (Dijkstra e BFS):\n\n" +
                "Uma estrutura de dados não-linear composta por vértices (nós) e arestas (conexões). " +
                "Muito utilizada para modelar redes, rotas de GPS e relacionamentos complexos.\n\n" +
                "Algoritmos de Busca:\n" +
                "- BFS (O(V + E)): Explora camada por camada. Ideal para grafos sem peso.\n" +
                "- Dijkstra (O(E log V)): Usa uma fila de prioridade para encontrar o caminho de menor custo acumulado.\n\n"
                +
                "Prós: O Dijkstra garante matematicamente o caminho mais curto global em grafos com pesos positivos.\n"
                +
                "Contras: O Dijkstra não suporta arestas com pesos negativos e é computacionalmente mais lento que a BFS se o grafo não tiver pesos.";
    }

    private void limparGrafo() {
        nos.clear();
        adjacencia.clear();
        noSelecionado = null;
        noInicio = null;
        noFim = null;
        caminhoDestacado.clear();
        textoLog.setText(getDefinicao());
        painelDesenho.repaint();
    }

    // --- Lógica dos Algoritmos ---

    private void executarBFS() {
        if (noInicio == null || noFim == null) {
            JOptionPane.showMessageDialog(this, "Defina um nó de Início e Fim com o botão direito.");
            return;
        }

        caminhoDestacado.clear();
        Queue<Node> fila = new LinkedList<>();
        Set<Node> visitados = new HashSet<>();
        Map<Node, Node> anterior = new HashMap<>();

        fila.add(noInicio);
        visitados.add(noInicio);

        boolean encontrou = false;

        while (!fila.isEmpty()) {
            Node atual = fila.poll();
            if (atual.equals(noFim)) {
                encontrou = true;
                break;
            }

            if (adjacencia.containsKey(atual)) {
                for (Node vizinho : adjacencia.get(atual).keySet()) {
                    if (!visitados.contains(vizinho)) {
                        visitados.add(vizinho);
                        anterior.put(vizinho, atual);
                        fila.add(vizinho);
                    }
                }
            }
        }

        if (encontrou) {
            reconstruirCaminho(anterior);
            textoLog.setText("BFS: Caminho encontrado!\nPassos: " + (caminhoDestacado.size() - 1));
        } else {
            textoLog.setText("BFS: Destino inalcançável a partir do início.");
            painelDesenho.repaint();
        }
    }

    private void executarDijkstra() {
        if (noInicio == null || noFim == null) {
            JOptionPane.showMessageDialog(this, "Defina um nó de Início e Fim com o botão direito.");
            return;
        }

        caminhoDestacado.clear();

        // Distâncias iniciais
        Map<Node, Integer> distancias = new HashMap<>();
        Map<Node, Node> anterior = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(distancias::get));

        for (Node n : nos) {
            distancias.put(n, Integer.MAX_VALUE);
        }
        distancias.put(noInicio, 0);
        pq.add(noInicio);

        while (!pq.isEmpty()) {
            Node atual = pq.poll();

            if (atual.equals(noFim))
                break; // Chegamos (otimização)

            // Se distância atual é infinito, ignora
            if (distancias.get(atual) == Integer.MAX_VALUE)
                continue;

            if (adjacencia.containsKey(atual)) {
                for (Map.Entry<Node, Integer> aresta : adjacencia.get(atual).entrySet()) {
                    Node vizinho = aresta.getKey();
                    int peso = aresta.getValue();
                    int novaDist = distancias.get(atual) + peso;

                    if (novaDist < distancias.get(vizinho)) {
                        // Remova da fila antes de atualizar o valor no mapa
                        pq.remove(vizinho);
                        // atualização dos valores
                        distancias.put(vizinho, novaDist);
                        anterior.put(vizinho, atual);
                        // Adiciona novo valor de prioridade
                        pq.add(vizinho);
                    }
                }
            }
        }

        if (distancias.get(noFim) != Integer.MAX_VALUE) {
            reconstruirCaminho(anterior);
            textoLog.setText("Dijkstra: Caminho Mínimo encontrado!\nCusto Total: " + distancias.get(noFim));
        } else {
            textoLog.setText("Dijkstra: Destino inalcançável.");
            painelDesenho.repaint();
        }
    }

    private void reconstruirCaminho(Map<Node, Node> anterior) {
        Node atual = noFim;
        while (atual != null) {
            caminhoDestacado.add(0, atual); // Adiciona no início
            atual = anterior.get(atual);
        }
        painelDesenho.repaint();
    }

    // --- Classes Internas e Visualização ---

    private class Node {
        String id;
        int x, y;

        public Node(String id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        // Usado para mapas e comparações
        @Override
        public String toString() {
            return id;
        }
    }

    private class VisualizacaoPanel extends JPanel {
        public VisualizacaoPanel() {
            setBackground(Color.WHITE);
            MouseHandler handler = new MouseHandler();
            addMouseListener(handler);
            addMouseMotionListener(handler);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Desenhar Arestas
            for (Node origem : adjacencia.keySet()) {
                for (Map.Entry<Node, Integer> entry : adjacencia.get(origem).entrySet()) {
                    Node destino = entry.getKey();
                    int peso = entry.getValue();

                    // Verifica se esta aresta faz parte do caminho destacado
                    boolean inPath = isArestaNoCaminho(origem, destino);

                    g2d.setColor(inPath ? Color.ORANGE : Color.GRAY);
                    g2d.setStroke(new BasicStroke(inPath ? 3 : 1));
                    g2d.drawLine(origem.x, origem.y, destino.x, destino.y);

                    // Desenha Peso (no meio da linha)
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(peso), (origem.x + destino.x) / 2, (origem.y + destino.y) / 2);
                }
            }

            // Desenhar Nós
            int r = 15; // Raio
            for (Node n : nos) {
                if (n.equals(noInicio))
                    g2d.setColor(new Color(144, 238, 144)); // Verde claro
                else if (n.equals(noFim))
                    g2d.setColor(new Color(255, 160, 122)); // Vermelho claro
                else if (caminhoDestacado.contains(n))
                    g2d.setColor(Color.YELLOW);
                else
                    g2d.setColor(Color.CYAN);

                g2d.fillOval(n.x - r, n.y - r, 2 * r, 2 * r);

                // Borda
                if (n.equals(noSelecionado)) {
                    g2d.setColor(Color.BLUE);
                    g2d.setStroke(new BasicStroke(3));
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawOval(n.x - r, n.y - r, 2 * r, 2 * r);

                // Texto ID
                g2d.setColor(Color.BLACK);
                g2d.drawString(n.id, n.x - 5, n.y + 5);
            }
        }

        private boolean isArestaNoCaminho(Node a, Node b) {
            if (caminhoDestacado.size() < 2)
                return false;
            for (int i = 0; i < caminhoDestacado.size() - 1; i++) {
                Node n1 = caminhoDestacado.get(i);
                Node n2 = caminhoDestacado.get(i + 1);
                // Verifica a aresta (direcionada ou não)
                if ((n1 == a && n2 == b))
                    return true;
            }
            return false;
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Node clicado = getNoEm(e.getX(), e.getY());

            if (SwingUtilities.isRightMouseButton(e)) {
                // Botão Direito: Menu de Contexto
                if (clicado != null) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem itemInicio = new JMenuItem("Definir como Início");
                    JMenuItem itemFim = new JMenuItem("Definir como Fim");

                    itemInicio.addActionListener(ev -> {
                        noInicio = clicado;
                        painelDesenho.repaint();
                    });
                    itemFim.addActionListener(ev -> {
                        noFim = clicado;
                        painelDesenho.repaint();
                    });

                    menu.add(itemInicio);
                    menu.add(itemFim);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
                return;
            }

            // Botão Esquerdo
            if (clicado == null) {
                // Criar novo nó
                String id = String.valueOf(nos.size());
                Node novo = new Node(id, e.getX(), e.getY());
                nos.add(novo);
                painelDesenho.repaint();
            } else {
                // Lógica de seleção e criação de aresta
                if (noSelecionado == null) {
                    noSelecionado = clicado;
                } else {
                    if (noSelecionado != clicado) {
                        // Adicionar aresta
                        String pesoStr = JOptionPane.showInputDialog("Peso da aresta:", "1");
                        try {
                            int peso = Integer.parseInt(pesoStr);
                            // Adiciona (Grafo Direcionado para simplificar Dijkstra)
                            adjacencia.computeIfAbsent(noSelecionado, k -> new HashMap<>()).put(clicado, peso);
                            adjacencia.computeIfAbsent(clicado, k -> new HashMap<>()).put(noSelecionado, peso);
                        } catch (NumberFormatException ex) {
                            // Cancelou ou inválido
                        }
                        noSelecionado = null;
                    } else {
                        noSelecionado = null; // Deseleciona
                    }
                }
                painelDesenho.repaint();
            }
        }
    }

    private Node getNoEm(int x, int y) {
        int raio = 20;
        for (Node n : nos) {
            if (Math.abs(n.x - x) < raio && Math.abs(n.y - y) < raio) {
                return n;
            }
        }
        return null;
    }
}