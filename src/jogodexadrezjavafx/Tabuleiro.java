package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {

    private final Casa[][] casas;
    
    private boolean primeiroMovimentoBrancasFeito;
    private boolean primeiroMovimentoPretasFeito;
    
    private int contadorChecksBrancas = 0;
    private int contadorChecksPretas = 0;

    public Tabuleiro() {
        casas = new Casa[8][8];
        inicializarTabuleiro();
    }

    public void reset() {
        contadorChecksBrancas = 0;
        contadorChecksPretas = 0;
        primeiroMovimentoBrancasFeito = false;
        primeiroMovimentoPretasFeito = false;
        inicializarTabuleiro();
    }
    
    private void inicializarTabuleiro() {
        primeiroMovimentoBrancasFeito = false;
        primeiroMovimentoPretasFeito = false;
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (casas[i][j] == null) {
                    casas[i][j] = new Casa(i, j);
                } else {
                    casas[i][j].removerPeca();
                }
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
    
    /**
     * Move uma peça e verifica se houve promoção.
     * @return Verdadeiro se um peão foi promovido, falso caso contrário.
     */
    public boolean moverPeca(Casa origem, Casa destino) {
        Peca pecaMovida = origem.getPeca();
        
        if (pecaMovida.getCor() == Cor.BRANCA) {
            this.primeiroMovimentoBrancasFeito = true;
        } else {
            this.primeiroMovimentoPretasFeito = true;
        }
        
        destino.setPeca(pecaMovida);
        origem.removerPeca();
        pecaMovida.registrarMovimento();

        // --- LÓGICA DE PROMOÇÃO ---
        if (pecaMovida instanceof Peao) {
            // Se um peão branco chegar na linha 0
            if (pecaMovida.getCor() == Cor.BRANCA && destino.getLinha() == 0) {
                destino.setPeca(new Rainha(Cor.BRANCA));
                System.out.println("Peão Branco promovido a Rainha!");
                return true;
            }
            // Se um peão preto chegar na linha 7
            if (pecaMovida.getCor() == Cor.PRETA && destino.getLinha() == 7) {
                destino.setPeca(new Rainha(Cor.PRETA));
                System.out.println("Peão Preto promovido a Rainha!");
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isPrimeiroMovimentoBrancasFeito() {
        return primeiroMovimentoBrancasFeito;
    }

    public boolean isPrimeiroMovimentoPretasFeito() {
        return primeiroMovimentoPretasFeito;
    }
    
    public Casa moverTorreRoque(Casa destinoRei) {
        int linha = destinoRei.getLinha();
        if (destinoRei.getColuna() == 6 || destinoRei.getColuna() == 2) {
             int colunaTorreOrigem = (destinoRei.getColuna() == 6) ? 7 : 0;
             int colunaTorreDestino = (destinoRei.getColuna() == 6) ? 5 : 3;

             Casa casaTorreOrigem = getCasa(linha, colunaTorreOrigem);
             Casa casaTorreDestino = getCasa(linha, colunaTorreDestino);
             if (casaTorreOrigem != null && casaTorreDestino != null) {
                // A chamada moverPeca aqui não precisa verificar promoção
                Peca torre = casaTorreOrigem.getPeca();
                casaTorreDestino.setPeca(torre);
                casaTorreOrigem.removerPeca();
                torre.registrarMovimento();
                return casaTorreOrigem;
             }
        }
        return null;
    }
    
    public void verificarChequeAposMovimento(Cor corDoOponente) {
        Casa casaDoReiOponente = getCasaDoRei(corDoOponente);
        if (casaDoReiOponente != null && isCasaAtacadaPor(casaDoReiOponente, (corDoOponente == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA)) {
            if (corDoOponente == Cor.BRANCA) {
                contadorChecksBrancas++;
            } else {
                contadorChecksPretas++;
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
                    
                    if (pecaAtacante instanceof Peao) {
                        int direcao = (pecaAtacante.getCor() == Cor.BRANCA) ? -1 : 1;
                        if(casaAlvo.getLinha() == casaAtual.getLinha() + direcao && Math.abs(casaAlvo.getColuna() - casaAtual.getColuna()) == 1){
                            return true;
                        }
                    } else if (pecaAtacante instanceof Rei) {
                        if (((Rei) pecaAtacante).getMovimentosBasicos(casaAtual, this).contains(casaAlvo)) {
                            return true;
                        }
                    } else {
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

