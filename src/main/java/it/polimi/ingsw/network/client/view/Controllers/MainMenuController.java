package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.network.client.AnswerHandler;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.SocketException;


public class MainMenuController implements GUIController {

    private GUI gui;

    @FXML
    private TextField username;

    @FXML
    private TextField serverAddress;

    @FXML
    private TextField serverPort;

    private static String nickname;


    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void play() {
        gui.changeStage("setup.fxml");
    }

    public void quit() {
        System.out.println("Quitting the game. See you soon!!!");
        System.exit(0);
    }

    public void startServer() {
        try {
            AnswerHandler answerHandler = new AnswerHandler();
            SocketClient socketClient = new SocketClient(serverAddress.getText(), Integer.parseInt(serverPort.getText()), answerHandler);
            gui.startConnection(answerHandler, socketClient);
            nickname = username.getText();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid parameter!");
            alert.setHeaderText("Please insert the right parameters.");
            alert.setContentText("The address port should contain only number.");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid parameter!");
            alert.setHeaderText("Server not available.");
            alert.setContentText("The given address isn't correct.");
            alert.showAndWait();
        }

    }

    public String getNickname() {
        try {
            gui.getSocketClient().send(new GenericMessage(nickname));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    public void userNameNotAvailable (String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Username not available!");
        alert.setHeaderText(message);
        alert.setContentText("This username is already taken.");
        alert.showAndWait();
    }

}
