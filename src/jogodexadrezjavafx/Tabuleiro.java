package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Zera os contadores no início também
        contadorChecksBrancas = 0;
        contadorChecksPretas = 0;


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
         for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                 if(casas[i][j].temPeca()){
                     casas[i][j].getPeca().reset();
                 }
            }
         }
    }


    public Casa getCasa(int linha, int coluna) {
        if (linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8) {
            return casas[linha][coluna];
        }
        return null;
    }

    public boolean moverPeca(Casa origem, Casa destino) {
        Peca pecaMovida = origem.getPeca();
        if (pecaMovida == null) return false;

        boolean isCastling = false;
        Casa casaTorreOrigem = null;
        Casa casaTorreDestino = null;
        Peca torre = null;

        if (pecaMovida instanceof Rei && Math.abs(destino.getColuna() - origem.getColuna()) == 2) {
            isCastling = true;
            int linha = origem.getLinha();
            int colunaTorreOrigemIdx = (destino.getColuna() == 6) ? 7 : 0;
            int colunaTorreDestinoIdx = (destino.getColuna() == 6) ? 5 : 3;

            casaTorreOrigem = getCasa(linha, colunaTorreOrigemIdx);
            casaTorreDestino = getCasa(linha, colunaTorreDestinoIdx);

            if (casaTorreOrigem != null && casaTorreOrigem.temPeca() && casaTorreOrigem.getPeca() instanceof Torre && casaTorreDestino != null) {
                torre = casaTorreOrigem.getPeca();
                if (!torre.isPrimeiroMovimento()) {
                    System.err.println("Erro lógico Roque: Torre já moveu.");
                    isCastling = false; torre = null;
                }
            } else {
                System.err.println("Erro lógico Roque: Torre não encontrada ou casa destino inválida.");
                isCastling = false; torre = null;
            }
        }

        if (pecaMovida.getCor() == Cor.BRANCA) this.primeiroMovimentoBrancasFeito = true;
        else this.primeiroMovimentoPretasFeito = true;

        origem.removerPeca();
        destino.setPeca(pecaMovida);
        pecaMovida.registrarMovimento();
        System.out.println("Lógica: " + pecaMovida.getClass().getSimpleName() + " movido de " + origem.getLinha()+","+origem.getColuna() + " para " + destino.getLinha()+","+destino.getColuna());


        if (isCastling && torre != null && casaTorreOrigem != null && casaTorreDestino != null) {
            casaTorreOrigem.removerPeca(); 
            casaTorreDestino.setPeca(torre); 
            torre.registrarMovimento();
            System.out.println("Lógica Roque: Torre movida de " + casaTorreOrigem.getLinha()+","+casaTorreOrigem.getColuna() + " para " + casaTorreDestino.getLinha()+","+casaTorreDestino.getColuna());
        }

        boolean promocao = false;
        if (pecaMovida instanceof Peao) { 
            if (pecaMovida.getCor() == Cor.BRANCA && destino.getLinha() == 0) {
                destino.setPeca(new Rainha(Cor.BRANCA)); 
                System.out.println("Peão Branco promovido a Rainha!");
                promocao = true;
            }
            if (pecaMovida.getCor() == Cor.PRETA && destino.getLinha() == 7) {
                destino.setPeca(new Rainha(Cor.PRETA)); 
                System.out.println("Peão Preto promovido a Rainha!");
                promocao = true;
            }
        }
        return promocao;
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
             if (casaTorreOrigem != null && casaTorreOrigem.temPeca() && casaTorreOrigem.getPeca() instanceof Torre && casaTorreOrigem.getPeca().isPrimeiroMovimento() && casaTorreDestino != null) {
                Peca torre = casaTorreOrigem.getPeca();
                casaTorreDestino.setPeca(torre);
                casaTorreOrigem.removerPeca();
                torre.registrarMovimento();
                return casaTorreOrigem;
             }
        }
        return null;
    }

    public void verificarChequeAposMovimento(Cor corDoReiOponente) {
        Casa casaDoReiOponente = getCasaDoRei(corDoReiOponente);
        Cor corAtacante = (corDoReiOponente == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;

        if (casaDoReiOponente != null && isCasaAtacadaPor(casaDoReiOponente, corAtacante)) {
            if (corDoReiOponente == Cor.BRANCA) {
                contadorChecksBrancas++;
                System.out.println("Rei BRANCO em cheque! (Cheque #" + contadorChecksBrancas + ")");
            } else {
                contadorChecksPretas++;
                System.out.println("Rei PRETO em cheque! (Cheque #" + contadorChecksPretas + ")");
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
        System.err.println("!!! REI DA COR " + corRei + " NÃO ENCONTRADO !!!");
        return null;
    }

    public boolean isCasaAtacadaPor(Casa casaAlvo, Cor corAtacante) {
        if (casaAlvo == null) return false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Casa casaAtual = casas[i][j];
                if (casaAtual.temPeca() && casaAtual.getPeca().getCor() == corAtacante) {
                    Peca pecaAtacante = casaAtual.getPeca();
                    List<Casa> movimentosAtaque;

                    if (pecaAtacante instanceof Peao) {
                        movimentosAtaque = new ArrayList<>();
                        int linhaAtual = casaAtual.getLinha();
                        int colunaAtual = casaAtual.getColuna();
                        int direcao = (pecaAtacante.getCor() == Cor.BRANCA) ? -1 : 1;
                        Casa diagEsq = getCasa(linhaAtual + direcao, colunaAtual - 1);
                        if (diagEsq != null) movimentosAtaque.add(diagEsq);
                        Casa diagDir = getCasa(linhaAtual + direcao, colunaAtual + 1);
                        if (diagDir != null) movimentosAtaque.add(diagDir);
                    } else if (pecaAtacante instanceof Rei) {
                         movimentosAtaque = ((Rei) pecaAtacante).getMovimentosBasicos(casaAtual, this);
                    } else {
                        // Usa a versão SEM filtro para verificar ataques potenciais
                        movimentosAtaque = pecaAtacante.getMovimentosPossiveisSemFiltro(casaAtual, this);
                    }

                    if (movimentosAtaque != null && movimentosAtaque.contains(casaAlvo)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isReiEmCheque(Cor corRei) {
        Casa casaDoRei = getCasaDoRei(corRei);
        if (casaDoRei == null) {
             return false;
        }
        Cor corOponente = (corRei == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        return isCasaAtacadaPor(casaDoRei, corOponente);
    }


    public List<Casa> filtrarMovimentosLegais(List<Casa> movimentosBrutos, Casa origem, Peca pecaMovida) {
        if (pecaMovida == null) return new ArrayList<>();

        Cor corJogador = pecaMovida.getCor();
        List<Casa> movimentosLegais = new ArrayList<>();

        for (Casa destino : movimentosBrutos) {
            Peca pecaCapturada = destino.getPeca();
            destino.setPeca(pecaMovida);
            origem.removerPeca();

            boolean reiFicariaEmCheque = isReiEmCheque(corJogador);


            origem.setPeca(pecaMovida);
            destino.setPeca(pecaCapturada);

            if (!reiFicariaEmCheque) {
                movimentosLegais.add(destino);
            }
        }
        return movimentosLegais;
    }

    public boolean isXequeMate(Cor corRei) {

        if (!isReiEmCheque(corRei)) {
            return false; 
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Casa casaAtual = casas[i][j];
               
                if (casaAtual.temPeca() && casaAtual.getPeca().getCor() == corRei) {
                    Peca peca = casaAtual.getPeca();                   
                    List<Casa> movimentosLegais = peca.getMovimentosPossiveis(casaAtual, this);

                    if (!movimentosLegais.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        System.out.println("DEBUG: Xeque-mate detectado para " + corRei);
        return true;
    }

    public int getContadorChecksBrancas() {
        return contadorChecksBrancas;
    }

    public int getContadorChecksPretas() {
        return contadorChecksPretas;
    }
}