package jogodexadrezjavafx;

import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Button btnJogarLocal;
    
    @FXML
    private Button btnJogarContraBot;

    @FXML
    private void iniciarJogoLocal(ActionEvent event) throws IOException {
        iniciarJogo(event, false, null);
    }
    

    @FXML
    private void iniciarJogoContraBot(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Escolha a Dificuldade");
        alert.setHeaderText("Selecione o nível do bot:");
        alert.setContentText("Fácil: Movimentos aleatórios\n" +
                           "Médio: Prioriza capturas e ameaças\n" +
                           "Difícil: Estratégia avançada");
        
        ButtonType btnFacil = new ButtonType("Fácil");
        ButtonType btnMedio = new ButtonType("Médio");
        ButtonType btnDificil = new ButtonType("Difícil");
        ButtonType btnCancelar = new ButtonType("Cancelar");
        
        alert.getButtonTypes().setAll(btnFacil, btnMedio, btnDificil, btnCancelar);
        
        Optional<ButtonType> resultado = alert.showAndWait();
        
        if (resultado.isPresent() && resultado.get() != btnCancelar) {
            Bot.NivelDificuldade nivel;
            
            if (resultado.get() == btnFacil) {
                nivel = Bot.NivelDificuldade.FACIL;
            } else if (resultado.get() == btnMedio) {
                nivel = Bot.NivelDificuldade.MEDIO;
            } else {
                nivel = Bot.NivelDificuldade.DIFICIL;
            }
            
            iniciarJogo(event, true, nivel);
        }
    }
    
    private void iniciarJogo(ActionEvent event, boolean contraBot, Bot.NivelDificuldade nivel) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/tabuleiro.fxml"));
        Parent tabuleiroRoot = loader.load();
        
        Controller controllerDoTabuleiro = loader.getController();
        
        if (contraBot && nivel != null) {
            controllerDoTabuleiro.ativarJogoContraBot(nivel);
        }

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