public class Peao extends Peca {
    public Peao(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    // Adiciona suporte ao movimento en passant
    private boolean enPassantValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro) {
        // Verifica se o Tabuleiro permite en passant para esta casa
        return tabuleiro.enPassantDisponivel(destinoLinha, destinoColuna, cor);
    }

    @Override
    public boolean movimentoValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro) {
        int direcao = (cor == Cor.BRANCO) ? -1 : 1;
        // Movimento simples para frente
        if (coluna == destinoColuna && tabuleiro.getPeca(destinoLinha, destinoColuna) == null) {
            if (destinoLinha - linha == direcao) return true;
            // Primeiro movimento pode avançar duas casas
            if ((cor == Cor.BRANCO && linha == 6 || cor == Cor.PRETO && linha == 1) && destinoLinha - linha == 2 * direcao) {
                return tabuleiro.getPeca(linha + direcao, coluna) == null;
            }
        }
        // Captura diagonal
        if (Math.abs(destinoColuna - coluna) == 1 && destinoLinha - linha == direcao) {
            Peca alvo = tabuleiro.getPeca(destinoLinha, destinoColuna);
            if (alvo != null && alvo.getCor() != cor) return true;
            // En passant
            if (enPassantValido(destinoLinha, destinoColuna, tabuleiro)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return cor == Cor.BRANCO ? "♙" : "♟";
    }
}
