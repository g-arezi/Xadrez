public class Rainha extends Peca {
    public Rainha(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    public boolean movimentoValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro) {
        int dl = Math.abs(destinoLinha - linha);
        int dc = Math.abs(destinoColuna - coluna);
        return (dl == dc || linha == destinoLinha || coluna == destinoColuna);
    }

    @Override
    public String toString() {
        return cor == Cor.BRANCO ? "♕" : "♛";
    }
}

