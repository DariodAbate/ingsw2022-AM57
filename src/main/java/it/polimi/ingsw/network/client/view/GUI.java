package it.polimi.ingsw.network.client.view;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.AnswerHandler;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.client.modelBean.*;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;
import it.polimi.ingsw.network.client.view.Controllers.*;
import it.polimi.ingsw.network.server.answers.*;
import it.polimi.ingsw.network.server.answers.update.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GUI extends Application implements PropertyChangeListener {

    public static final String MAIN_SCENE_FOR2 = "mainScene2Player.fxml";
    public static final String MENU = "mainMenu.fxml";
    public static final String SETUP = "setup.fxml";
    public static final String LOADING = "loading.fxml";
    public static final String GENERIC = "genericScene.fxml";
    public static final String CHOICE = "chooseCardBack.fxml";
    private Stage stage;
    private Scene currentScene;
    private GameBean gameBean; //model view
    private String nickname;
    private CardBack cardBack;
    private SocketClient socketClient;
    private AnswerHandler answerHandler;
    private ArrayList<Color> entranceColors = new ArrayList<>();
    private ArrayList<Color> cloudColors = new ArrayList<>();


    // Map each Scene to an explanatory String
    private final HashMap<String, Scene> sceneMap = new HashMap<>();

    // Map each controller to an explanatory String
    private final HashMap<String, GUIController> controllerMap = new HashMap<>();


    @Override
    public void start(Stage primaryStage) throws IOException {
        setup();
        stage = primaryStage;
        stage.setTitle("Eryantis");
        stage.setScene(currentScene);
        stage.setResizable(false);
        stage.show();
    }

    public void setup() throws IOException {
        ArrayList<String> fxmList = new ArrayList<>(Arrays.asList(MAIN_SCENE_FOR2, MENU, SETUP, LOADING, GENERIC, CHOICE));
        for (String path : fxmList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path));
            sceneMap.put(path, new Scene(loader.load()));
            GUIController controller = loader.getController();
            controller.setGUI(this);
            controllerMap.put(path, controller);
        }
        currentScene = sceneMap.get(MENU);
    }

    public void changeStage(String newStage) {
        currentScene = sceneMap.get(newStage);
        stage.setScene(currentScene);
        stage.show();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "stopSending" -> closeUserInterface();
            case "genericMessage" -> displayGenericMessage((String)evt.getNewValue());
            case "requestNickname" -> reqNickname((String)evt.getNewValue());
            case "requestNumPlayer" -> reqNumOfPlayer((String)evt.getNewValue());
            case "requestExpertMode" -> reqExpertMode((String)evt.getNewValue());
            case "startMessage" -> startGame((String)evt.getNewValue());
            case "towerChoice" -> displaySelectableTower((ArrayList<Tower>) evt.getNewValue());
            case "cardBackChoice" -> displaySelectableCardBack((ArrayList<CardBack>) evt.getNewValue());
            case "nickname" -> this.nickname = (String) evt.getNewValue();
            case "gameState" -> {
                this.gameBean = (GameBean)evt.getNewValue();
                displayAllGame() ;
            }
            case "cardPlayed" -> {
                String nickname = ((AssistantCardPlayedAnswer)evt.getNewValue()).getNickname();
                ArrayList<AssistantCard> hand = ((AssistantCardPlayedAnswer)evt.getNewValue()).getHand();
                AssistantCard playedCard = ((AssistantCardPlayedAnswer)evt.getNewValue()).getCard();

                for(PlayerBean player :gameBean.getPlayers()){
                    if(player.getNickname().equals(nickname)){
                        player.setPlayedCard(playedCard);
                        player.setHand(hand); //new hand
                    }
                }
                displayAllGame() ;
            }
            case "toHall" -> {
                String nickname = ((ToHallUpdateAnswer)evt.getNewValue()).getNickname();
                BoardBean updatedBoard = ((ToHallUpdateAnswer)evt.getNewValue()).getUpdatedBoard();

                for(PlayerBean player :gameBean.getPlayers()){
                    if(player.getNickname().equals(nickname))
                        player.setBoard(updatedBoard);
                }
                displayAllGame() ;
            }
            case "toIsland" -> {
                String nickname = ((ToIslandUpdateAnswer)evt.getNewValue()).getNickname();
                BoardBean updatedBoard = ((ToIslandUpdateAnswer)evt.getNewValue()).getUpdatedBoard();
                ArrayList<IslandBean> updatedArchipelago = ((ToIslandUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();

                for(PlayerBean player :gameBean.getPlayers()){
                    if(player.getNickname().equals(nickname))
                        player.setBoard(updatedBoard);
                }
                gameBean.setArchipelago(updatedArchipelago);
                displayAllGame() ;
            }

            case "motherMovement" -> {
                int motherNature =  ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedMotherNature();
                ArrayList<BoardBean> updatedBoards = ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                ArrayList<IslandBean> updatedArchipelago = ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();

                for(int i = 0; i < gameBean.getPlayers().size(); i++){
                    gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                }
                gameBean.setMotherNature(motherNature);
                gameBean.setArchipelago(updatedArchipelago);
                displayAllGame() ;
            }
            case "cloudChoice" -> {
                ArrayList<BoardBean> updatedBoards = ((CloudsUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                ArrayList<CloudBean> updateClouds = ((CloudsUpdateAnswer)evt.getNewValue()).getUpdateClouds();

                for(int i = 0; i < gameBean.getPlayers().size(); i++){
                    gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                }
                gameBean.setCloudTiles(updateClouds);
                displayAllGame();
            }
            case "expertCard" -> {
                ArrayList<BoardBean> updatedBoards = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                ArrayList<IslandBean> updatedArchipelago = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();
                ArrayList<ExpertCardBean> updatedExpertCards = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedExpertCards();

                gameBean.setExpertCards(updatedExpertCards);
                if(updatedBoards != null){
                    for(int i = 0; i < gameBean.getPlayers().size(); i++){
                        gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                    }
                }
                if(updatedArchipelago != null){
                    gameBean.setArchipelago(updatedArchipelago);
                }
                displayAllGame();
            }
        }
    }

    public void closeUserInterface() {

     }

     public void reqNickname(String message) {
         System.out.println(message + " (request nickname message)");
         Platform.runLater(() -> {
             MainMenuController controller = (MainMenuController) controllerMap.get(MENU);
             nickname = controller.getNickname();
         });
     }

     public void reqNumOfPlayer(String message) {   //TODO gestire invio risposta al server nel controller
         System.out.println(message + " (request numPlayer message)");
         Platform.runLater(() -> {
             LoaderController controller = (LoaderController) controllerMap.get(LOADING);
             controller.requestNumOfPlayer(message);
         });
    }

    public void reqExpertMode(String message){  //TODO gestire invio risposta al server nel controller
        Platform.runLater(() -> {
            LoaderController controller = (LoaderController) controllerMap.get(LOADING);
            controller.requestExpertMode(message);
        });
    }

    public void startGame(String message) {
        System.out.println(message + " (start message)");
        Platform.runLater(() -> {
            ChoiceController controller = (ChoiceController) controllerMap.get(CHOICE);
            controller.setVisibility();
            changeStage(CHOICE);
        });
    }

     public void displayGenericMessage(String message){
        System.out.println(message + " (generic message)");
        if(message.contains("Username not available")) {   //TODO gestire Username not available
            Platform.runLater(() -> {
                changeStage(MENU);
            });
        }

        else if(message.contains("Please select the priority of the card")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.priorityCardInfo();
            });
        }

        else if(message.contains("Select where you want to move your students")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.chooseMovementInfo();
            });
        }

        else if(message.contains(("Please select the color of the student"))) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.colorStudentInfo();
            });
         }

        /*else if(message.contains("Move mother nature. You can travel")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.movementInfo(message);
            });
         }*/

        else if(message.contains("Choose the number of islands you want to travel")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.numOfIslandToTravel();
            });
        }

        else if(message.contains("Select one of the clouds")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.selectCloudInfo(message);
            });
        }

        /*else  {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.setMyLabel(message);
            });
        }*/
         else{}
     }


    public void displaySelectableCardBack(ArrayList<CardBack> selectableCardBack) {
        Platform.runLater(() -> {
            ChoiceController controller = (ChoiceController) controllerMap.get(CHOICE);
            controller.setHeader("Choose your favourite card back");
            for (CardBack cardBack : selectableCardBack) {
                controller.showCard(cardBack);
            }
        });
    }

    public void displaySelectableTower(ArrayList<Tower> selectableTowers) {
        Platform.runLater(() -> {
            ChoiceController controller = (ChoiceController) controllerMap.get(CHOICE);
            controller.requestTowerColor(selectableTowers);
            changeStage(MAIN_SCENE_FOR2);
        });
    }

    public void displayAllGame() {
        System.out.println("ALL GAME");
        Platform.runLater(() -> {
            MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
            controller.updateOtherLastPlayedCard();
            for (int i = 0; i < gameBean.getPlayers().size(); i++) {
                if (gameBean.getPlayers().get(i).getNickname().equals(nickname)) {
                    displayBoard(gameBean.getPlayers().get(i).getBoard(), gameBean.isExpertGame());
                }
            }
        });
        displayClouds();
    }

    public void displayBoard(BoardBean board, boolean expertGame) {
        MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
        entranceColors.removeAll(entranceColors);
        for (Color color : Color.values()) {
            for (int j = 0; j < board.getEntranceStudent().get(color); j++) {
                entranceColors.add(color);
                System.out.println(color);
            }
        }
        Platform.runLater(() -> {
            controller.showEntranceStudents(entranceColors);
            controller.showTower(board.getTowerColor());
        });

    }

    public AssistantCard getOtherPlayersLastCard() {
        AssistantCard lastCard = null;
        if (gameBean.getPlayers().size() == 2) {
            for (int i = 0; i < 2; i ++) {
                if(!gameBean.getPlayers().get(i).getNickname().equals(nickname)){
                    lastCard = gameBean.getPlayers().get(i).getPlayedCard();
                }
            }
        }
        return lastCard;
    }


    public void displayCard(PlayerBean playerBean) {

    }

    public void displayArchipelago() {

    }

    public void displayExpertCard() {

    }

    public void displayClouds() {
        MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
        for(CloudBean cloudBean : gameBean.getCloudTiles()) {
            for (Color color : Color.values()) {
                for(int j = 0; j < cloudBean.getStudents().get(color); j ++) {
                    cloudColors.add(color);
                }
            }
            Platform.runLater(() -> {
                controller.updateClouds(cloudColors, gameBean.getCloudTiles().indexOf(cloudBean));
            });
            System.out.println(gameBean.getCloudTiles().indexOf(cloudBean));
            System.out.println(cloudColors);
            cloudColors.removeAll(cloudColors);
        }
    }

    public void startConnection(AnswerHandler answerHandler, SocketClient socketClient) throws IOException {
        this.answerHandler = answerHandler;
        this.socketClient = socketClient;
        this.answerHandler.addPropertyChangeListener(this);
        this.socketClient.startListening();
        this.socketClient.startPinging();
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public GameBean getGameBean() {
        return gameBean;
    }

}


