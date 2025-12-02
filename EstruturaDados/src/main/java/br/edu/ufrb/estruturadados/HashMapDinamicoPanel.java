package br.edu.ufrb.estruturadados;

import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class HashMapDinamicoPanel extends JPanel {

    private static final double CARGA_MAXIMA = 0.70;

    private Integer[] tabelaHash;
    private int capacidade;
    private int tamanhoAtual = 0;

    private PainelDesenhoHashMapDinamico painelDesenho;
    private JTextField campoValor, campoQtdAleatorio;
    private JButton botaoInserir, botaoBuscar, botaoRemover, botaoLimpar, botaoGerar;
    private JLabel labelInfo;

    private final Color COR_SUCESSO = new Color(144, 238, 144);
    private final Color COR_COLISAO = new Color(255, 100, 100);
    private final Color COR_BUSCA = new Color(173, 216, 230);

    public HashMapDinamicoPanel() {
        this.capacidade = 15;
        this.tabelaHash = new Integer[capacidade];

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Tema.BACKGROUND);

        JPanel painelControles = ComponentesUI.criarPainelModerno();
        painelControles.setLayout(new GridBagLayout());
        painelControles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Tema.BORDER),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        campoValor = ComponentesUI.criarCampoTextoModerno(6);
        campoQtdAleatorio = ComponentesUI.criarCampoTextoModerno(5);
        botaoInserir = ComponentesUI.criarBotaoModerno("Inserir", Tema.SUCCESS);
        botaoLimpar = ComponentesUI.criarBotaoModerno("Limpar tabela", Tema.WARNING);
        botaoBuscar = ComponentesUI.criarBotaoModerno("Buscar", Tema.SECONDARY);
        botaoRemover = ComponentesUI.criarBotaoModerno("Remover", Tema.ERROR);
        botaoGerar = ComponentesUI.criarBotaoModerno("Gerar aleatórios", Tema.PRIMARY);

        gbc.gridy = 0;
        gbc.gridx = 0;

        painelControles.add(ComponentesUI.criarLabelEstilizado("Valor:", "subheading"), gbc);
        
        gbc.gridx = 1;
        painelControles.add(campoValor, gbc);
        gbc.gridx = 2;
        painelControles.add(botaoInserir, gbc);
        gbc.gridx = 3;
        painelControles.add(botaoBuscar, gbc);
        gbc.gridx = 4;
        painelControles.add(botaoRemover, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        painelControles.add(ComponentesUI.criarLabelEstilizado("Qtd. aleatória:", "subheading"), gbc);
        gbc.gridx = 1;
        painelControles.add(campoQtdAleatorio, gbc);
        gbc.gridx = 2;
        painelControles.add(botaoGerar, gbc);
        gbc.gridx = 3;
        painelControles.add(botaoLimpar, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 5;
        labelInfo = new JLabel("Info: Insira um valor para calcular o índice.");
        painelControles.add(labelInfo, gbc);

        add(painelControles, BorderLayout.NORTH);

        painelDesenho = new PainelDesenhoHashMapDinamico();
        add(new JScrollPane(painelDesenho), BorderLayout.CENTER);

        JTextArea textoDefinicao = ComponentesUI.criarAreaTextoEstilizada();
        textoDefinicao.setText(getDefinicao());
        JScrollPane scrollPane = new JScrollPane(textoDefinicao);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(Tema.FIELD_WIDTH_CODE, Tema.FIELD_HEIGHT_INFO));
        add(scrollPane, BorderLayout.SOUTH);        

        botaoInserir.addActionListener(e -> executarAcao("inserir"));
        botaoBuscar.addActionListener(e -> executarAcao("buscar"));
        botaoRemover.addActionListener(e -> executarAcao("remover"));
        botaoGerar.addActionListener(e -> gerarAleatorios());
        botaoLimpar.addActionListener(e -> limpar());

        atualizarVisualizacao();
    }

    private void limpar() {
        capacidade = 15;
        tabelaHash = new Integer[capacidade];
        tamanhoAtual = 0;
        labelInfo.setText("Info: Tabela foi limpa.");
        atualizarVisualizacao();
    }

    private void executarAcao(String acao) {
        try {
            int valor = Integer.parseInt(campoValor.getText().trim());

            bloquearControles(true);
            SwingWorker<Void, String> worker = new SwingWorker<>() {

                @Override
                protected Void doInBackground() throws Exception {

                    publish("Calculando índice inicial...");
                    int index = valor % capacidade;
                    Thread.sleep(500);

                    publish(valor + " % " + capacidade + " = " + index);
                    painelDesenho.destacar(index, COR_BUSCA);
                    Thread.sleep(700);

                    switch (acao) {
                        case "inserir":
                            inserir(valor, index);
                            break;
                        case "buscar":
                            buscar(valor, index);
                            break;
                        case "remover":
                            remover(valor, index);
                            break;
                    }

                    Thread.sleep(800);
                    return null;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    labelInfo.setText("Info: " + chunks.get(chunks.size() - 1));
                }

                @Override
                protected void done() {
                    atualizarVisualizacao();
                    bloquearControles(false);
                    campoValor.setText("");
                }
            };

            worker.execute();

        } catch (NumberFormatException ex) {
            erro("Digite um número inteiro válido.");
        }
    }


    private void inserir(int valor, int indexInicial) throws Exception {

        int index = indexInicial;

        for (int tentativa = 0; tentativa < capacidade; tentativa++) {

            if (tabelaHash[index] == null) {
                tabelaHash[index] = valor;
                tamanhoAtual++;

                painelDesenho.destacar(index, COR_SUCESSO);
                aviso("Valor " + valor + " inserido no índice " + index + ".");
                break;
            } else {
                painelDesenho.destacar(index, COR_COLISAO);
                labelInfo.setText("Info: Colisão no índice " + index + ". Usando linear probing...");
                Thread.sleep(500);
            }

            index = (index + 1) % capacidade;
        }

        if ((double) tamanhoAtual / capacidade > CARGA_MAXIMA) {
            redimensionar();
        }
    }

    private void buscar(int valor, int indexInicial) throws Exception {

        int index = indexInicial;

        for (int tentativa = 0; tentativa < capacidade; tentativa++) {

            painelDesenho.destacar(index, COR_BUSCA);
            Thread.sleep(350);

            if (tabelaHash[index] == null) {
                aviso("Valor NÃO encontrado.");
                return;
            }

            if (tabelaHash[index] == valor) {
                painelDesenho.destacar(index, COR_SUCESSO);
                aviso("Valor encontrado no índice " + index + ".");
                return;
            }

            index = (index + 1) % capacidade;
        }

        aviso("Valor NÃO encontrado após varrer toda a tabela.");
    }

    private void remover(int valor, int indexInicial) throws Exception {
        int index = indexInicial;

        for (int tentativa = 0; tentativa < capacidade; tentativa++) {

            painelDesenho.destacar(index, COR_BUSCA);
            Thread.sleep(350);

            if (tabelaHash[index] == null) {
                aviso("Valor não encontrado.");
                return;
            }

            if (tabelaHash[index] == valor) {
                tabelaHash[index] = null;
                tamanhoAtual--;
                painelDesenho.destacar(index, COR_SUCESSO);
                aviso("Valor removido do índice " + index + ".");
                return;
            }

            index = (index + 1) % capacidade;
        }

        aviso("Valor não encontrado após varrer toda a tabela.");
    }

    private void redimensionar() throws Exception {
        int novaCapacidade = capacidade * 2;
        Integer[] novaTabela = new Integer[novaCapacidade];

        aviso("Redimensionando tabela para " + novaCapacidade + " posições...");
        Thread.sleep(800);

        for (int i = 0; i < capacidade; i++) {
            if (tabelaHash[i] != null) {

                int novoIndex = tabelaHash[i] % novaCapacidade;

                while (novaTabela[novoIndex] != null) {
                    novoIndex = (novoIndex + 1) % novaCapacidade;
                }

                novaTabela[novoIndex] = tabelaHash[i];
            }
        }

        capacidade = novaCapacidade;
        tabelaHash = novaTabela;
        painelDesenho.atualizar(tabelaHash, capacidade);
        labelInfo.setText("Info: Redimensionamento concluído!");
    }


    private void inserirSilencioso(int valor) {
        int index = valor % capacidade;

        for (int tentativa = 0; tentativa < capacidade; tentativa++) {
            if (tabelaHash[index] == null) {
                tabelaHash[index] = valor;
                tamanhoAtual++;
                return;
            }
            index = (index + 1) % capacidade;
        }
    }

    private void redimensionarSemAviso() {
        int novaCapacidade = capacidade * 2;
        Integer[] novaTabela = new Integer[novaCapacidade];

        for (int i = 0; i < capacidade; i++) {
            if (tabelaHash[i] != null) {
                int novoIndex = tabelaHash[i] % novaCapacidade;
                while (novaTabela[novoIndex] != null) {
                    novoIndex = (novoIndex + 1) % novaCapacidade;
                }
                novaTabela[novoIndex] = tabelaHash[i];
            }
        }

        capacidade = novaCapacidade;
        tabelaHash = novaTabela;
        painelDesenho.atualizar(tabelaHash, capacidade);
    }


    private void gerarAleatorios() {
        try {
            int qtd = Integer.parseInt(campoQtdAleatorio.getText().trim());
            if (qtd <= 0) {
                erro("A quantidade deve ser maior que zero.");
                return;
            }

            Random rand = new Random();
            int inseridos = 0;
            int tentados = 0;
            int limiteTentativas = Math.max(100, qtd * 5);

            while (inseridos < qtd && tamanhoAtual < capacidade && tentados < limiteTentativas) {
                int valorAleatorio = rand.nextInt(1000) + 1;
                int index = valorAleatorio % capacidade;
                if (tabelaHash[index] == null) {
                    inserirSilencioso(valorAleatorio);
                    inseridos++;
                } else {

                    int idx = index;
                    boolean colocado = false;
                    for (int p = 0; p < capacidade; p++) {
                        if (tabelaHash[idx] == null) {
                            tabelaHash[idx] = valorAleatorio;
                            tamanhoAtual++;
                            colocado = true;
                            break;
                        }
                        idx = (idx + 1) % capacidade;
                    }
                    if (colocado) inseridos++;
                }

                if ((double) tamanhoAtual / capacidade > CARGA_MAXIMA) {
                    redimensionarSemAviso();
                }

                tentados++;
            }

            if (inseridos == 0) {
                labelInfo.setText("Info: Não foi possível inserir novos valores (colisões ou tabela cheia).");
            } else if (inseridos < qtd) {
                labelInfo.setText("Info: Inseridos " + inseridos + " de " + qtd + " valores (outros colidiram).");
            } else {
                labelInfo.setText("Info: " + inseridos + " valores aleatórios inseridos com sucesso!");
            }

            atualizarVisualizacao();

        } catch (NumberFormatException ex) {
            erro("A quantidade deve ser um número inteiro válido.");
        }
    }

    private void atualizarVisualizacao() {
        painelDesenho.atualizar(tabelaHash, capacidade);
    }

    private void bloquearControles(boolean bloquear) {
        botaoInserir.setEnabled(!bloquear);
        botaoBuscar.setEnabled(!bloquear);
        botaoRemover.setEnabled(!bloquear);
        botaoLimpar.setEnabled(!bloquear);
        botaoGerar.setEnabled(!bloquear);
        campoValor.setEnabled(!bloquear);
        campoQtdAleatorio.setEnabled(!bloquear);
    }

    private void erro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getDefinicao() {
        return """
        Hash Map Dinâmico com Linear Probing:

        Nesta versão, utilizamos colisões resolvidas com Linear Probing e redimensionamento automático.
        Quando a tabela atinge 70% da capacidade, ela é dobrada e todos os elementos são re-hashados.

        Vantagens:
        - Cresce conforme necessário (não perde inserções)
        - Inserções, buscas e remoções permanecem rápidas
        - Visualização clara de colisões e probing

        Desvantagens:
        - Linear probing pode gerar “clusters”
        - Rehashing durante o redimensionamento pode ser custoso

        Complexidade:
        - Inserção média: O(1)
        - Remoção média: O(1)
        - Redimensionamento: O(n)""";
    }


    class PainelDesenhoHashMapDinamico extends JPanel {

        private Integer[] tabelaHash;
        private int capacidade;
        private int indexDestaque = -1;
        private Color corDestaque = Color.GRAY;

        public PainelDesenhoHashMapDinamico() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Tema.BORDER));
        }

        @Override
        public Dimension getPreferredSize() {
            if (tabelaHash == null || capacidade == 0) {
                return new Dimension(800, 400);
            }
            int w = 100, h = 60, gap = 20, cols = 5;
            int rows = (int) Math.ceil(capacidade / (double) cols);
            int totalHeight = rows * (h + gap) + 40;
            int totalWidth = cols * (w + gap);
            return new Dimension(totalWidth, totalHeight);
        }

        public void atualizar(Integer[] tabelaHash, int capacidade) {
            this.tabelaHash = tabelaHash;
            this.capacidade = capacidade;
            this.indexDestaque = -1;

            int w = 100, h = 60, gap = 20, cols = 5;
            int rows = (int) Math.ceil(capacidade / (double) cols);
            int totalHeight = rows * (h + gap) + 40;
            setPreferredSize(new Dimension(getWidth() > 0 ? getWidth() : cols * (w + gap), totalHeight));

            repaint();
            revalidate();
        }

        public void destacar(int index, Color cor) {
            this.indexDestaque = index;
            this.corDestaque = cor;
            repaint();
        }

        public void destacarTemporario(int index, Color cor, int msDestaque) {
            this.indexDestaque = index;
            this.corDestaque = cor;
            repaint();

            // remove destaque depois do tempo
            new Timer(msDestaque, e -> {
                indexDestaque = -1;
                repaint();
            }).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (tabelaHash == null) return;

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = 100, h = 60, gap = 20, cols = 5;
            int rows = (int) Math.ceil(capacidade / (double) cols);
            int totalHeight = rows * (h + gap);
            int totalWidth = cols * (w + gap);
            int startY = (getHeight() - totalHeight) / 2;
            if (startY < 20) startY = 20;
            int startX = (getWidth() - totalWidth) / 2;
            if (startX < 20) startX = 20;

            for (int i = 0; i < capacidade; i++) {
                int r = i / cols;
                int c = i % cols;
                int x = startX + c * (w + gap);
                int y = startY + r * (h + gap);

                boolean ocupado = tabelaHash[i] != null;

                Color corFundo;
                int espessuraBorda;
                Color corBorda;

                if (i == indexDestaque) {
                    corFundo = corDestaque;
                    espessuraBorda = 4;
                    corBorda = corDestaque.darker();
                } else {
                    corFundo = ocupado ? Tema.PRIMARY_LIGHT : Color.WHITE;
                    espessuraBorda = 1;
                    corBorda = Tema.BORDER.darker();
                }

                // Desenha o fundo do bucket
                g2d.setColor(corFundo);
                g2d.fillRoundRect(x, y, w, h, 10, 10);

                // Desenha a borda
                g2d.setColor(corBorda);
                g2d.setStroke(new BasicStroke(espessuraBorda));
                g2d.drawRoundRect(x, y, w, h, 10, 10);

                // Desenha o índice [i]
                g2d.setColor(Tema.TEXT_SECONDARY);
                g2d.setFont(Tema.FONT_CODE);
                g2d.drawString("[" + i + "]", x + 5, y + 20);

                // Desenha o valor armazenado (se houver)
                if (ocupado) {
                    g2d.setColor(Tema.TEXT_PRIMARY);
                    g2d.setFont(Tema.FONT_HEADING);
                    String v = String.valueOf(tabelaHash[i]);
                    
                    // Centraliza melhor o texto
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(v);
                    int textX = x + (w - textWidth) / 2;
                    int textY = y + h / 2 + fm.getAscent() / 2;

                    g2d.drawString(v, textX, textY);
                }
            }

            g2d.dispose();
        }
    }
}
