package jogodexadrezjavafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

    private Tabuleiro tabuleiro;
    private Cor turnoAtual;
    private Peca pecaSelecionada;
    private Casa casaOrigem;
    private List<Casa> movimentosPossiveis = new ArrayList<>();
    private boolean jogoAcabou = false;

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
                
                mapaCasaParaStackPane.put(casa, sp);
                mapaStackPaneParaCasa.put(sp, casa);
                
                estilosOriginais.put(sp, sp.getStyle());
                
                sp.setOnMouseClicked(event -> onCasaClicked(sp));
            }
        }
    }
    
    private void onCasaClicked(StackPane spClicado) {
        if (jogoAcabou) return;

        Casa casaClicada = mapaStackPaneParaCasa.get(spClicado);

        if (pecaSelecionada != null) {
            if (movimentosPossiveis.contains(casaClicada)) {
                moverPeca(casaOrigem, casaClicada);
                verificarFimDeJogoAposMovimento();
                trocarTurno();
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

        if (pecaMovida instanceof Rei && Math.abs(destino.getColuna() - origem.getColuna()) == 2) {
            tabuleiro.moverPeca(origem, destino);
            spDestino.getChildren().removeIf(node -> node instanceof ImageView);
            spDestino.getChildren().add(pecaVisual);
            
            Casa casaOrigemTorre = tabuleiro.moverTorreRoque(destino);
            StackPane spOrigemTorre = mapaCasaParaStackPane.get(casaOrigemTorre);
            int colunaDestinoTorre = (destino.getColuna() == 6) ? 5 : 3;
            StackPane spDestinoTorre = mapaCasaParaStackPane.get(tabuleiro.getCasa(origem.getLinha(), colunaDestinoTorre));
            ImageView torreVisual = (ImageView) spOrigemTorre.getChildren().stream()
                .filter(node -> node instanceof ImageView).findFirst().orElse(null);
            
            spDestinoTorre.getChildren().add(torreVisual);

        } else {
            if(destino.temPeca()){
                System.out.println("Peça " + destino.getPeca().getClass().getSimpleName() + " capturada!");
            }
            tabuleiro.moverPeca(origem, destino);
            spDestino.getChildren().removeIf(node -> node instanceof ImageView);
            spDestino.getChildren().add(pecaVisual);
        }
    }
    
    private void verificarFimDeJogoAposMovimento() {
        Cor corOponente = (turnoAtual == Cor.BRANCA) ? Cor.PRETA : Cor.BRANCA;
        tabuleiro.verificarChequeAposMovimento(corOponente);

        if (tabuleiro.getContadorChecksBrancas() >= 3) {
            System.out.println("FIM DE JOGO! As PRETAS venceram por 3 xeques!");
            jogoAcabou = true;
        } else if (tabuleiro.getContadorChecksPretas() >= 3) {
            System.out.println("FIM DE JOGO! As BRANCAS venceram por 3 xeques!");
            jogoAcabou = true;
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
            System.out.println("--- Turno das " + turnoAtual.toString() + "S ---");
        }
    }

    private void mostrarDestaques() {
        limparDestaques();
        
        StackPane spOrigem = mapaCasaParaStackPane.get(casaOrigem);
        String estiloOriginal = estilosOriginais.get(spOrigem);
        spOrigem.setStyle(estiloOriginal + "; -fx-border-color: yellow; -fx-border-width: 3;");
        destaquesMovimento.add(spOrigem);
        
        for (Casa casaDestino : movimentosPossiveis) {
            StackPane spDestino = mapaCasaParaStackPane.get(casaDestino);
            Circle circulo = new Circle(15, Color.rgb(128, 128, 128, 0.5));
            destaquesMovimento.add(circulo);
            spDestino.getChildren().add(circulo);
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


