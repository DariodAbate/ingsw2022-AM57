package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.network.client.AnswerHandler;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
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

    public void startServer() throws IOException, NumberFormatException {
        AnswerHandler answerHandler = new AnswerHandler();
        SocketClient socketClient = new SocketClient(serverAddress.getText(), Integer.parseInt(serverPort.getText()), answerHandler);
        gui.startConnection(answerHandler, socketClient);
        nickname = username.getText();
    }

    public String getNickname() {
        try {
            gui.getSocketClient().send(new GenericMessage(nickname));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return nickname;
    }

}
