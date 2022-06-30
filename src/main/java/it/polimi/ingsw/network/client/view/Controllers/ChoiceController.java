package it.polimi.ingsw.network.client.view.Controllers;

import it.polimi.ingsw.model.CardBack;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.messages.ChooseCardBack;
import it.polimi.ingsw.network.client.messages.ChooseTowerColor;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import java.net.SocketException;

/**
 * Main controller for the choice scene
 * @author Luca Bresciani
 */
public class ChoiceController implements GUIController {

    private GUI gui;

    @FXML private ImageView druid;
    @FXML private ImageView sage;
    @FXML private ImageView king;
    @FXML private ImageView witch;
    @FXML private Label header;
    @FXML private ImageView blackT;
    @FXML private ImageView grayT;
    @FXML private ImageView whiteT;

    /**
     * This method set the gui objet in the controller
     * @param gui is the gui reference
     */
    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void witchChosen() throws SocketException {
        gui.getSocketClient().send(new ChooseCardBack(CardBack.WITCH));
    }

    public void sageChosen() throws SocketException {
        gui.getSocketClient().send(new ChooseCardBack(CardBack.SAGE));
    }

    public void druidChosen() throws SocketException {
        gui.getSocketClient().send(new ChooseCardBack(CardBack.DRUID));
    }

    public void kingChosen() throws SocketException {
        gui.getSocketClient().send(new ChooseCardBack(CardBack.KING));
    }

    public void setCardVisibility() {
        druid.setVisible(false);
        king.setVisible(false);
        sage.setVisible(false);
        witch.setVisible(false);
    }

    public void showCard(CardBack cardback) {
        switch (cardback) {
            case DRUID -> druid.setVisible(true);
            case KING -> king.setVisible(true);
            case SAGE -> sage.setVisible(true);
            case WITCH -> witch.setVisible(true);
        }
    }

    public void setHeader(String message) {
        header.setText(message);
    }

    public void sendBlack() {
        try {
            gui.getSocketClient()
                    .send(new ChooseTowerColor(Tower.BLACK));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            GenericController controller = (GenericController) gui.getControllerMap().get(GUI.GENERIC);
            gui.changeStage(GUI.GENERIC);
            controller.setMyLabel("The other players are making their choice.");
        });
    }

    public void sendGray() {
        try {
            gui.getSocketClient()
                    .send(new ChooseTowerColor(Tower.GRAY));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            GenericController controller = (GenericController) gui.getControllerMap().get(GUI.GENERIC);
            gui.changeStage(GUI.GENERIC);
            controller.setMyLabel("The other players are making their choice.");
        });
    }

    public void sendWhite() {
        try {
            gui.getSocketClient()
                    .send(new ChooseTowerColor(Tower.WHITE));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            GenericController controller = (GenericController) gui.getControllerMap().get(GUI.GENERIC);
            gui.changeStage(GUI.GENERIC);
            controller.setMyLabel("The other players are making their choice.");
        });
    }

    public void setTowerVisibility() {
        blackT.setVisible(false);
        whiteT.setVisible(false);
        grayT.setVisible(false);
    }

    public void showTower(Tower tower) {
        switch (tower) {
            case BLACK -> blackT.setVisible(true);
            case GRAY -> grayT.setVisible(true);
            case WHITE -> whiteT.setVisible(true);
        }
    }
}

