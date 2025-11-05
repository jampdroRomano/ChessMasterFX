package com.chessmaster.fx.model;

public class Movimento {
    private final Casa origem;
    private final Casa destino;
    private final Peca peca;
    private int pontuacao;

    public Movimento(Casa origem, Casa destino, Peca peca) {
        this.origem = origem;
        this.destino = destino;
        this.peca = peca;
        this.pontuacao = 0;
    }

    public Casa getOrigem() {
        return origem;
    }

    public Casa getDestino() {
        return destino;
    }
    
    public Peca getPeca() {
        return peca;
    }
    
    public int getPontuacao() {
        return pontuacao;
    }
    
    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    @Override
    public String toString() {
        String pStr = (peca != null) ? peca.getClass().getSimpleName() : "PecaNula";
        return pStr + " de (" + origem.getLinha() + "," + origem.getColuna() +
               ") para (" + destino.getLinha() + "," + destino.getColuna() + ")";
    }
}
