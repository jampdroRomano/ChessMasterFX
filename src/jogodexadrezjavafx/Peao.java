package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Peao extends Peca {

    public Peao(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa casaAtual, Tabuleiro tabuleiro) {
        List<Casa> movimentos = getMovimentosPossiveisSemFiltro(casaAtual, tabuleiro);
        return tabuleiro.filtrarMovimentosLegais(movimentos, casaAtual, this);
    }

    @Override
    public List<Casa> getMovimentosPossiveisSemFiltro(Casa casaAtual, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = casaAtual.getLinha();
        int coluna = casaAtual.getColuna();

        if (getCor() == Cor.BRANCA) {
           
            if (linha > 0) {
                Casa casaFrente = tabuleiro.getCasa(linha - 1, coluna);
                if (casaFrente != null && !casaFrente.temPeca()) { 
                    movimentos.add(casaFrente);

                    if (!tabuleiro.isPrimeiroMovimentoBrancasFeito() && linha == 6) { 
                        Casa casaDupla = tabuleiro.getCasa(linha - 2, coluna);
                        if (casaDupla != null && !casaDupla.temPeca()) {
                            movimentos.add(casaDupla);
                        }
                    }
                }
            }

          
            if (linha > 0 && coluna > 0) { 
                Casa casaCaptura = tabuleiro.getCasa(linha - 1, coluna - 1);
                if (casaCaptura != null && casaCaptura.temPeca() && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
            if (linha > 0 && coluna < 7) {
                Casa casaCaptura = tabuleiro.getCasa(linha - 1, coluna + 1);
                if (casaCaptura != null && casaCaptura.temPeca() && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }

        } else { 
             if (linha < 7) {
                Casa casaFrente = tabuleiro.getCasa(linha + 1, coluna);

                if (casaFrente != null && !casaFrente.temPeca()) { 
                    movimentos.add(casaFrente);

                    if (!tabuleiro.isPrimeiroMovimentoPretasFeito() && linha == 1) { 
                        Casa casaDupla = tabuleiro.getCasa(linha + 2, coluna);
                        if (casaDupla != null && !casaDupla.temPeca()) {
                            movimentos.add(casaDupla);
                        }
                    }
                }
            }

           
            if (linha < 7 && coluna > 0) { 
                Casa casaCaptura = tabuleiro.getCasa(linha + 1, coluna - 1);
                if (casaCaptura != null && casaCaptura.temPeca() && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
            if (linha < 7 && coluna < 7) { 
                Casa casaCaptura = tabuleiro.getCasa(linha + 1, coluna + 1);
                if (casaCaptura != null && casaCaptura.temPeca() && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
        }

        return movimentos; 
    }
}