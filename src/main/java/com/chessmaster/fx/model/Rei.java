package com.chessmaster.fx.model;

import java.util.ArrayList;
import java.util.List;

public class Rei extends Peca {

    public Rei(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentosBrutos = getMovimentosPossiveisSemFiltro(origem, tabuleiro);
        return tabuleiro.filtrarMovimentosLegais(movimentosBrutos, origem, this);
    }

    @Override
    public List<Casa> getMovimentosPossiveisSemFiltro(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = getMovimentosBasicos(origem, tabuleiro);
        adicionarMovimentoRoque(movimentos, origem, tabuleiro);
        return movimentos;
    }

    public List<Casa> getMovimentosBasicos(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = origem.getLinha();
        int coluna = origem.getColuna();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; 

                int l = linha + i;
                int c = coluna + j;

                 if (l >= 0 && l < 8 && c >= 0 && c < 8) {
                    Casa destino = tabuleiro.getCasa(l, c);
                    if (!destino.temPeca() || destino.getPeca().getCor() != this.getCor()) {
                        movimentos.add(destino);
                    }
                }
            }
        }
        return movimentos;
    }
    
    private void adicionarMovimentoRoque(List<Casa> movimentos, Casa origem, Tabuleiro tabuleiro) {
        if (!isPrimeiroMovimento() || tabuleiro.isReiEmCheque(getCor())) {
            return;
        }

        int linha = origem.getLinha();
        Cor corOponente = (getCor() == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;

        Casa casaTorrePequeno = tabuleiro.getCasa(linha, 7); 
        if (casaTorrePequeno != null && casaTorrePequeno.temPeca() && casaTorrePequeno.getPeca() instanceof Torre && casaTorrePequeno.getPeca().isPrimeiroMovimento()) {
            Casa casaF = tabuleiro.getCasa(linha, 5); 
            Casa casaG = tabuleiro.getCasa(linha, 6);
            if (casaF != null && !casaF.temPeca() && casaG != null && !casaG.temPeca()) {
                if (!tabuleiro.isCasaAtacadaPor(casaF, corOponente) && !tabuleiro.isCasaAtacadaPor(casaG, corOponente)) {
                    movimentos.add(casaG); 
                }
            }
        }

        Casa casaTorreGrande = tabuleiro.getCasa(linha, 0); 
        if (casaTorreGrande != null && casaTorreGrande.temPeca() && casaTorreGrande.getPeca() instanceof Torre && casaTorreGrande.getPeca().isPrimeiroMovimento()) {
            Casa casaB = tabuleiro.getCasa(linha, 1); 
            Casa casaC = tabuleiro.getCasa(linha, 2);
            Casa casaD = tabuleiro.getCasa(linha, 3); 
            if (casaB != null && !casaB.temPeca() && casaC != null && !casaC.temPeca() && casaD != null && !casaD.temPeca()) {
                if (!tabuleiro.isCasaAtacadaPor(casaC, corOponente) && !tabuleiro.isCasaAtacadaPor(casaD, corOponente)) {
                    movimentos.add(casaC); 
                }
            }
        }
    }
}