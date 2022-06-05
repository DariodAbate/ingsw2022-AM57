package it.polimi.ingsw.network.client.view.Controllers;

import it.polimi.ingsw.model.CardBack;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.messages.ChooseCardBack;
import it.polimi.ingsw.network.client.messages.ChooseTowerColor;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;


public class ChoiceController implements GUIController {

    private GUI gui;

    @FXML
    private ImageView druid;

    @FXML
    private ImageView sage;

    @FXML
    private ImageView king;

    @FXML
    private ImageView witch;

    @FXML
    Label header;



    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void witchChosen() throws SocketException {
        System.out.println("witch chosen");
        gui.getSocketClient().send(new ChooseCardBack(CardBack.WITCH));
    }

    public void sageChosen() throws SocketException {
        System.out.println("sage chosen");
        gui.getSocketClient().send(new ChooseCardBack(CardBack.SAGE));
    }

    public void druidChosen() throws SocketException {
        System.out.println("druid chosen");
        gui.getSocketClient().send(new ChooseCardBack(CardBack.DRUID));
    }

    public void kingChosen() throws SocketException {
        System.out.println("king chosen");
        gui.getSocketClient().send(new ChooseCardBack(CardBack.KING));
    }

    public void setVisibility() {
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

    public void requestTowerColor(ArrayList<Tower> selectableTowers) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Towers' color");
        alert.setHeaderText("Choose your towers' color!");
        alert.setContentText("Choose one of the color below!");
        HashMap<String, ButtonType> buttons = new HashMap<>();
        selectableTowers.forEach(n -> buttons.put(n.toString(), new ButtonType(n.toString())));
        alert.getButtonTypes().setAll(buttons.values());
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(
                buttonType ->
                {
                    try {
                        gui.getSocketClient()
                                .send(new ChooseTowerColor(Enum.valueOf(Tower.class, buttonType.getText())));
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                });
    }
}

