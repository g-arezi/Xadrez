import java.util.*;

public class Tabuleiro {
    private Peca[][] casas;
    public static final int TAMANHO = 8;
    private Cor turno = Cor.BRANCO;

    private boolean reiBrancoMovido = false;
    private boolean reiPretoMovido = false;
    private boolean torreBrancaEsqMovida = false;
    private boolean torreBrancaDirMovida = false;
    private boolean torrePretaEsqMovida = false;
    private boolean torrePretaDirMovida = false;
    private int enPassantLinha = -1;
    private int enPassantColuna = -1;
    private List<String> historicoJogadas = new ArrayList<>();

    public Tabuleiro() {
        casas = new Peca[TAMANHO][TAMANHO];
        inicializar();
    }

    public void inicializar() {
        // Coloca as peças pretas
        casas[0][0] = new Torre(Cor.PRETO, 0, 0);
        casas[0][1] = new Cavalo(Cor.PRETO, 0, 1);
        casas[0][2] = new Bispo(Cor.PRETO, 0, 2);
        casas[0][3] = new Rainha(Cor.PRETO, 0, 3);
        casas[0][4] = new Rei(Cor.PRETO, 0, 4);
        casas[0][5] = new Bispo(Cor.PRETO, 0, 5);
        casas[0][6] = new Cavalo(Cor.PRETO, 0, 6);
        casas[0][7] = new Torre(Cor.PRETO, 0, 7);
        for (int i = 0; i < TAMANHO; i++) {
            casas[1][i] = new Peao(Cor.PRETO, 1, i);
        }
        // Coloca as peças brancas
        casas[7][0] = new Torre(Cor.BRANCO, 7, 0);
        casas[7][1] = new Cavalo(Cor.BRANCO, 7, 1);
        casas[7][2] = new Bispo(Cor.BRANCO, 7, 2);
        casas[7][3] = new Rainha(Cor.BRANCO, 7, 3);
        casas[7][4] = new Rei(Cor.BRANCO, 7, 4);
        casas[7][5] = new Bispo(Cor.BRANCO, 7, 5);
        casas[7][6] = new Cavalo(Cor.BRANCO, 7, 6);
        casas[7][7] = new Torre(Cor.BRANCO, 7, 7);
        for (int i = 0; i < TAMANHO; i++) {
            casas[6][i] = new Peao(Cor.BRANCO, 6, i);
        }
    }

    public Peca getPeca(int linha, int coluna) {
        return casas[linha][coluna];
    }

    public Cor getTurno() {
        return turno;
    }

