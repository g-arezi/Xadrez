public abstract class Peca {
    protected Cor cor;
    protected int linha;
    protected int coluna;

    public Peca(Cor cor, int linha, int coluna) {
        this.cor = cor;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Cor getCor() {
        return cor;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setPosicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public abstract boolean movimentoValido(int destinoLinha, int destinoColuna, Tabuleiro tabuleiro);
    public abstract String toString();
}

