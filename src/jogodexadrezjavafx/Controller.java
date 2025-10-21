package jogodexadrezjavafx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private int vitoriasBrancas = 0;
    private int vitoriasPretas = 0;

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
        if (jogoAcabou) return;

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
                }
            } else {
                limparSelecao();
                if (casaClicada.temPeca() && casaClicada.getPeca().getCor() == turnoAtual) {
                    selecionarPeca(casaClicada);
                } else {
                     atualizarDestaqueCheque();
                }
            }
        } else {
            if (casaClicada.temPeca() && casaClicada.getPeca().getCor() == turnoAtual) {
                selecionarPeca(casaClicada);
            }
        }
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
                spDestino.getChildren().add(pecaVisual);
            } else {
                 System.err.println("Erro visual: Não foi possível adicionar pecaVisual (nula) ao destino: (" + destino.getLinha() + "," + destino.getColuna() + ")");
            }
        } else {
             System.err.println("Erro visual: StackPane de destino não encontrado para (" + destino.getLinha() + "," + destino.getColuna() + ")");
        }

        boolean isRoque = pecaMovida instanceof Rei && Math.abs(destino.getColuna() - origem.getColuna()) == 2;
        if (isRoque) {
            System.out.println("Movendo visualmente a Torre para o Roque...");
            int linha = origem.getLinha();
            int colunaTorreOrigemIdx = (destino.getColuna() == 6) ? 7 : 0;
            int colunaTorreDestinoIdx = (destino.getColuna() == 6) ? 5 : 3;
            Casa casaTorreOrigem = tabuleiro.getCasa(linha, colunaTorreOrigemIdx);
            StackPane spTorreOrigem = mapaCasaParaStackPane.get(casaTorreOrigem);
            Casa casaTorreDestino = tabuleiro.getCasa(linha, colunaTorreDestinoIdx);
            StackPane spTorreDestino = mapaCasaParaStackPane.get(casaTorreDestino);
            ImageView imgTorre = null;

            if (spTorreOrigem != null) {
                Optional<Node> foundTorre = spTorreOrigem.getChildren().stream()
                   .filter(node -> node instanceof ImageView).findFirst();
                if (foundTorre.isPresent()) {
                    imgTorre = (ImageView) foundTorre.get();
                    spTorreOrigem.getChildren().remove(imgTorre);
                     System.out.println("  > Imagem da torre removida da origem visual (" + linha + "," + colunaTorreOrigemIdx + ")");
                } else {
                     System.err.println("  > Aviso visual Roque: Imagem da torre não encontrada na casa de origem visual (" + linha + "," + colunaTorreOrigemIdx + "). Limpando.");
                     spTorreOrigem.getChildren().removeIf(node -> node instanceof ImageView);
                }
            } else {
                 System.err.println("  > Erro visual Roque: StackPane de ORIGEM da torre nulo.");
            }

            if (spTorreDestino != null) {
                spTorreDestino.getChildren().removeIf(node -> node instanceof ImageView);
                if (imgTorre != null) {
                    if(imgTorre.getParent() != null) ((Pane)imgTorre.getParent()).getChildren().remove(imgTorre);
                    spTorreDestino.getChildren().add(imgTorre);
                    System.out.println("  > Imagem da torre adicionada ao destino visual (" + linha + "," + colunaTorreDestinoIdx + ")");
                } else {
                     System.err.println("  > Erro visual Roque: Imagem da torre nula. Não pode adicionar ao destino visual.");
                     Peca torreLogica = casaTorreDestino.getPeca();
                     if(torreLogica instanceof Torre){
                          String corTorre = (torreLogica.getCor() == Cor.BRANCA) ? "Branca" : "Preta";
                          String caminhoTorre = "resources/imagens/PecaTorre" + corTorre + ".png";
                          try(InputStream is = Controller.class.getClassLoader().getResourceAsStream(caminhoTorre)){
                               if(is != null){
                                    Image img = new Image(is);
                                    ImageView iv = new ImageView(img);
                                    iv.setFitHeight(70.0); iv.setFitWidth(70.0); iv.setPreserveRatio(true); iv.setMouseTransparent(true);
                                    spTorreDestino.getChildren().add(iv);
                                    System.out.println("  > Fallback: Imagem da torre RECRIADA e adicionada.");
                               } else { System.err.println("  > Fallback falhou: Recurso da torre não encontrado em " + caminhoTorre);}
                          } catch (Exception e) { System.err.println("  > Fallback de imagem da torre falhou com exceção: " + e.getMessage());}
                     }
                }
            } else {
                System.err.println("  > Erro visual Roque: StackPane de DESTINO da torre nulo.");
            }
        }

        if(promocaoOcorreuLogica && pecaVisual != null && pecaMovida instanceof Rainha){
            System.out.println("Atualizando imagem visual para Promoção!");
            String caminhoImagemRainha = (pecaMovida.getCor() == Cor.BRANCA)
                ? "/resources/imagens/PecaRainhaBranca.png"
                : "/resources/imagens/PecaRainhaPreta.png";
            try (InputStream is = getClass().getResourceAsStream(caminhoImagemRainha)) {
                 if (is == null) {
                    System.err.println("Erro CRÍTICO ao carregar imagem visual da rainha para promoção: Recurso não encontrado em " + caminhoImagemRainha);
                 } else {
                     Image novaImagem = new Image(is);
                     if (novaImagem.isError()) {
                        System.err.println("Erro ao decodificar imagem visual da rainha: " + caminhoImagemRainha);
                         if(novaImagem.getException() != null) novaImagem.getException().printStackTrace();
                     } else {
                         pecaVisual.setImage(novaImagem);
                     }
                 }
            } catch (Exception e) {
                System.err.println("Exceção geral ao carregar imagem visual da rainha para promoção: " + e.getMessage());
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
            System.out.println("Vitória por captura do Rei!");
            if ("BRANCAS".equals(vencedor)) vitoriasBrancas++; else vitoriasPretas++;
        }
        else if (tabuleiro.getContadorChecksBrancas() >= 3) {
            vencedor = "PRETAS";
            System.out.println("Vitória das PRETAS por 3 cheques!");
            vitoriasPretas++;
        } else if (tabuleiro.getContadorChecksPretas() >= 3) {
            vencedor = "BRANCAS";
            System.out.println("Vitória das BRANCAS por 3 cheques!");
            vitoriasBrancas++;
        }
        else if (tabuleiro.isXequeMate(corOponente)) {
            vencedor = (turnoAtual == Cor.BRANCA) ? "BRANCAS" : "PRETAS";
            System.out.println("Vitória por Xeque-mate!");
             if ("BRANCAS".equals(vencedor)) vitoriasBrancas++; else vitoriasPretas++;
        }


        if (vencedor != null) {
            jogoAcabou = true;
            atualizarDestaqueCheque();
            mostrarDialogoFimDeJogo(vencedor);
        }
    }


    private void mostrarDialogoFimDeJogo(String vencedor) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fim de Jogo!");
        alert.setHeaderText("As " + vencedor + " venceram!");
        alert.setContentText(null);

        Node nodeParaStage = gridPaneTabuleiro != null ? gridPaneTabuleiro : btnVoltar;
         if(nodeParaStage == null && rootPane != null) nodeParaStage = rootPane;

        alert.showAndWait();

        if (nodeParaStage != null && nodeParaStage.getScene() != null) {
            irParaTelaHome(nodeParaStage);
        } else {
            System.err.println("Erro: Não foi possível obter um Node válido para retornar à tela Home após o diálogo.");
            if (gridPaneTabuleiro != null) gridPaneTabuleiro.setDisable(true);
            if (btnVoltar != null) btnVoltar.setDisable(true);
        }
    }

    private void reiniciarJogoInternamente() {
        System.out.println("Iniciando reinício interno do jogo...");
        tabuleiro.reset();
        turnoAtual = Cor.BRANCA;
        pecaSelecionada = null;
        casaOrigem = null;
        movimentosPossiveis.clear();
        jogoAcabou = false;

        for (Map.Entry<Casa, StackPane> entry : mapaCasaParaStackPane.entrySet()) {
            Casa casa = entry.getKey();
            StackPane sp = entry.getValue();
            if (sp == null) continue;

            sp.getChildren().removeIf(node -> !(node instanceof javafx.scene.control.Label));
            sp.setStyle(estilosOriginais.get(sp));

            if (casa.temPeca()) {
                Peca peca = casa.getPeca();
                String nomePeca = peca.getClass().getSimpleName();
                String corPeca = (peca.getCor() == Cor.BRANCA) ? "Branca" : "Preta";
                String caminhoRecurso = "resources/imagens/Peca" + nomePeca + corPeca + ".png";

                Image imagemPeca = null;
                InputStream is = null;

                try {
                     is = Controller.class.getClassLoader().getResourceAsStream(caminhoRecurso);

                    if (is == null) {
                        System.err.println("Erro CRÍTICO ao carregar imagem: Recurso NÃO ENCONTRADO em '" + caminhoRecurso + "'.");
                        System.err.println("Verifique:");
                        System.err.println("1. Se o arquivo existe EXATAMENTE com este nome (maiúsculas/minúsculas) em 'src/resources/imagens/'.");
                        System.err.println("2. Se a pasta 'resources' está diretamente dentro de 'src'.");
                        System.err.println("3. Se o arquivo foi copiado para 'build/classes/resources/imagens/' após a compilação.");
                    } else {
                        imagemPeca = new Image(is);
                        if (imagemPeca.isError()) {
                            System.err.println("Erro ao decodificar imagem: " + caminhoRecurso);
                            if(imagemPeca.getException() != null) imagemPeca.getException().printStackTrace();
                        } else {
                            ImageView imageView = new ImageView(imagemPeca);
                            imageView.setFitHeight(70.0);
                            imageView.setFitWidth(70.0);
                            imageView.setPreserveRatio(true);
                            imageView.setMouseTransparent(true);
                            sp.getChildren().add(imageView);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Exceção geral ao recarregar imagem (" + caminhoRecurso + "): " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) { /* Ignora */ }
                    }
                }
            }
        }

        if (gridPaneTabuleiro != null) gridPaneTabuleiro.setDisable(false);
        if (btnVoltar != null) btnVoltar.setDisable(false);

        limparDestaques();
        reiEmChequeDestacado = null;
        atualizarDestaqueCheque();
        System.out.println("Jogo reiniciado (interno). Turno das: " + turnoAtual);
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
            String estiloOriginal = estilosOriginais.get(spOrigem);
            spOrigem.setStyle(estiloOriginal + "; -fx-border-color: yellow; -fx-border-width: 3; -fx-border-radius: 5;");
            destaquesMovimento.add(spOrigem);
        }

        for (Casa casaDestino : movimentosPossiveis) {
            StackPane spDestino = mapaCasaParaStackPane.get(casaDestino);
            if (spDestino != null) {
                 Node destaque;
                 boolean temPecaOponente = casaDestino.temPeca() && casaDestino.getPeca().getCor() != turnoAtual;

                 if (temPecaOponente) {
                     Circle circuloCaptura = new Circle(25, Color.rgb(180, 0, 0, 0.4));
                     circuloCaptura.setStroke(Color.DARKRED);
                     circuloCaptura.setStrokeWidth(2);
                     circuloCaptura.setMouseTransparent(true);
                     destaque = circuloCaptura;
                 } else {
                     Circle circuloNormal = new Circle(15, Color.rgb(128, 128, 128, 0.5));
                     circuloNormal.setMouseTransparent(true);
                     destaque = circuloNormal;
                 }
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

        Casa reiEmCheque = null;
        if (tabuleiro.isReiEmCheque(Cor.BRANCA)) {
            reiEmCheque = tabuleiro.getCasaDoRei(Cor.BRANCA);
        } else if (tabuleiro.isReiEmCheque(Cor.PRETA)) {
            reiEmCheque = tabuleiro.getCasaDoRei(Cor.PRETA);
        }

        if (reiEmCheque != null) {
            StackPane spRei = mapaCasaParaStackPane.get(reiEmCheque);
            if (spRei != null) {
                spRei.setStyle(estilosOriginais.get(spRei) + "; -fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
                reiEmChequeDestacado = spRei;
            }
        }
    }

    private void irParaTelaHome(Node nodeOrigem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Home.fxml"));
            Parent homeRoot = loader.load();
            Stage stage = null;
            if (nodeOrigem != null && nodeOrigem.getScene() != null) {
                 stage = (Stage) nodeOrigem.getScene().getWindow();
            } else {
                 System.err.println("Não foi possível obter o Stage a partir do Node. Tentando pelo gridPane.");
                 if (gridPaneTabuleiro != null && gridPaneTabuleiro.getScene() != null) {
                     stage = (Stage) gridPaneTabuleiro.getScene().getWindow();
                 }
            }

            if (stage == null) {
                 System.err.println("Falha crítica: Não foi possível obter o Stage para voltar ao menu.");
                 return;
            }

            Scene homeScene = new Scene(homeRoot, 970, 630);
            stage.setScene(homeScene);
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.setTitle("ChessMasterFX");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela Home: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
             System.err.println("Erro: Não foi possível encontrar o arquivo FXML Home.fxml. Verifique o caminho: /resources/Home.fxml - " + e.getMessage());
             e.printStackTrace();
        } catch (Exception e) {
             System.err.println("Ocorreu um erro inesperado ao voltar para Home: " + e.getMessage());
             e.printStackTrace();
        }
    }

    @FXML
    private void voltarParaHome(MouseEvent event) {
         irParaTelaHome((Node) event.getSource());
    }
}