package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Controller {

    @FXML private GridPane gridPaneTabuleiro;
    // A variável rootPane foi mantida para a lógica de escala.
    @FXML private StackPane rootPane;

    @FXML private StackPane a1, b1, c1, d1, e1, f1, g1, h1;
    @FXML private StackPane a2, b2, c2, d2, e2, f2, g2, h2;
    @FXML private StackPane a3, b3, c3, d3, e3, f3, g3, h3;
    @FXML private StackPane a4, b4, c4, d4, e4, f4, g4, h4;
    @FXML private StackPane a5, b5, c5, d5, e5, f5, g5, h5;
    @FXML private StackPane a6, b6, c6, d6, e6, f6, g6, h6;
    @FXML private StackPane a7, b7, c7, d7, e7, f7, g7, h7;
    @FXML private StackPane a8, b8, c8, d8, e8, f8, g8, h8;

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

    @FXML
    public void initialize() {
        this.tabuleiro = new Tabuleiro();
        this.turnoAtual = Cor.BRANCA;
        configurarMapeamentoECliques();
    }
    
    // O método de validação de tela foi mantido.
    public void aplicarEscalaParaTelaGrande() {
        double fatorEscala = 1.6; 
        if(gridPaneTabuleiro != null){
            gridPaneTabuleiro.setScaleX(fatorEscala);
            gridPaneTabuleiro.setScaleY(fatorEscala);
        }
    }
    
    private void configurarMapeamentoECliques() {
        StackPane[][] stackPanes = {
            {a8, b8, c8, d8, e8, f8, g8, h8},
            {a7, b7, c7, d7, e7, f7, g7, h7},
            {a6, b6, c6, d6, e6, f6, g6, h6},
            {a5, b5, c5, d5, e5, f5, g5, h5},
            {a4, b4, c4, d4, e4, f4, g4, h4},
            {a3, b3, c3, d3, e3, f3, g3, h3},
            {a2, b2, c2, d2, e2, f2, g2, h2},
            {a1, b1, c1, d1, e1, f1, g1, h1}
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
    }
    
    private void onCasaClicked(StackPane spClicado) {
        if (jogoAcabou) return;

        Casa casaClicada = mapaStackPaneParaCasa.get(spClicado);

        if (pecaSelecionada != null) {
            if (movimentosPossiveis.contains(casaClicada)) {
                Peca pecaCapturada = casaClicada.getPeca();
                moverPeca(casaOrigem, casaClicada);
                verificarFimDeJogoAposMovimento(pecaCapturada);
                if (!jogoAcabou) {
                    trocarTurno();
                }
                limparSelecao();
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

    private void moverPeca(Casa origem, Casa destino) {
        Peca pecaMovida = origem.getPeca();
        
        StackPane spOrigem = mapaCasaParaStackPane.get(origem);
        StackPane spDestino = mapaCasaParaStackPane.get(destino);
        ImageView pecaVisual = (ImageView) spOrigem.getChildren().stream()
                .filter(node -> node instanceof ImageView).findFirst().orElse(null);

        // A lógica de mover a peça é chamada primeiro, e nos diz se houve promoção
        boolean promocaoOcorreu = tabuleiro.moverPeca(origem, destino);

        // Lógica para mover a peça VISUALMENTE
        if (pecaMovida instanceof Rei && Math.abs(destino.getColuna() - origem.getColuna()) == 2) {
            spDestino.getChildren().removeIf(node -> node instanceof ImageView);
            if (pecaVisual != null) spDestino.getChildren().add(pecaVisual);
            
            Casa casaOrigemTorre = tabuleiro.moverTorreRoque(destino);
            StackPane spOrigemTorre = mapaCasaParaStackPane.get(casaOrigemTorre);
            int colunaDestinoTorre = (destino.getColuna() == 6) ? 5 : 3;
            StackPane spDestinoTorre = mapaCasaParaStackPane.get(tabuleiro.getCasa(origem.getLinha(), colunaDestinoTorre));
            ImageView torreVisual = (ImageView) spOrigemTorre.getChildren().stream()
                .filter(node -> node instanceof ImageView).findFirst().orElse(null);
            
            if (torreVisual != null) spDestinoTorre.getChildren().add(torreVisual);

        } else {
            spDestino.getChildren().removeIf(node -> node instanceof ImageView);
            if (pecaVisual != null) {
                spDestino.getChildren().add(pecaVisual);
            }
        }
        
        // --- NOVA LÓGICA VISUAL DA PROMOÇÃO ---
        if (promocaoOcorreu && pecaVisual != null) {
            System.out.println("Promoção! Peão virou Rainha.");
            String caminhoImagemRainha = (pecaMovida.getCor() == Cor.BRANCA) 
                ? "/resources/imagens/PecaRainhaBranca.png" 
                : "/resources/imagens/PecaRainhaPreta.png";
            try {
                Image novaImagem = new Image(getClass().getResourceAsStream(caminhoImagemRainha));
                pecaVisual.setImage(novaImagem);
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem da rainha para promoção: " + e.getMessage());
            }
        }
    }
    
    private void verificarFimDeJogoAposMovimento(Peca pecaCapturada) {
        Cor corOponente = (turnoAtual == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        tabuleiro.verificarChequeAposMovimento(corOponente);

        String vencedor = null;

        if (tabuleiro.getContadorChecksBrancas() >= 3) {
            vencedor = "PRETAS";
            vitoriasPretas++;
        } else if (tabuleiro.getContadorChecksPretas() >= 3) {
            vencedor = "BRANCAS";
            vitoriasBrancas++;
        } else if (pecaCapturada instanceof Rei) {
            vencedor = (pecaCapturada.getCor() == Cor.BRANCA) ? "PRETAS" : "BRANCAS";
            if ("PRETAS".equals(vencedor)) vitoriasPretas++; else vitoriasBrancas++;
        }
        
        if (vencedor != null) {
            jogoAcabou = true;
            limparDestaques();
            mostrarDialogoFimDeJogo(vencedor);
        }
    }
    
    private void mostrarDialogoFimDeJogo(String vencedor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fim de Jogo!");
        alert.setHeaderText("As " + vencedor + " venceram!");
        alert.setContentText("Deseja jogar novamente?");

        ButtonType botaoSim = new ButtonType("Sim");
        ButtonType botaoNao = new ButtonType("Não");

        alert.getButtonTypes().setAll(botaoSim, botaoNao);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == botaoSim){
            // Lógica para reiniciar ainda não implementada
        } else {
            gridPaneTabuleiro.setDisable(true);
        }
    }

    private void selecionarPeca(Casa casa) {
        pecaSelecionada = casa.getPeca();
        casaOrigem = casa;
        movimentosPossiveis = pecaSelecionada.getMovimentosPossiveis(casa, tabuleiro);
        mostrarDestaques();
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
        }
    }

    private void mostrarDestaques() {
        limparDestaques();
        
        StackPane spOrigem = mapaCasaParaStackPane.get(casaOrigem);
        if (spOrigem != null) {
            String estiloOriginal = estilosOriginais.get(spOrigem);
            spOrigem.setStyle(estiloOriginal + "; -fx-border-color: yellow; -fx-border-width: 3;");
            destaquesMovimento.add(spOrigem);
        }
        
        for (Casa casaDestino : movimentosPossiveis) {
            StackPane spDestino = mapaCasaParaStackPane.get(casaDestino);
            if (spDestino != null) {
                Circle circulo = new Circle(15, Color.rgb(128, 128, 128, 0.5));
                circulo.setMouseTransparent(true);
                destaquesMovimento.add(circulo);
                spDestino.getChildren().add(circulo);
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
                sp.setStyle(estilosOriginais.get(sp));
            }
        }
        destaquesMovimento.clear();
    }
}

