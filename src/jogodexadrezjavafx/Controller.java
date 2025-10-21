package jogodexadrezjavafx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Controller {

    @FXML private GridPane gridPaneTabuleiro;
    @FXML private StackPane rootPane;
    @FXML private StackPane a1, b1, c1, d1, e1, f1, g1, h1;
    @FXML private StackPane a2, b2, c2, d2, e2, f2, g2, h2;
    @FXML private StackPane a3, b3, c3, d3, e3, f3, g3, h3;
    @FXML private StackPane a4, b4, c4, d4, e4, f4, g4, h4;
    @FXML private StackPane a5, b5, c5, d5, e5, f5, g5, h5;
    @FXML private StackPane a6, b6, c6, d6, e6, f6, g6, h6;
    @FXML private StackPane a7, b7, c7, d7, e7, f7, g7, h7;
    @FXML private StackPane a8, b8, c8, d8, e8, f8, g8, h8;
    @FXML private Pane uiPane;
    @FXML private Pane btnVoltar;

    private Tabuleiro tabuleiro;
    private Cor turnoAtual;
    private Peca pecaSelecionada;
    private Casa casaOrigem;
    private List<Casa> movimentosPossiveis = new ArrayList<>();
    private boolean jogoAcabou = false;

    private boolean contraBot = false;
    private Bot bot;
    private boolean processandoJogadaBot = false;

    private final Map<Casa, StackPane> mapaCasaParaStackPane = new HashMap<>();
    private final Map<StackPane, Casa> mapaStackPaneParaCasa = new HashMap<>();
    private final List<Node> destaquesMovimento = new ArrayList<>();
    private final Map<StackPane, String> estilosOriginais = new HashMap<>();
    private StackPane reiEmChequeDestacado = null;

    @FXML
    public void initialize() {
        this.tabuleiro = new Tabuleiro();
        this.turnoAtual = Cor.BRANCA;
        configurarMapeamentoECliques();
        atualizarDestaqueCheque();
    }

    public void ativarJogoContraBot(Bot.NivelDificuldade nivel) {
        this.contraBot = true;
        this.bot = new Bot(this.tabuleiro, Cor.PRETA, nivel);
        System.out.println("Modo de jogo: Player vs Bot (Nível: " + nivel + ")");
    }

    public void setModoJogo(boolean contraBot) {
        this.contraBot = contraBot;
        if (!contraBot) {
            this.bot = null;
            System.out.println("Modo de jogo: Player vs Player");
        }
    }
    
    public void aplicarEscalaParaTelaGrande() {
        double fatorEscala = 1.6;
        if(gridPaneTabuleiro != null){
            gridPaneTabuleiro.setScaleX(fatorEscala);
            gridPaneTabuleiro.setScaleY(fatorEscala);
        }
    }

    private void configurarMapeamentoECliques() {
        StackPane[][] stackPanes = {
            {a8, b8, c8, d8, e8, f8, g8, h8}, {a7, b7, c7, d7, e7, f7, g7, h7},
            {a6, b6, c6, d6, e6, f6, g6, h6}, {a5, b5, c5, d5, e5, f5, g5, h5},
            {a4, b4, c4, d4, e4, f4, g4, h4}, {a3, b3, c3, d3, e3, f3, g3, h3},
            {a2, b2, c2, d2, e2, f2, g2, h2}, {a1, b1, c1, d1, e1, f1, g1, h1}
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Casa casa = tabuleiro.getCasa(i, j);
                StackPane sp = stackPanes[i][j];
                if (casa != null && sp != null) {
                    mapaCasaParaStackPane.put(casa, sp);
                    mapaStackPaneParaCasa.put(sp, casa);
                    estilosOriginais.put(sp, sp.getStyle());
                    sp.setOnMouseClicked(event -> onCasaClicked(sp));
                }
            }
        }
        if (btnVoltar != null) {
             btnVoltar.setOnMouseClicked(this::voltarParaHome);
        }
    }

    private void onCasaClicked(StackPane spClicado) {
        if (jogoAcabou || processandoJogadaBot || (contraBot && turnoAtual != Cor.BRANCA)) {
            return;
        }

        Casa casaClicada = mapaStackPaneParaCasa.get(spClicado);
        if (casaClicada == null) return;

        if (pecaSelecionada != null) {
            if (movimentosPossiveis.contains(casaClicada)) {
                Peca pecaCapturada = casaClicada.getPeca();
                boolean promocao = tabuleiro.moverPeca(casaOrigem, casaClicada);
                moverPecaVisualmente(casaOrigem, casaClicada, promocao);
                limparSelecao();
                verificarFimDeJogoAposMovimento(pecaCapturada);

                if (!jogoAcabou) {
                    trocarTurno();
                    atualizarDestaqueCheque();
                    if (contraBot && turnoAtual == bot.getCorDoBot()) {
                        dispararJogadaDoBot();
                    }
                }
            } else {
                limparSelecao();
                if (casaClicada.temPeca() && casaClicada.getPeca().getCor() == turnoAtual) {
                    selecionarPeca(casaClicada);
                }
            }
        } else {
            if (casaClicada.temPeca() && casaClicada.getPeca().getCor() == turnoAtual) {
                selecionarPeca(casaClicada);
            }
        }
    }

    private void dispararJogadaDoBot() {
        processandoJogadaBot = true;
        if (gridPaneTabuleiro != null) gridPaneTabuleiro.setMouseTransparent(true);
        System.out.println("Bot (" + bot.getCorDoBot() + ", Nível: " + bot.getNivel() + ") está pensando...");

        new Thread(() -> {
            Movimento jogadaDoBot = bot.calcularMelhorMovimento();

            Platform.runLater(() -> {
                if (jogadaDoBot != null) {
                    System.out.println("Bot jogou: " + jogadaDoBot);
                    Peca pecaCapturada = jogadaDoBot.getDestino().getPeca();
                    boolean promocao = tabuleiro.moverPeca(jogadaDoBot.getOrigem(), jogadaDoBot.getDestino());
                    moverPecaVisualmente(jogadaDoBot.getOrigem(), jogadaDoBot.getDestino(), promocao);
                    verificarFimDeJogoAposMovimento(pecaCapturada);

                    if (!jogoAcabou) {
                        trocarTurno();
                        atualizarDestaqueCheque();
                    }
                } else {
                     verificarFimDeJogoAposMovimento(null); 
                }
                processandoJogadaBot = false;
                if (gridPaneTabuleiro != null) gridPaneTabuleiro.setMouseTransparent(false);
            });
        }).start();
    }

    private void selecionarPeca(Casa casa) {
        if (casa == null || !casa.temPeca()) return;

        pecaSelecionada = casa.getPeca();
        casaOrigem = casa;
        movimentosPossiveis = pecaSelecionada.getMovimentosPossiveis(casa, tabuleiro);
        System.out.println("Selecionado: " + pecaSelecionada.getClass().getSimpleName() + " em (" + casa.getLinha() + "," + casa.getColuna() + "). Movimentos legais: " + movimentosPossiveis.size());
        mostrarDestaques();
    }

    private void moverPecaVisualmente(Casa origem, Casa destino, boolean promocaoOcorreuLogica) {
        Peca pecaMovida = destino.getPeca();
        if (pecaMovida == null) {
            System.err.println("Erro visual: Peça movida não encontrada no destino lógico (" + destino.getLinha() + "," + destino.getColuna() + ")");
            return;
        }

        StackPane spOrigem = mapaCasaParaStackPane.get(origem);
        StackPane spDestino = mapaCasaParaStackPane.get(destino);

        ImageView pecaVisual = null;
        if (spOrigem != null) {
             pecaVisual = (ImageView) spOrigem.getChildren().stream()
                .filter(node -> node instanceof ImageView).findFirst().orElse(null);
        } else {
             System.err.println("Erro visual: StackPane de origem não encontrado para " + origem.getLinha() + "," + origem.getColuna());
        }

        if (spOrigem != null && pecaVisual != null) {
            spOrigem.getChildren().remove(pecaVisual);
        } else if (spOrigem != null) {
             spOrigem.getChildren().removeIf(node -> node instanceof ImageView);
             System.err.println("Aviso visual: ImageView não encontrada diretamente no StackPane de origem: (" + origem.getLinha() + "," + origem.getColuna() + ")");
        }

        if (spDestino != null) {
            spDestino.getChildren().removeIf(node -> node instanceof ImageView);
            if (pecaVisual != null) {
                if(pecaVisual.getParent() != null) ((Pane)pecaVisual.getParent()).getChildren().remove(pecaVisual);
                spDestino.getChildren().add(pecaVisual);
            } else {
                System.err.println("Erro visual: Não foi possível adicionar pecaVisual (nula) ao destino: (" + destino.getLinha() + "," + destino.getColuna() + "). Tentando recriar.");
                String nomePeca = pecaMovida.getClass().getSimpleName();
                String corPeca = (pecaMovida.getCor() == Cor.BRANCA) ? "Branca" : "Preta";
                String caminhoRecurso = "resources/imagens/Peca" + nomePeca + corPeca + ".png";
                try(InputStream is = Controller.class.getClassLoader().getResourceAsStream(caminhoRecurso)){
                    if(is != null){
                        Image img = new Image(is);
                        ImageView iv = new ImageView(img);
                        iv.setFitHeight(70.0); iv.setFitWidth(70.0); iv.setPreserveRatio(true); iv.setMouseTransparent(true);
                        spDestino.getChildren().add(iv);
                    }
                } catch (Exception e) {}
            }
        }

        boolean isRoque = pecaMovida instanceof Rei && Math.abs(destino.getColuna() - origem.getColuna()) == 2;
        if (isRoque) {
            int linha = origem.getLinha();
            int colunaTorreOrigemIdx = (destino.getColuna() == 6) ? 7 : 0;
            int colunaTorreDestinoIdx = (destino.getColuna() == 6) ? 5 : 3;
            Casa casaTorreOrigem = tabuleiro.getCasa(linha, colunaTorreOrigemIdx);
            StackPane spTorreOrigem = mapaCasaParaStackPane.get(casaTorreOrigem);
            Casa casaTorreDestino = tabuleiro.getCasa(linha, colunaTorreDestinoIdx);
            StackPane spTorreDestino = mapaCasaParaStackPane.get(casaTorreDestino);
            
            Optional<Node> foundTorre = spTorreOrigem.getChildren().stream().filter(node -> node instanceof ImageView).findFirst();
            if (foundTorre.isPresent()) {
                ImageView imgTorre = (ImageView) foundTorre.get();
                spTorreOrigem.getChildren().remove(imgTorre);
                spTorreDestino.getChildren().removeIf(node -> node instanceof ImageView);
                spTorreDestino.getChildren().add(imgTorre);
            }
        }

        if(promocaoOcorreuLogica && destino.getPeca() instanceof Rainha){
            String caminhoImagemRainha = (destino.getPeca().getCor() == Cor.BRANCA)
                ? "/resources/imagens/PecaRainhaBranca.png"
                : "/resources/imagens/PecaRainhaPreta.png";
            try (InputStream is = getClass().getResourceAsStream(caminhoImagemRainha)) {
                 if (is != null) {
                     Image novaImagem = new Image(is);
                     ImageView imageViewNoDestino = (ImageView) spDestino.getChildren().stream()
                        .filter(node -> node instanceof ImageView).findFirst().orElse(null);
                     if(imageViewNoDestino != null) {
                         imageViewNoDestino.setImage(novaImagem);
                     }
                 }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void verificarFimDeJogoAposMovimento(Peca pecaCapturada) {
        Cor corOponente = (turnoAtual == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        tabuleiro.verificarChequeAposMovimento(corOponente);

        String vencedor = null;

        if (pecaCapturada instanceof Rei) {
            vencedor = (turnoAtual == Cor.BRANCA) ? "BRANCAS" : "PRETAS";
        } else if (tabuleiro.getContadorChecksBrancas() >= 3) {
            vencedor = "PRETAS";
        } else if (tabuleiro.getContadorChecksPretas() >= 3) {
            vencedor = "BRANCAS";
        } else if (tabuleiro.isXequeMate(corOponente)) {
            vencedor = (turnoAtual == Cor.BRANCA) ? "BRANCAS" : "PRETAS";
        } else if (!temMovimentosLegais(corOponente)) {
             vencedor = "EMPATE por Afogamento";
        }

        if (vencedor != null) {
            jogoAcabou = true;
            mostrarDialogoFimDeJogo(vencedor);
        }
    }

    private boolean temMovimentosLegais(Cor cor) {
        List<Tabuleiro.PecaComPosicao> pecas = tabuleiro.getTodasPecas(cor);
        for (Tabuleiro.PecaComPosicao pcp : pecas) {
            if (!pcp.peca.getMovimentosPossiveis(pcp.casa, tabuleiro).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void mostrarDialogoFimDeJogo(String resultado) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fim de Jogo!");
        if (resultado.contains("EMPATE")) {
             alert.setHeaderText(resultado);
        } else {
             alert.setHeaderText("As " + resultado + " venceram!");
        }
        alert.setContentText(null);
        
        alert.showAndWait();
        
        Node nodeParaStage = gridPaneTabuleiro != null ? gridPaneTabuleiro : btnVoltar;
        if (nodeParaStage != null) {
            irParaTelaHome(nodeParaStage);
        }
    }

    private void limparSelecao() {
        pecaSelecionada = null;
        casaOrigem = null;
        movimentosPossiveis.clear();
        limparDestaques();
    }

    private void trocarTurno() {
        if (!jogoAcabou) {
            turnoAtual = (turnoAtual == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
             System.out.println("Turno das: " + turnoAtual);
        }
    }

    private void mostrarDestaques() {
        limparDestaques();
        StackPane spOrigem = mapaCasaParaStackPane.get(casaOrigem);
        if (spOrigem != null) {
            spOrigem.setStyle(estilosOriginais.get(spOrigem) + "; -fx-border-color: yellow; -fx-border-width: 3; -fx-border-radius: 5;");
            destaquesMovimento.add(spOrigem);
        }
        for (Casa casaDestino : movimentosPossiveis) {
            StackPane spDestino = mapaCasaParaStackPane.get(casaDestino);
            if (spDestino != null) {
                 Node destaque;
                 if (casaDestino.temPeca()) {
                     Circle c = new Circle(30, Color.TRANSPARENT);
                     c.setStroke(Color.rgb(200, 0, 0, 0.7));
                     c.setStrokeWidth(4);
                     destaque = c;
                 } else {
                     destaque = new Circle(15, Color.rgb(0, 0, 0, 0.3));
                 }
                destaque.setMouseTransparent(true);
                destaquesMovimento.add(destaque);
                spDestino.getChildren().add(destaque);
            }
        }
    }

    private void limparDestaques() {
        for (Node node : destaquesMovimento) {
            if (node instanceof Circle) {
                if (node.getParent() instanceof StackPane) {
                    ((StackPane) node.getParent()).getChildren().remove(node);
                }
            } else if (node instanceof StackPane) {
                StackPane sp = (StackPane) node;
                 if (sp != reiEmChequeDestacado) {
                    sp.setStyle(estilosOriginais.get(sp));
                 } else {
                     sp.setStyle(estilosOriginais.get(sp) + "; -fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
                 }
            }
        }
        destaquesMovimento.clear();
    }

    private void atualizarDestaqueCheque() {
        if (reiEmChequeDestacado != null) {
            reiEmChequeDestacado.setStyle(estilosOriginais.get(reiEmChequeDestacado));
            reiEmChequeDestacado = null;
        }

        Casa reiBranco = tabuleiro.getCasaDoRei(Cor.BRANCA);
        if (reiBranco != null && tabuleiro.isReiEmCheque(Cor.BRANCA)) {
            reiEmChequeDestacado = mapaCasaParaStackPane.get(reiBranco);
        }

        Casa reiPreto = tabuleiro.getCasaDoRei(Cor.PRETA);
        if (reiPreto != null && tabuleiro.isReiEmCheque(Cor.PRETA)) {
            reiEmChequeDestacado = mapaCasaParaStackPane.get(reiPreto);
        }
        
        if (reiEmChequeDestacado != null) {
            reiEmChequeDestacado.setStyle(estilosOriginais.get(reiEmChequeDestacado) + "; -fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
        }
    }

    private void irParaTelaHome(Node nodeOrigem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Home.fxml"));
            Parent homeRoot = loader.load();
            Stage stage = (Stage) nodeOrigem.getScene().getWindow();
            
            Scene homeScene = new Scene(homeRoot, 970, 630);
            stage.setScene(homeScene);
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.setTitle("ChessMasterFX");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void voltarParaHome(MouseEvent event) {
        irParaTelaHome((Node) event.getSource());
    }
}
