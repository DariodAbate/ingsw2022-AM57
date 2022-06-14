package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.messages.MoveStudentMessage;
import it.polimi.ingsw.network.client.messages.PlayExpertCard;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import java.net.SocketException;
import java.util.Optional;

public class GenericController implements GUIController{

    private GUI gui;

    @FXML
    Label myLabel;


    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void setMyLabel(String message) {
        gui.changeStage("genericScene.fxml");
        myLabel.setText(message);
    }

    public void priorityCardInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Planning phase!");
        alert.setHeaderText("Choose the card you want to play.");
        alert.setContentText("Please select the card you want to play by clicking on it.");
        alert.showAndWait();
    }

    public void chooseMovementInfo(boolean isExpert) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Action phase!");
        alert.setHeaderText("Choose where you want to move your student.");
        if (isExpert) {
            alert.setContentText("Please select hall or island from the button below. Click 'Play card' to play an expert card");
        } else {
            alert.setContentText("Please select hall or island from the button below.");
        }

        ButtonType hall = new ButtonType("Hall");
        ButtonType island = new ButtonType("Island");
        if (isExpert) {
            ButtonType playCard = new ButtonType("Play card");
            alert.getButtonTypes().setAll(hall, island, playCard);
            Optional<ButtonType> result = alert.showAndWait();
            String choice = null;
            if (result.isPresent() && result.get() == hall) {
                choice = "hall";
            } else if (result.isPresent() && result.get() == island) {
                choice = "island";
            } else if (result.isPresent() && result.get() == playCard) {
                try {
                    gui.getSocketClient().send(new PlayExpertCard());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            if (choice != null) {
                try {
                    gui.getSocketClient().send(new MoveStudentMessage(choice));
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        } else {
            alert.getButtonTypes().setAll(hall, island);
            Optional<ButtonType> result = alert.showAndWait();
            String choice = null;
            if (result.isPresent() && result.get() == hall) {
                choice = "hall";
            } else if (result.isPresent() && result.get() == island) {
                choice = "island";
            }
            try {
                gui.getSocketClient().send(new MoveStudentMessage(choice));
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

    }

    public void colorStudentInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Color to move!");
        alert.setHeaderText("Choose the student you want to move.");
        alert.setContentText("Click on the student in your entrance that you want to move.");
        alert.showAndWait();
        gui.displayAllGame();
    }

    public void movementInfo(String message) {                   //TODO remove alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Movement!");
        alert.setHeaderText("You have to move mother nature");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void numOfIslandToTravel(String message) throws NumberFormatException{
        TextInputDialog numOfIsland = new TextInputDialog();
        numOfIsland.setTitle("Movement!");
        numOfIsland.setHeaderText(message);
        numOfIsland.setContentText("Number of island:");

        Optional<String> result = numOfIsland.showAndWait();

        result.ifPresent(num -> {
            try {
                gui.getSocketClient().send(new IntegerMessage(Integer.parseInt(num)));
            } catch (SocketException e) {
                e.printStackTrace();
            }
        });
    }

    public void selectCloudInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clouds!");
        alert.setHeaderText(message);
        alert.setContentText("Select the cloud by clicking on it");
        alert.showAndWait();
    }

    public void selectIslandInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clouds!");
        alert.setHeaderText(message);
        alert.setContentText("Select the island by clicking on it");
        alert.showAndWait();
    }

    public void winnerInfo(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WIN!");
        alert.setHeaderText("Winner!");
        alert.setContentText("Congratulation " + winner + " .You just win!");
        alert.showAndWait();
    }

    public void notWinnerInfo(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WIN!");
        alert.setHeaderText("Winner!");
        alert.setContentText( winner + " has won!");
        alert.showAndWait();
        //TODO aggiungere terminazione processo una volta cliccato OK
    }
}
