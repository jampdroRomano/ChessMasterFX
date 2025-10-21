package jogodexadrezjavafx;

import java.util.List;

public abstract class Peca {
    private final Cor cor;
    private boolean jaMoveu = false; 

    public Peca(Cor cor) {
        this.cor = cor;
    }

    public Cor getCor() {
        return cor;
    }

    public boolean isPrimeiroMovimento() {
        return !jaMoveu;
    }

    public void registrarMovimento() {
        this.jaMoveu = true;
    }

    public void reset() {
        this.jaMoveu = false;
    }

    public void resetMoveu() {
        this.jaMoveu = false;
    }

    public abstract List<Casa> getMovimentosPossiveis(Casa casaAtual, Tabuleiro tabuleiro);

    public abstract List<Casa> getMovimentosPossiveisSemFiltro(Casa casaAtual, Tabuleiro tabuleiro);
}
