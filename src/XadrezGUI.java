import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class XadrezGUI extends JFrame {
    private Tabuleiro tabuleiro;
    private JButton[][] botoes;
    private int origemLinha = -1, origemColuna = -1;
    private JLabel statusLabel;
    private JLabel contagemLabel;
    private JLabel capturadasLabel;
    private JLabel relogioLabel;
    private JTextArea historicoArea;
    private Timer timer;
    private int segundosBranco = 0, segundosPreto = 0;
    private int tempoPersonalizado = 0; // segundos por jogador, 0 = sem limite

    public XadrezGUI() {
        tabuleiro = new Tabuleiro();
        botoes = new JButton[Tabuleiro.TAMANHO][Tabuleiro.TAMANHO];
        setTitle("Xadrez");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel painelTabuleiro = new JPanel(new GridLayout(Tabuleiro.TAMANHO, Tabuleiro.TAMANHO));
        inicializarBotoes(painelTabuleiro);
        add(painelTabuleiro, BorderLayout.CENTER);
        statusLabel = new JLabel("Turno: " + tabuleiro.getTurno());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(statusLabel, BorderLayout.SOUTH);
        JPanel painelInfo = new JPanel(new BorderLayout());
        contagemLabel = new JLabel();
        contagemLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        painelInfo.add(contagemLabel, BorderLayout.NORTH);
        historicoArea = new JTextArea(10, 20);
        historicoArea.setEditable(false);
        painelInfo.add(new JScrollPane(historicoArea), BorderLayout.CENTER);
        JButton exportarBtn = new JButton("Exportar Histórico");
        exportarBtn.addActionListener(e -> mostrarExportacao());
        painelInfo.add(exportarBtn, BorderLayout.SOUTH);
        capturadasLabel = new JLabel();
        capturadasLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        painelInfo.add(capturadasLabel, BorderLayout.WEST);
        relogioLabel = new JLabel();
        relogioLabel.setFont(new Font("Arial", Font.BOLD, 16));
        painelInfo.add(relogioLabel, BorderLayout.EAST);
        JButton tempoBtn = new JButton("Definir Tempo");
        tempoBtn.addActionListener(e -> mostrarDialogoTempo());
        painelInfo.add(tempoBtn, BorderLayout.NORTH);
        add(painelInfo, BorderLayout.EAST);
        atualizarTabuleiro();
        atualizarContagemEHistorico();
        iniciarRelogio();
        setVisible(true);
    }

    private void iniciarRelogio() {
        timer = new Timer(1000, e -> {
            if (tabuleiro.getTurno() == Cor.BRANCO) segundosBranco++;
            else segundosPreto++;
            atualizarRelogio();
            if (tempoPersonalizado > 0) {
                if (segundosBranco >= tempoPersonalizado) {
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Tempo esgotado para as brancas! Fim de jogo.", "Tempo", JOptionPane.INFORMATION_MESSAGE);
                }
                if (segundosPreto >= tempoPersonalizado) {
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Tempo esgotado para as pretas! Fim de jogo.", "Tempo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        timer.start();
    }

    private void atualizarRelogio() {
        relogioLabel.setText("Branco: " + formatarTempo(segundosBranco) + " | Preto: " + formatarTempo(segundosPreto));
    }

    private String formatarTempo(int s) {
        int min = s / 60;
        int seg = s % 60;
        return String.format("%02d:%02d", min, seg);
    }

    private void salvarHistoricoEmArquivo() {
        JFileChooser chooser = new JFileChooser();
        int op = chooser.showSaveDialog(this);
        if (op == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(), tabuleiro.exportarPartida().getBytes());
                JOptionPane.showMessageDialog(this, "Histórico salvo em arquivo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarExportacao() {
        String texto = tabuleiro.exportarPartida();
        JOptionPane.showMessageDialog(this, texto, "Histórico da Partida", JOptionPane.INFORMATION_MESSAGE);
        int opt = JOptionPane.showConfirmDialog(this, "Deseja salvar o histórico em arquivo?", "Salvar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            salvarHistoricoEmArquivo();
        }
    }

    private void mostrarDialogoTempo() {
        String min = JOptionPane.showInputDialog(this, "Tempo por jogador (minutos, 0 = sem limite):", "10");
        try {
            int minutos = Integer.parseInt(min);
            setTempoPersonalizado(minutos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Adiciona método para definir tempo personalizado
    public void setTempoPersonalizado(int minutos) {
        tempoPersonalizado = minutos * 60;
        segundosBranco = 0;
        segundosPreto = 0;
        atualizarRelogio();
    }

    private void atualizarCapturadas() {
        java.util.List<String> capturadasBrancas = tabuleiro.getPecasCapturadasPorCor(Cor.BRANCO);
        java.util.List<String> capturadasPretas = tabuleiro.getPecasCapturadasPorCor(Cor.PRETO);
        StringBuilder sb = new StringBuilder("Capturadas - Brancas: ");
        for (String p : capturadasBrancas) sb.append(p).append(" ");
        sb.append("| Pretas: ");
        for (String p : capturadasPretas) sb.append(p).append(" ");
        capturadasLabel.setText(sb.toString());
    }

    private void atualizarContagemEHistorico() {
        StringBuilder sb = new StringBuilder();
        sb.append("Brancas: ").append(tabuleiro.contarPecas(Cor.BRANCO)).append("\n");
        sb.append("Pretas: ").append(tabuleiro.contarPecas(Cor.PRETO)).append("\n");
        contagemLabel.setText("<html>" + sb.toString().replace("\n", "<br>") + "</html>");
        StringBuilder hist = new StringBuilder();
        for (String jogada : tabuleiro.getHistoricoJogadas()) {
            hist.append(jogada).append("\n");
        }
        historicoArea.setText(hist.toString());
        atualizarCapturadas();
    }

    private void inicializarBotoes(JPanel painelTabuleiro) {
        for (int linha = 0; linha < Tabuleiro.TAMANHO; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.TAMANHO; coluna++) {
                JButton botao = new JButton();
                botao.setFont(new Font("Arial", Font.BOLD, 24));
                botao.setBackground((linha + coluna) % 2 == 0 ? Color.WHITE : Color.GRAY);
                final int l = linha, c = coluna;
                botao.addActionListener(e -> cliqueCasa(l, c));
                botoes[linha][coluna] = botao;
                painelTabuleiro.add(botao);
            }
        }
    }

    private void cliqueCasa(int linha, int coluna) {
        if (origemLinha == -1 && tabuleiro.getPeca(linha, coluna) != null && tabuleiro.getPeca(linha, coluna).getCor() == tabuleiro.getTurno()) {
            origemLinha = linha;
            origemColuna = coluna;
            botoes[linha][coluna].setBackground(Color.YELLOW);
        } else if (origemLinha != -1) {
            if (tabuleiro.movimentoValido(origemLinha, origemColuna, linha, coluna)) {
                tabuleiro.moverPecaComRegras(origemLinha, origemColuna, linha, coluna);
                if (tabuleiro.estaEmXeque(tabuleiro.getTurno())) {
                    statusLabel.setText("Xeque! Turno: " + tabuleiro.getTurno());
                } else if (tabuleiro.estaEmXequeMate(tabuleiro.getTurno())) {
                    statusLabel.setText("Xeque-mate! Fim de jogo.");
                } else if (tabuleiro.estaAfogamento(tabuleiro.getTurno())) {
                    statusLabel.setText("Empate por afogamento! Fim de jogo.");
                } else {
                    statusLabel.setText("Turno: " + tabuleiro.getTurno());
                }
            } else {
                statusLabel.setText("Movimento inválido! Turno: " + tabuleiro.getTurno());
            }
            botoes[origemLinha][origemColuna].setBackground((origemLinha + origemColuna) % 2 == 0 ? Color.WHITE : Color.GRAY);
            origemLinha = -1;
            origemColuna = -1;
            atualizarTabuleiro();
        }
        atualizarContagemEHistorico();
    }

    private void atualizarTabuleiro() {
        for (int linha = 0; linha < Tabuleiro.TAMANHO; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.TAMANHO; coluna++) {
                Peca peca = tabuleiro.getPeca(linha, coluna);
                if (peca == null) {
                    botoes[linha][coluna].setText("");
                } else {
                    // Usa emojis Unicode para as peças
                    String emoji;
                    if (peca instanceof Rei) emoji = peca.getCor() == Cor.BRANCO ? "&#9812;" : "&#9818;";
                    else if (peca instanceof Rainha) emoji = peca.getCor() == Cor.BRANCO ? "&#9813;" : "&#9819;";
                    else if (peca instanceof Torre) emoji = peca.getCor() == Cor.BRANCO ? "&#9814;" : "&#9820;";
                    else if (peca instanceof Bispo) emoji = peca.getCor() == Cor.BRANCO ? "&#9815;" : "&#9821;";
                    else if (peca instanceof Cavalo) emoji = peca.getCor() == Cor.BRANCO ? "&#9816;" : "&#9822;";
                    else if (peca instanceof Peao) emoji = peca.getCor() == Cor.BRANCO ? "&#9817;" : "&#9823;";
                    else emoji = peca.toString();
                    botoes[linha][coluna].setText(emoji);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(XadrezGUI::new);
    }
}
