package com.chessmaster.fx.model;

public class Casa {
    private final int linha;
    private final int coluna;
    private Peca peca;

    public Casa(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.peca = null;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public boolean temPeca() {
        return this.peca != null;
    }

    public void removerPeca() {
        this.peca = null;
    }
}

