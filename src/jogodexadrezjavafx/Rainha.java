package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Rainha extends Peca {

    public Rainha(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = origem.getLinha();
        int coluna = origem.getColuna();

        // Vetores de direção: 4 retas (Torre) + 4 diagonais (Bispo)
        int[][] direcoes = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Movimentos da Torre
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Movimentos do Bispo
        };

        for (int[] d : direcoes) {
            int l = linha + d[0];
            int c = coluna + d[1];

            // Continua na direção enquanto estiver dentro do tabuleiro
            while (l >= 0 && l < 8 && c >= 0 && c < 8) {
                Casa destino = tabuleiro.getCasa(l, c);
                if (!destino.temPeca()) {
                    movimentos.add(destino); // Casa vazia, pode mover
                } else {
                    // Se a peça no destino for de cor diferente, é um movimento de captura
                    if (destino.getPeca().getCor() != this.getCor()) {
                        movimentos.add(destino);
                    }
                    // Para a busca nesta direção, pois encontrou uma peça
                    break;
                }
                l += d[0];
                c += d[1];
            }
        }
        return movimentos;
    }
}

