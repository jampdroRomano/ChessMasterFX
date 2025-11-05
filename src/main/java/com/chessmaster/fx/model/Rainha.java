package com.chessmaster.fx.model;

import java.util.ArrayList;
import java.util.List;

public class Rainha extends Peca {

    public Rainha(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = getMovimentosPossiveisSemFiltro(origem, tabuleiro); 
        return tabuleiro.filtrarMovimentosLegais(movimentos, origem, this); 
    }

    @Override
    public List<Casa> getMovimentosPossiveisSemFiltro(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = origem.getLinha();
        int coluna = origem.getColuna();

        int[][] direcoes = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, 
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  
        };

        for (int[] d : direcoes) {
            int l = linha + d[0];
            int c = coluna + d[1];

            while (l >= 0 && l < 8 && c >= 0 && c < 8) {
                Casa destino = tabuleiro.getCasa(l, c);
                if (!destino.temPeca()) {
                    movimentos.add(destino); 
                } else {
                    if (destino.getPeca().getCor() != this.getCor()) {
                        movimentos.add(destino);
                    }
                    break;
                }
                l += d[0];
                c += d[1];
            }
        }
        return movimentos; 
    }
}