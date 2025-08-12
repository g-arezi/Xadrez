public class Torre extends Peca {
    public Torre(Cor cor, int linha, int coluna) {
        super(cor, linha, coluna);
    }

    @Override
    public boolean movimentoValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro) {
        return (linha == destinoLinha || coluna == destinoColuna);
    }

    @Override
    public String toString() {
        return cor == Cor.BRANCO ? "♖" : "♜";
    }
}

