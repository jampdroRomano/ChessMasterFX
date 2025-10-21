package jogodexadrezjavafx;

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
    
    public void setNivel(NivelDificuldade nivel) {
        this.nivel = nivel;
    }
    
    public Movimento calcularMelhorMovimento() {
        List<Movimento> todosMovimentos = obterTodosMovimentosPossiveis();
        
        List<Movimento> movimentosValidos = filtrarMovimentosValidos(todosMovimentos);
        
        if (movimentosValidos.isEmpty()) {
            return null;
        }
        
        switch (nivel) {
            case FACIL:
                return movimentoFacil(movimentosValidos);
            case MEDIO:
                return movimentoMedio(movimentosValidos);
            case DIFICIL:
                return movimentoDificil(movimentosValidos);
            default:
                return movimentoAleatorio(movimentosValidos);
        }
    }
    
    private List<Movimento> obterTodosMovimentosPossiveis() {
        List<Movimento> movimentos = new ArrayList<>();
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Casa casa = tabuleiro.getCasa(i, j);
                if (casa.temPeca() && casa.getPeca().getCor() == corDoBot) {
                    List<Casa> movimentosPeca = casa.getPeca().getMovimentosPossiveis(casa, tabuleiro);
                    for (Casa destino : movimentosPeca) {
                        movimentos.add(new Movimento(casa, destino));
                    }
                }
            }
        }
        
        return movimentos;
    }
    
    private List<Movimento> filtrarMovimentosValidos(List<Movimento> movimentos) {
        List<Movimento> movimentosValidos = new ArrayList<>();
        
        for (Movimento mov : movimentos) {
            if (!deixaReiEmCheque(mov)) {
                movimentosValidos.add(mov);
            }
        }
        
        return movimentosValidos;
    }
    
    private boolean deixaReiEmCheque(Movimento mov) {
        Casa origem = mov.getOrigem();
        Casa destino = mov.getDestino();
        Peca peca = origem.getPeca();
        Peca pecaCapturada = destino.getPeca();
        
        destino.setPeca(peca);
        origem.removerPeca();
        
        Casa casaRei = tabuleiro.getCasaDoRei(corDoBot);
        Cor corInimiga = (corDoBot == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        boolean emCheque = (casaRei != null) && tabuleiro.isCasaAtacadaPor(casaRei, corInimiga);
        
        origem.setPeca(peca);
        destino.setPeca(pecaCapturada);
        
        return emCheque;
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
        
        Casa casaRei = tabuleiro.getCasaDoRei(corDoBot);
        Cor corInimiga = (corDoBot == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        if (casaRei != null && tabuleiro.isCasaAtacadaPor(casaRei, corInimiga)) {
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
        
        if (destino.temPeca()) {
            int valorCaptura = valorPeca(destino.getPeca());
            int valorPecaMovida = valorPeca(peca);
            
            if (valorCaptura >= valorPecaMovida) {
                pontuacao += valorCaptura * 15;
            } else {
                pontuacao += valorCaptura * 8;
            }
            
            if (destino.getPeca() instanceof Rei) {
                pontuacao += 100000;
            }
        }
        
        Cor corInimiga = (corDoBot == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        
        Peca pecaCapturada = destino.getPeca();
        destino.setPeca(peca);
        origem.removerPeca();
        
        if (tabuleiro.isCasaAtacadaPor(destino, corInimiga)) {
            pontuacao -= valorPeca(peca) * 12;
        } else {
            pontuacao += 5;
        }
        
        origem.setPeca(peca);
        destino.setPeca(pecaCapturada);
        
        pontuacao += pontuacaoPosicaoCentro(destino) * 3;
        
        if (peca.isPrimeiroMovimento() && !(peca instanceof Peao)) {
            pontuacao += 8;
        }
        
        if (ameacaReiInimigo(mov)) {
            pontuacao += 60;
        }
        
        Casa casaRei = tabuleiro.getCasaDoRei(corDoBot);
        if (casaRei != null) {
            int distanciaRei = Math.abs(destino.getLinha() - casaRei.getLinha()) + 
                              Math.abs(destino.getColuna() - casaRei.getColuna());
            
            if (distanciaRei <= 2 && !(peca instanceof Rei)) {
                pontuacao += 4;
            }
        }
        
        if (peca instanceof Peao) {
            int linhaPromocao = (corDoBot == Cor.BRANCA) ? 0 : 7;
            if (destino.getLinha() == linhaPromocao) {
                pontuacao += 90; 
            } else {
                int distanciaPromocao = Math.abs(destino.getLinha() - linhaPromocao);
                pontuacao += (8 - distanciaPromocao) * 2;
            }
        }
        
        destino.setPeca(peca);
        origem.removerPeca();
        int mobilidade = peca.getMovimentosPossiveis(destino, tabuleiro).size();
        pontuacao += mobilidade * 2;
        origem.setPeca(peca);
        destino.setPeca(pecaCapturada);
        
        if (peca instanceof Torre || peca instanceof Rainha) {
            pontuacao += avaliarControleLinhas(destino) * 3;
        }
        
        return pontuacao;
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
    
    private int avaliarControleLinhas(Casa casa) {
        int pontos = 0;
        int linha = casa.getLinha();
        int coluna = casa.getColuna();
        
        boolean linhaLivre = true;
        for (int c = 0; c < 8; c++) {
            if (c != coluna) {
                Casa casaLinha = tabuleiro.getCasa(linha, c);
                if (casaLinha != null && casaLinha.temPeca()) {
                    linhaLivre = false;
                    break;
                }
            }
        }
        if (linhaLivre) pontos += 5;
        
        boolean colunaLivre = true;
        for (int l = 0; l < 8; l++) {
            if (l != linha) {
                Casa casaColuna = tabuleiro.getCasa(l, coluna);
                if (casaColuna != null && casaColuna.temPeca()) {
                    colunaLivre = false;
                    break;
                }
            }
        }
        if (colunaLivre) pontos += 5;
        
        return pontos;
    }
    
    private boolean ameacaReiInimigo(Movimento mov) {
        Cor corInimiga = (corDoBot == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        Casa casaReiInimigo = tabuleiro.getCasaDoRei(corInimiga);
        
        if (casaReiInimigo == null) {
            return false;
        }
        
        Casa origem = mov.getOrigem();
        Casa destino = mov.getDestino();
        Peca peca = origem.getPeca();
        Peca pecaCapturada = destino.getPeca();
        
        destino.setPeca(peca);
        origem.removerPeca();
        
        boolean ameaca = tabuleiro.isCasaAtacadaPor(casaReiInimigo, corDoBot);
        
        origem.setPeca(peca);
        destino.setPeca(pecaCapturada);
        
        return ameaca;
    }

    private Movimento melhorCaptura(List<Movimento> capturas) {
        Movimento melhorCaptura = capturas.get(0);
        int maiorValor = valorPeca(melhorCaptura.getDestino().getPeca());
        
        for (Movimento mov : capturas) {
            int valor = valorPeca(mov.getDestino().getPeca());
            if (valor > maiorValor) {
                maiorValor = valor;
                melhorCaptura = mov;
            }
        }
        
        return melhorCaptura;
    }
    
    public static class Movimento {
        private final Casa origem;
        private final Casa destino;
        
        public Movimento(Casa origem, Casa destino) {
            this.origem = origem;
            this.destino = destino;
        }
        
        public Casa getOrigem() {
            return origem;
        }
        
        public Casa getDestino() {
            return destino;
        }
    }
}