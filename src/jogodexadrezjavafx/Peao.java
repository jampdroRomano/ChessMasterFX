package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Peao extends Peca {

    public Peao(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa casaAtual, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = casaAtual.getLinha();
        int coluna = casaAtual.getColuna();

        if (getCor() == Cor.BRANCA) {
            // --- Movimento para frente ---
            if (linha > 0) {
                Casa casaFrente = tabuleiro.getCasa(linha - 1, coluna);
                if (casaFrente.getPeca() == null) {
                    movimentos.add(casaFrente);
                    
                    // --- Movimento de duas casas (se estiver na linha inicial) ---
                    if (linha == 6) {
                        Casa casaDupla = tabuleiro.getCasa(linha - 2, coluna);
                        if (casaDupla.getPeca() == null) {
                            movimentos.add(casaDupla);
                        }
                    }
                }
            }
            
            // --- Captura diagonal ---
            if (linha > 0 && coluna > 0) { // Diagonal esquerda
                Casa casaCaptura = tabuleiro.getCasa(linha - 1, coluna - 1);
                if (casaCaptura.getPeca() != null && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
            if (linha > 0 && coluna < 7) { // Diagonal direita
                Casa casaCaptura = tabuleiro.getCasa(linha - 1, coluna + 1);
                if (casaCaptura.getPeca() != null && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }

        } else { // PEÇAS PRETAS
            // --- Movimento para frente ---
             if (linha < 7) {
                Casa casaFrente = tabuleiro.getCasa(linha + 1, coluna);
                if (casaFrente.getPeca() == null) {
                    movimentos.add(casaFrente);

                    // --- Movimento de duas casas (se estiver na linha inicial) ---
                    if (linha == 1) {
                        Casa casaDupla = tabuleiro.getCasa(linha + 2, coluna);
                        if (casaDupla.getPeca() == null) {
                            movimentos.add(casaDupla);
                        }
                    }
                }
            }
            
            // --- Captura diagonal ---
            if (linha < 7 && coluna > 0) { // Diagonal esquerda
                Casa casaCaptura = tabuleiro.getCasa(linha + 1, coluna - 1);
                if (casaCaptura.getPeca() != null && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
            if (linha < 7 && coluna < 7) { // Diagonal direita
                Casa casaCaptura = tabuleiro.getCasa(linha + 1, coluna + 1);
                if (casaCaptura.getPeca() != null && casaCaptura.getPeca().getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
        }

        return movimentos;
    }
}

