package jogodexadrezjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // O programa agora inicia com a tela Home.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/resources/Home.fxml"));
        
        // CORREÇÃO: Cria a cena com o tamanho exato do seu FXML
        Scene scene = new Scene(root, 958, 614); 
        
        primaryStage.setTitle("ChessMasterFX");
        primaryStage.setScene(scene);
        
        // Mantém a janela com tamanho fixo, como solicitado
        primaryStage.setResizable(false); 
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

