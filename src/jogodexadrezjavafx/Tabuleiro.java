package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {

    private final Casa[][] casas;
    
    private int contadorChecksBrancas = 0;
    private int contadorChecksPretas = 0;

    public Tabuleiro() {
        casas = new Casa[8][8];
        inicializarTabuleiro();
    }

    private void inicializarTabuleiro() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                casas[i][j] = new Casa(i, j);
            }
        }

        // Peças Pretas
        casas[0][0].setPeca(new Torre(Cor.PRETA));
        casas[0][1].setPeca(new Cavalo(Cor.PRETA));
        casas[0][2].setPeca(new Bispo(Cor.PRETA));
        casas[0][3].setPeca(new Rainha(Cor.PRETA));
        casas[0][4].setPeca(new Rei(Cor.PRETA));
        casas[0][5].setPeca(new Bispo(Cor.PRETA));
        casas[0][6].setPeca(new Cavalo(Cor.PRETA));
        casas[0][7].setPeca(new Torre(Cor.PRETA));
        for (int j = 0; j < 8; j++) {
            casas[1][j].setPeca(new Peao(Cor.PRETA));
        }

        // Peças Brancas
        casas[7][0].setPeca(new Torre(Cor.BRANCA));
        casas[7][1].setPeca(new Cavalo(Cor.BRANCA));
        casas[7][2].setPeca(new Bispo(Cor.BRANCA));
        casas[7][3].setPeca(new Rainha(Cor.BRANCA));
        casas[7][4].setPeca(new Rei(Cor.BRANCA));
        casas[7][5].setPeca(new Bispo(Cor.BRANCA));
        casas[7][6].setPeca(new Cavalo(Cor.BRANCA));
        casas[7][7].setPeca(new Torre(Cor.BRANCA));
        for (int j = 0; j < 8; j++) {
            casas[6][j].setPeca(new Peao(Cor.BRANCA));
        }
    }

    public Casa getCasa(int linha, int coluna) {
        if (linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8) {
            return casas[linha][coluna];
        }
        return null;
    }
    
    public void moverPeca(Casa origem, Casa destino) {
        Peca pecaMovida = origem.getPeca();
        destino.setPeca(pecaMovida);
        origem.removerPeca();
        pecaMovida.registrarMovimento();
    }
    
    public Casa moverTorreRoque(Casa destinoRei) {
        int linha = destinoRei.getLinha();
        if (destinoRei.getColuna() == 6) { // Roque pequeno
            Casa casaTorreOrigem = getCasa(linha, 7);
            Casa casaTorreDestino = getCasa(linha, 5);
            moverPeca(casaTorreOrigem, casaTorreDestino);
            return casaTorreOrigem;
        }
        if (destinoRei.getColuna() == 2) { // Roque grande
            Casa casaTorreOrigem = getCasa(linha, 0);
            Casa casaTorreDestino = getCasa(linha, 3);
            moverPeca(casaTorreOrigem, casaTorreDestino);
            return casaTorreOrigem;
        }
        return null;
    }
    
    public void verificarChequeAposMovimento(Cor corDoOponente) {
        Casa casaDoReiOponente = getCasaDoRei(corDoOponente);
        if (casaDoReiOponente != null && isCasaAtacadaPor(casaDoReiOponente, (corDoOponente == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA)) {
            if (corDoOponente == Cor.BRANCA) {
                contadorChecksBrancas++;
                System.out.println("Rei BRANCO está em xeque! (" + contadorChecksBrancas + " de 3)");
            } else {
                contadorChecksPretas++;
                System.out.println("Rei PRETO está em xeque! (" + contadorChecksPretas + " de 3)");
            }
        }
    }
    
    public Casa getCasaDoRei(Cor corRei) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Casa casa = casas[i][j];
                if (casa.temPeca() && casa.getPeca() instanceof Rei && casa.getPeca().getCor() == corRei) {
                    return casa;
                }
            }
        }
        return null;
    }
    
    public boolean isCasaAtacadaPor(Casa casaAlvo, Cor corAtacante) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Casa casaAtual = casas[i][j];
                if (casaAtual.temPeca() && casaAtual.getPeca().getCor() == corAtacante) {
                    Peca pecaAtacante = casaAtual.getPeca();
                    
                    // Tratamento especial para o Peão, que ataca de forma diferente de como se move
                    if (pecaAtacante instanceof Peao) {
                        int direcao = (pecaAtacante.getCor() == Cor.BRANCA) ? -1 : 1;
                        if(casaAlvo.getLinha() == casaAtual.getLinha() + direcao && Math.abs(casaAlvo.getColuna() - casaAtual.getColuna()) == 1){
                            return true;
                        }
                    // Trata o Rei de forma especial para evitar o loop
                    } else if (pecaAtacante instanceof Rei) {
                        if (((Rei) pecaAtacante).getMovimentosBasicos(casaAtual, this).contains(casaAlvo)) {
                            return true;
                        }
                    } else {
                        // Para as outras peças, a lógica de movimento já inclui a de ataque
                        if (pecaAtacante.getMovimentosPossiveis(casaAtual, this).contains(casaAlvo)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public int getContadorChecksBrancas() {
        return contadorChecksBrancas;
    }

    public int getContadorChecksPretas() {
        return contadorChecksPretas;
    }
}

