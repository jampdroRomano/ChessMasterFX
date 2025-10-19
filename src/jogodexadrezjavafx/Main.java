package jogodexadrezjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o FXML usando um FXMLLoader para termos acesso ao controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/tabuleiro.fxml"));
        Parent root = loader.load();
        
        // Pega a instância do controller que foi criada
        Controller controller = loader.getController();

        // Pega as dimensões da tela principal do usuário
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Define um "ponto de corte". Se a largura da tela for maior que 1400px, consideramos uma tela grande.
        double telaGrandeLargura = 1400; 

        // Se for uma tela grande, chama o método no controller para aplicar a escala
        if (screenBounds.getWidth() > telaGrandeLargura) {
            controller.aplicarEscalaParaTelaGrande();
        }

        Scene scene = new Scene(root);
        primaryStage.setTitle("Jogo de Xadrez");
        primaryStage.setScene(scene);
        
        // Define um tamanho mínimo para a janela, baseado no tabuleiro pequeno
        primaryStage.setMinWidth(650 + 50); // Largura do tabuleiro + margem
        primaryStage.setMinHeight(650 + 50); // Altura do tabuleiro + margem
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

