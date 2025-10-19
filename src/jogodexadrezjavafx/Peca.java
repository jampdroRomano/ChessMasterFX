package jogodexadrezjavafx;

import java.util.List;

public abstract class Peca {
    private final Cor cor;
    private boolean jaMoveu = false; // Rastreia se a peça já fez seu primeiro movimento

    public Peca(Cor cor) {
        this.cor = cor;
    }

    public Cor getCor() {
        return cor;
    }

    // Método para saber se é o primeiro movimento da peça (essencial para Roque e Peão)
    public boolean isPrimeiroMovimento() {
        return !jaMoveu;
    }

    // Marca que a peça já se moveu
    public void registrarMovimento() {
        this.jaMoveu = true;
    }
    
    // Reseta o estado da peça para uma nova partida
    public void reset() {
        this.jaMoveu = false;
    }

    public abstract List<Casa> getMovimentosPossiveis(Casa casaAtual, Tabuleiro tabuleiro);
}

