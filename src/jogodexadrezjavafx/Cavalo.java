package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Cavalo extends Peca {
    public Cavalo(Cor cor) {
        super(cor);
    }

    @Override
    public List<Casa> getMovimentosPossiveis(Casa origem, Tabuleiro tabuleiro) {
        List<Casa> movimentos = new ArrayList<>();
        int linha = origem.getLinha();
        int coluna = origem.getColuna();

        // Todos os 8 movimentos possíveis em "L" do cavalo
        int[][] movimentosEmL = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] m : movimentosEmL) {
            int l = linha + m[0];
            int c = coluna + m[1];

            // Verifica se o movimento está dentro do tabuleiro
            if (l >= 0 && l < 8 && c >= 0 && c < 8) {
                Casa destino = tabuleiro.getCasa(l, c);
                // Pode mover se a casa estiver vazia ou tiver uma peça de cor diferente (captura)
                if (!destino.temPeca() || destino.getPeca().getCor() != this.getCor()) {
                    movimentos.add(destino);
                }
            }
        }
        return movimentos;
    }
}

