package com.chessmaster.fx.service;

import com.chessmaster.fx.model.Bispo;
import com.chessmaster.fx.model.Casa;
import com.chessmaster.fx.model.Cavalo;
import com.chessmaster.fx.model.Cor;
import com.chessmaster.fx.model.Movimento;
import com.chessmaster.fx.model.Peao;
import com.chessmaster.fx.model.Peca;
import com.chessmaster.fx.model.Rainha;
import com.chessmaster.fx.model.Rei;
import com.chessmaster.fx.model.Tabuleiro;
import com.chessmaster.fx.model.Torre;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot {

    private final Tabuleiro tabuleiro; 
    private final Cor corDoBot;
    private final Random random;
    private NivelDificuldade nivel;

    public enum NivelDificuldade {
        FACIL,
        MEDIO,
        DIFICIL
    }

    public Bot(Tabuleiro tabuleiro, Cor corDoBot, NivelDificuldade nivel) {
        this.tabuleiro = tabuleiro;
        this.corDoBot = corDoBot;
        this.nivel = nivel;
        this.random = new Random();
    }

    public Cor getCorDoBot() {
        return corDoBot;
    }

    public NivelDificuldade getNivel() {
        return nivel;
    }

    public void setNivel(NivelDificuldade nivel) {
        this.nivel = nivel;
    }

    public Movimento calcularMelhorMovimento() {
        List<Movimento> todosMovimentosLegais = obterTodosMovimentosLegais();

        if (todosMovimentosLegais.isEmpty()) {
            System.out.println("Bot não encontrou movimentos legais (Xeque-mate ou Afogamento).");
            return null; // Não há movimentos
        }

        switch (nivel) {
            case FACIL:
                return movimentoFacil(todosMovimentosLegais);
            case MEDIO:
                return movimentoMedio(todosMovimentosLegais);
            case DIFICIL:
                return movimentoDificil(todosMovimentosLegais);
            default:
                return movimentoAleatorio(todosMovimentosLegais);
        }
    }

    private List<Movimento> obterTodosMovimentosLegais() {
        List<Movimento> movimentos = new ArrayList<>();
        List<Tabuleiro.PecaComPosicao> pecasDoBot = tabuleiro.getTodasPecas(corDoBot);

        for (Tabuleiro.PecaComPosicao pcp : pecasDoBot) {
            Casa casaOrigem = pcp.casa;
            Peca peca = pcp.peca;
            List<Casa> destinosPossiveis = peca.getMovimentosPossiveis(casaOrigem, tabuleiro);
            for (Casa destino : destinosPossiveis) {
                movimentos.add(new Movimento(casaOrigem, destino, peca));
            }
        }
        return movimentos;
    }

    private Movimento movimentoFacil(List<Movimento> movimentos) {
        if (random.nextInt(100) < 30) {
            List<Movimento> capturas = new ArrayList<>();
            for (Movimento mov : movimentos) {
                if (mov.getDestino().temPeca()) {
                    capturas.add(mov);
                }
            }
            if (!capturas.isEmpty()) {
                return capturas.get(random.nextInt(capturas.size()));
            }
        }
        return movimentoAleatorio(movimentos);
    }

    private Movimento movimentoAleatorio(List<Movimento> movimentos) {
        return movimentos.get(random.nextInt(movimentos.size()));
    }

    private Movimento movimentoMedio(List<Movimento> movimentos) {
        for (Movimento mov : movimentos) {
            if (mov.getDestino().temPeca() && mov.getDestino().getPeca() instanceof Rei) {
                return mov;
            }
        }

        if (tabuleiro.isReiEmCheque(corDoBot)) {
             return movimentos.get(0);
        }

        List<Movimento> capturas = new ArrayList<>();
        for (Movimento mov : movimentos) {
            if (mov.getDestino().temPeca()) {
                capturas.add(mov);
            }
        }
        if (!capturas.isEmpty()) {
            return melhorCaptura(capturas);
        }

        List<Movimento> ameacas = new ArrayList<>();
        for (Movimento mov : movimentos) {
            if (ameacaReiInimigo(mov)) {
                ameacas.add(mov);
            }
        }
        if (!ameacas.isEmpty()) {
            return ameacas.get(random.nextInt(ameacas.size()));
        }

        List<Movimento> desenvolvimento = new ArrayList<>();
        for (Movimento mov : movimentos) {
            Peca peca = mov.getOrigem().getPeca();
            if (peca.isPrimeiroMovimento() && !(peca instanceof Peao)) {
                desenvolvimento.add(mov);
            }
        }
        if (!desenvolvimento.isEmpty()) {
            return desenvolvimento.get(random.nextInt(desenvolvimento.size()));
        }

        return movimentoAleatorio(movimentos);
    }

    private Movimento movimentoDificil(List<Movimento> movimentos) {
        Movimento melhorMovimento = null;
        int melhorPontuacao = Integer.MIN_VALUE;

        for (Movimento mov : movimentos) {
            int pontuacao = avaliarMovimentoCompleto(mov);
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorMovimento = mov;
            }
        }

        return melhorMovimento;
    }

    private int avaliarMovimentoCompleto(Movimento mov) {
        int pontuacao = 0;
        Casa origem = mov.getOrigem();
        Casa destino = mov.getDestino();
        Peca peca = origem.getPeca();
        Peca pecaCapturada = destino.getPeca();

        // Simula o movimento
        destino.setPeca(peca);
        origem.removerPeca();
        boolean eraPrimeiroMovimento = peca.isPrimeiroMovimento();
        peca.registrarMovimento();

        if (pecaCapturada != null) {
            int valorCaptura = valorPeca(pecaCapturada);
            int valorPecaMovida = valorPeca(peca);
            pontuacao += (valorCaptura >= valorPecaMovida) ? valorCaptura * 15 : valorCaptura * 8;
            if (pecaCapturada instanceof Rei) pontuacao += 100000;
        }

        Cor corInimiga = (corDoBot == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        if (tabuleiro.isCasaAtacadaPor(destino, corInimiga)) {
            pontuacao -= valorPeca(peca) * 12;
        } else {
            pontuacao += 5;
        }

        pontuacao += pontuacaoPosicaoCentro(destino) * 3;

        if (eraPrimeiroMovimento && !(peca instanceof Peao)) {
            pontuacao += 8;
        }

        if (tabuleiro.isReiEmCheque(corInimiga)) { 
            pontuacao += 60;
        }
        
        origem.setPeca(peca);
        destino.setPeca(pecaCapturada);
        if (eraPrimeiroMovimento) {
            peca.resetMoveu();
        }

        return pontuacao + random.nextInt(3); 
    }

    private int valorPeca(Peca peca) {
        if (peca instanceof Peao) return 10;
        if (peca instanceof Cavalo) return 30;
        if (peca instanceof Bispo) return 32;
        if (peca instanceof Torre) return 50;
        if (peca instanceof Rainha) return 90;
        if (peca instanceof Rei) return 10000;
        return 0;
    }

    private int pontuacaoPosicaoCentro(Casa casa) {
        int linha = casa.getLinha();
        int coluna = casa.getColuna();
        int distCentroLinha = Math.min(Math.abs(3 - linha), Math.abs(4 - linha));
        int distCentroColuna = Math.min(Math.abs(3 - coluna), Math.abs(4 - coluna));
        return 8 - (distCentroLinha + distCentroColuna);
    }

    private boolean ameacaReiInimigo(Movimento mov) {
        Cor corInimiga = (corDoBot == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        Casa casaReiInimigo = tabuleiro.getCasaDoRei(corInimiga);
        if (casaReiInimigo == null) return false;

        Casa origem = mov.getOrigem();
        Casa destino = mov.getDestino();
        Peca peca = origem.getPeca();
        Peca pecaCapturada = destino.getPeca();
        boolean eraPrimeiro = peca.isPrimeiroMovimento();

        destino.setPeca(peca);
        origem.removerPeca();
        peca.registrarMovimento();
        
        boolean ameaca = tabuleiro.isCasaAtacadaPor(casaReiInimigo, corDoBot);
        
        origem.setPeca(peca);
        destino.setPeca(pecaCapturada);
        if(eraPrimeiro) peca.resetMoveu();
        
        return ameaca;
    }

    private Movimento melhorCaptura(List<Movimento> capturas) {
        Movimento melhorCaptura = capturas.get(0);
        int maiorValor = (melhorCaptura.getDestino().temPeca()) ? valorPeca(melhorCaptura.getDestino().getPeca()) : -1;
        
        for (int i = 1; i < capturas.size(); i++) {
             Movimento mov = capturas.get(i);
             if (mov.getDestino().temPeca()) {
                 int valorAtual = valorPeca(mov.getDestino().getPeca());
                 if (valorAtual > maiorValor) {
                     maiorValor = valorAtual;
                     melhorCaptura = mov;
                 }
             }
        }
        return melhorCaptura;
    }
}
