package it.polimi.ingsw.network.client.view.Controllers;

import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.SocketException;
import java.util.Optional;


public class LoaderController implements GUIController {

    private GUI gui;


    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void requestNumOfPlayer(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lobby capacity");
        alert.setHeaderText("Choose the number of players.");
        alert.setContentText(message);

        ButtonType two = new ButtonType("2");
        ButtonType three = new ButtonType("3");

        alert.getButtonTypes().setAll(two, three);
        Optional<ButtonType> result = alert.showAndWait();
        int players = 0;
        if (result.isPresent() && result.get() == two) {
            players = 2;
        } else if (result.isPresent() && result.get() == three) {
            players = 3;
        }
        try {
            gui.getSocketClient().send(new IntegerMessage(players));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void requestExpertMode(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mode");
        alert.setHeaderText("Choose the mode to play.");
        alert.setContentText(message);

        ButtonType y = new ButtonType("Yes");
        ButtonType n = new ButtonType("No");

        alert.getButtonTypes().setAll(y, n);
        Optional<ButtonType> result = alert.showAndWait();
        String choice = null;
        if (result.isPresent() && result.get() == y) {
            choice = "y";
        } else if (result.isPresent() && result.get() == n) {
            choice = "n";
        }
        try {
            gui.getSocketClient().send(new GenericMessage(choice));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}
