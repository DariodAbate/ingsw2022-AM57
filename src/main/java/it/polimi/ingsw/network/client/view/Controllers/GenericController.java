package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.client.messages.*;
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

    @FXML private Label myLabel;


    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void setMyLabel(String message) {
        gui.changeStage("genericScene.fxml");
        myLabel.setText(message);
    }


    public void chooseMovementInfo(String message, boolean isExpert) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Action phase!");
        alert.setHeaderText(message);
        if (isExpert) {
            alert.setContentText("Please select hall or island. Click 'Play card' to play an expert card");
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

    public void numOfIslandToTravel(String message, boolean isExpert) throws NumberFormatException{
        if (isExpert) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Play card");
            alert.setHeaderText("Do you want to play an expert card? You can't play a card if you have already played one");
            ButtonType ok = new ButtonType("Ok");
            ButtonType no = new ButtonType("Not now");
            alert.getButtonTypes().setAll(ok, no);
            Optional<ButtonType> resultAlert = alert.showAndWait();
            if (resultAlert.isPresent() && resultAlert.get() == ok) {
                try {
                    gui.getSocketClient().send(new PlayExpertCard());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (resultAlert.isPresent() && resultAlert.get() == no){
                showTextDialog(message);
            }
        } else {
            showTextDialog(message);
    }}

    public void showTextDialog(String message) {
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
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid parameter!");
                alert.setHeaderText("Please insert the right parameters.");
                alert.setContentText("The number of steps shouldn't be a string.");
                alert.showAndWait();
                showTextDialog(message);
            }
        });
    }



    public void selectCloudInfo(String message, boolean isExpert) {
        if (isExpert) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clouds!");
            alert.setHeaderText(message);
            alert.setContentText("Select the cloud by clicking on it or play a card");
            ButtonType ok = new ButtonType("Ok");
            ButtonType play = new ButtonType("Play card");
            alert.getButtonTypes().setAll(ok, play);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ok) {

            } else if (result.isPresent() && result.get() == play) {
                try {
                    gui.getSocketClient().send(new PlayExpertCard());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }


        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Clouds!");
            alert.setHeaderText(message);
            alert.setContentText("Select the cloud by clicking on it");
            alert.showAndWait();
        }
    }


    public void winnerInfo(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WIN!");
        alert.setHeaderText("Winner!");
        alert.setContentText("Congratulation " + winner + " .You just win!");
        alert.showAndWait();
        System.exit(0);
    }

    public void notWinnerInfo(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WIN!");
        alert.setHeaderText("Winner!");
        alert.setContentText( winner + " has won!");
        alert.showAndWait();
        System.exit(0);
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

    public void disconnection(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Disconnection");
        alert.setHeaderText("A player has disconnected");
        alert.setContentText( message);
        alert.showAndWait();
        System.exit(0);
    }

    public void colorsToChoose(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Colors");
        alert.setHeaderText(message);

        ButtonType r = new ButtonType("Red");
        ButtonType y = new ButtonType("Yellow");
        ButtonType b = new ButtonType("Blue");
        ButtonType g = new ButtonType("Green");
        ButtonType p = new ButtonType("Pink");

        alert.getButtonTypes().setAll(r,y, b, g, p);
        Optional<ButtonType> result = alert.showAndWait();
        Color choice = null;
        if (result.isPresent() && result.get() == r) {
            choice = Color.RED;
        } else if (result.isPresent() && result.get() == y) {
            choice = Color.YELLOW;
        }else if (result.isPresent() && result.get() == b) {
            choice = Color.BLUE;
        }else if (result.isPresent() && result.get() == g) {
            choice = Color.GREEN;
        }else if (result.isPresent() && result.get() == p) {
            choice = Color.PINK;
        }
        try {
            gui.getSocketClient().send(new ColorChosen(choice));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
