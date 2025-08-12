public class Rei extends Peca {
    public Rei(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    public boolean movimentoValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro) {
        int dl = Math.abs(destinoLinha - linha);
        int dc = Math.abs(destinoColuna - coluna);
        return (dl <= 1 && dc <= 1);
    }

    @Override
    public String toString() {
        return cor == Cor.BRANCO ? "♔" : "♚";
    }
}

