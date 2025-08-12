public class Cavalo extends Peca {
    public Cavalo(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    public boolean movimentoValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro) {
        int dl = Math.abs(destinoLinha - linha);
        int dc = Math.abs(destinoColuna - coluna);
        return (dl == 2 && dc == 1) || (dl == 1 && dc == 2);
    }

    @Override
    public String toString() {
        return cor == Cor.BRANCO ? "♘" : "♞";
    }
}

