package jogodexadrezjavafx;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Button btnJogarLocal;

    @FXML
    private void iniciarJogoLocal(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/tabuleiro.fxml"));
        Parent tabuleiroRoot = loader.load();
        
        Controller controllerDoTabuleiro = loader.getController();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double telaGrandeLargura = 1400; 

        if (screenBounds.getWidth() > telaGrandeLargura) {
            if (controllerDoTabuleiro != null) {
                controllerDoTabuleiro.aplicarEscalaParaTelaGrande();
            }
        }
        
        Scene tabuleiroScene = new Scene(tabuleiroRoot);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(tabuleiroScene);

        window.setResizable(true); 

        window.setMaximized(true);
        
        window.show();
    }
}