    private boolean podeRoque(int origemLinha, int origemColuna, int destinoLinha, int destinoColuna) {
        // Roque branco
        if (getPeca(origemLinha, origemColuna) instanceof Rei && getPeca(origemLinha, origemColuna).getCor() == Cor.BRANCO && origemLinha == 7 && origemColuna == 4) {
            // Roque pequeno
            if (destinoLinha == 7 && destinoColuna == 6 && !reiBrancoMovido && !torreBrancaDirMovida && getPeca(7,5) == null && getPeca(7,6) == null) {
                if (!estaEmXeque(Cor.BRANCO) && !casaAtacada(7,5,Cor.PRETO) && !casaAtacada(7,6,Cor.PRETO)) {
                    return true;
                }
            }
            // Roque grande
            if (destinoLinha == 7 && destinoColuna == 2 && !reiBrancoMovido && !torreBrancaEsqMovida && getPeca(7,1) == null && getPeca(7,2) == null && getPeca(7,3) == null) {
                if (!estaEmXeque(Cor.BRANCO) && !casaAtacada(7,3,Cor.PRETO) && !casaAtacada(7,2,Cor.PRETO)) {
                    return true;
                }
            }
        }
        // Roque preto
        if (getPeca(origemLinha, origemColuna) instanceof Rei && getPeca(origemLinha, origemColuna).getCor() == Cor.PRETO && origemLinha == 0 && origemColuna == 4) {
            // Roque pequeno
            if (destinoLinha == 0 && destinoColuna == 6 && !reiPretoMovido && !torrePretaDirMovida && getPeca(0,5) == null && getPeca(0,6) == null) {
                if (!estaEmXeque(Cor.PRETO) && !casaAtacada(0,5,Cor.BRANCO) && !casaAtacada(0,6,Cor.BRANCO)) {
                    return true;
                }
            }
            // Roque grande
            if (destinoLinha == 0 && destinoColuna == 2 && !reiPretoMovido && !torrePretaEsqMovida && getPeca(0,1) == null && getPeca(0,2) == null && getPeca(0,3) == null) {
                if (!estaEmXeque(Cor.PRETO) && !casaAtacada(0,3,Cor.BRANCO) && !casaAtacada(0,2,Cor.BRANCO)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean casaAtacada(int linha, int coluna, Cor atacante) {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Peca p = casas[i][j];
                if (p != null && p.getCor() == atacante) {
                    if (p.movimentoValido(linha, coluna, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean movimentoValido(int origemLinha, int origemColuna, int destinoLinha, int destinoColuna) {
        Peca peca = getPeca(origemLinha, origemColuna);
        if (peca == null || peca.getCor() != turno) return false;
        if (peca instanceof Rei && podeRoque(origemLinha, origemColuna, destinoLinha, destinoColuna)) return true;
        if (!peca.movimentoValido(destinoLinha, destinoColuna, this)) return false;
        // Não pode capturar peça da mesma cor
        Peca destino = getPeca(destinoLinha, destinoColuna);
        if (destino != null && destino.getCor() == turno) return false;
        // Simula movimento para verificar xeque
        Peca[][] backup = copiarCasas();
        moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
        boolean emXeque = estaEmXeque(turno);
        casas = backup;
        return !emXeque;
    }

    private Peca[][] copiarCasas() {
        Peca[][] copia = new Peca[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                copia[i][j] = casas[i][j];
            }
        }
        return copia;
    }

    public boolean estaEmXeque(Cor cor) {
        int reiLinha = -1, reiColuna = -1;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Peca p = casas[i][j];
                if (p instanceof Rei && p.getCor() == cor) {
                    reiLinha = i;
                    reiColuna = j;
                }
            }
        }
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Peca p = casas[i][j];
                if (p != null && p.getCor() != cor) {
                    if (p.movimentoValido(reiLinha, reiColuna, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean estaEmXequeMate(Cor cor) {
        if (!estaEmXeque(cor)) return false;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Peca p = casas[i][j];
                if (p != null && p.getCor() == cor) {
                    for (int x = 0; x < TAMANHO; x++) {
                        for (int y = 0; y < TAMANHO; y++) {
                            if (movimentoValido(i, j, x, y)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    // Verifica empate por afogamento (stalemate)
    public boolean estaAfogamento(Cor cor) {
        if (estaEmXeque(cor)) return false;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Peca p = casas[i][j];
                if (p != null && p.getCor() == cor) {
                    for (int x = 0; x < TAMANHO; x++) {
                        for (int y = 0; y < TAMANHO; y++) {
                            if (movimentoValido(i, j, x, y)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean enPassantDisponivel(int destinoLinha, int destinoColuna, Cor cor) {
        if (enPassantLinha == destinoLinha && enPassantColuna == destinoColuna) {
            // Só pode capturar en passant na jogada seguinte
            return true;
        }
        return false;
    }

    public Map<String, Integer> contarPecas(Cor cor) {
        Map<String, Integer> contagem = new HashMap<>();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Peca p = casas[i][j];
                if (p != null && p.getCor() == cor) {
                    String tipo = p.getClass().getSimpleName();
                    contagem.put(tipo, contagem.getOrDefault(tipo, 0) + 1);
                }
            }
        }
        return contagem;
    }

    public List<String> getHistoricoJogadas() {
        return historicoJogadas;
    }

    public String exportarPartida() {
        StringBuilder sb = new StringBuilder();
        int num = 1;
        for (String jogada : historicoJogadas) {
            sb.append(num++).append(". ").append(jogada).append("\n");
        }
        return sb.toString();
    }

    private String posicaoToStr(int linha, int coluna) {
        return "" + (char)('a' + coluna) + (8 - linha);
    }

    // Adiciona método moverPeca usado internamente
    public void moverPeca(int origemLinha, int origemColuna, int destinoLinha, int destinoColuna) {
        Peca peca = casas[origemLinha][origemColuna];
        casas[destinoLinha][destinoColuna] = peca;
        casas[origemLinha][origemColuna] = null;
        if (peca != null) {
            peca.setPosicao(destinoLinha, destinoColuna);
        }
    }

    // Adiciona controle de peças capturadas
    private List<String> pecasCapturadas = new ArrayList<>();
    public List<String> getPecasCapturadas() {
        return pecasCapturadas;
    }
    public List<String> getPecasCapturadasPorCor(Cor cor) {
        List<String> lista = new ArrayList<>();
        for (String p : pecasCapturadas) {
            if (p.startsWith(cor == Cor.BRANCO ? "B:" : "P:")) lista.add(p.substring(2));
        }
        return lista;
    }

    // Adiciona método para registrar capturas
    private void registrarCaptura(Peca capturada) {
        if (capturada != null) {
            String corStr = capturada.getCor() == Cor.BRANCO ? "B:" : "P:";
            pecasCapturadas.add(corStr + capturada.getClass().getSimpleName());
        }
    }

    public void moverPecaComRegras(int origemLinha, int origemColuna, int destinoLinha, int destinoColuna) {
        if (!movimentoValido(origemLinha, origemColuna, destinoLinha, destinoColuna)) return;
        Peca p = getPeca(origemLinha, origemColuna);
        Peca capturada = getPeca(destinoLinha, destinoColuna);
        // Roque
        if (p instanceof Rei && podeRoque(origemLinha, origemColuna, destinoLinha, destinoColuna)) {
            if (p.getCor() == Cor.BRANCO) {
                reiBrancoMovido = true;
                if (destinoColuna == 6) { // Roque pequeno
                    moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
                    moverPeca(7,7,7,5);
                    torreBrancaDirMovida = true;
                } else { // Roque grande
                    moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
                    moverPeca(7,0,7,3);
                    torreBrancaEsqMovida = true;
                }
            } else {
                reiPretoMovido = true;
                if (destinoColuna == 6) { // Roque pequeno
                    moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
                    moverPeca(0,7,0,5);
                    torrePretaDirMovida = true;
                } else { // Roque grande
                    moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
                    moverPeca(0,0,0,3);
                    torrePretaEsqMovida = true;
                }
            }
        } else {
            // En passant
            if (p instanceof Peao && Math.abs(destinoColuna - origemColuna) == 1 && getPeca(destinoLinha, destinoColuna) == null) {
                if (enPassantDisponivel(destinoLinha, destinoColuna, p.getCor())) {
                    moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
                    // Remove o peão capturado
                    if (p.getCor() == Cor.BRANCO) {
                        registrarCaptura(getPeca(destinoLinha + 1, destinoColuna));
                        casas[destinoLinha + 1][destinoColuna] = null;
                    } else {
                        registrarCaptura(getPeca(destinoLinha - 1, destinoColuna));
                        casas[destinoLinha - 1][destinoColuna] = null;
                    }
                } else {
                    registrarCaptura(capturada);
                    moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
                }
            } else {
                registrarCaptura(capturada);
                moverPeca(origemLinha, origemColuna, destinoLinha, destinoColuna);
            }
            // Atualiza flags de movimento
            if (p instanceof Rei) {
                if (p.getCor() == Cor.BRANCO) reiBrancoMovido = true;
                else reiPretoMovido = true;
            }
            if (p instanceof Torre) {
                if (p.getCor() == Cor.BRANCO) {
                    if (origemLinha == 7 && origemColuna == 0) torreBrancaEsqMovida = true;
                    if (origemLinha == 7 && origemColuna == 7) torreBrancaDirMovida = true;
                } else {
                    if (origemLinha == 0 && origemColuna == 0) torrePretaEsqMovida = true;
                    if (origemLinha == 0 && origemColuna == 7) torrePretaDirMovida = true;
                }
            }
        }
        // Controle de en passant
        enPassantLinha = -1;
        enPassantColuna = -1;
        if (p instanceof Peao && Math.abs(destinoLinha - origemLinha) == 2) {
            enPassantLinha = (origemLinha + destinoLinha) / 2;
            enPassantColuna = origemColuna;
        }
        // Promoção de peão
        if (p instanceof Peao) {
            if ((p.getCor() == Cor.BRANCO && destinoLinha == 0) || (p.getCor() == Cor.PRETO && destinoLinha == 7)) {
                casas[destinoLinha][destinoColuna] = new Rainha(p.getCor(), destinoLinha, destinoColuna);
            }
        }
        // Adiciona ao histórico
        Peca pc = getPeca(destinoLinha, destinoColuna);
        String tipo = pc != null ? pc.getClass().getSimpleName() : "";
        historicoJogadas.add(tipo + ": " + posicaoToStr(origemLinha, origemColuna) + " -> " + posicaoToStr(destinoLinha, destinoColuna));
        turno = (turno == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }

    public void imprimir() {
        for (int linha = 0; linha < TAMANHO; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                Peca peca = casas[linha][coluna];
                if (peca == null) {
                    System.out.print("- ");
                } else {
                    System.out.print(peca + " ");
                }
            }
            System.out.println();
        }
    }
}
