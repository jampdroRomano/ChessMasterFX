package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Rei extends Peca {

    public Rei(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa origem, Tabuleiro tabuleiro) {
        // Pega os movimentos básicos...
        List<Casa> movimentos = getMovimentosBasicos(origem, tabuleiro);
        // ... e então tenta adicionar o roque.
        adicionarMovimentoRoque(movimentos, origem, tabuleiro);
        return movimentos;
    }

    /**
     * Este método calcula apenas os movimentos adjacentes do Rei.
     * É usado para quebrar o loop infinito na verificação de ataques.
     */
    public List<Casa> getMovimentosBasicos(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = origem.getLinha();
        int coluna = origem.getColuna();

        // Movimentos em todas as 8 direções, uma casa
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                Casa destino = tabuleiro.getCasa(linha + i, coluna + j);
                if (destino != null && (!destino.temPeca() || destino.getPeca().getCor() != this.getCor())) {
                    movimentos.add(destino);
                }
            }
        }
        return movimentos;
    }
    
    private void adicionarMovimentoRoque(List<Casa> movimentos, Casa origem, Tabuleiro tabuleiro) {
        if (!isPrimeiroMovimento() || tabuleiro.isCasaAtacadaPor(origem, (getCor() == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA)) {
            return;
        }

        int linha = origem.getLinha();
        Cor corOponente = (getCor() == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;

        // 1. Roque Pequeno (lado do Rei)
        Casa casaTorrePequeno = tabuleiro.getCasa(linha, 7);
        if (casaTorrePequeno != null && casaTorrePequeno.temPeca() && casaTorrePequeno.getPeca().isPrimeiroMovimento()) {
            Casa casaF = tabuleiro.getCasa(linha, 5);
            Casa casaG = tabuleiro.getCasa(linha, 6);
            if (casaF != null && !casaF.temPeca() && casaG != null && !casaG.temPeca()) {
                if (!tabuleiro.isCasaAtacadaPor(casaF, corOponente) && !tabuleiro.isCasaAtacadaPor(casaG, corOponente)) {
                    movimentos.add(casaG);
                }
            }
        }

        // 2. Roque Grande (lado da Rainha)
        Casa casaTorreGrande = tabuleiro.getCasa(linha, 0);
        if (casaTorreGrande != null && casaTorreGrande.temPeca() && casaTorreGrande.getPeca().isPrimeiroMovimento()) {
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

