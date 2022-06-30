package it.polimi.ingsw.network.client.view;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.AnswerHandler;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.client.modelBean.*;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.StudBufferExpertCardBean;
import it.polimi.ingsw.network.client.view.Controllers.*;
import it.polimi.ingsw.network.server.answers.*;
import it.polimi.ingsw.network.server.answers.update.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
Main class for the graphic user interface.

@author Luca Bresciani
 */

public class GUI extends Application implements PropertyChangeListener{

    public static final String MAIN_SCENE_FOR2 = "mainScene2Player.fxml";
    public static final String MAIN_SCENE_FOR3 = "mainScene3Player.fxml";
    public static final String MENU = "mainMenu.fxml";
    public static final String SETUP = "setup.fxml";
    public static final String GENERIC = "genericScene.fxml";
    public static final String CHOOSE_CARD = "chooseCardBack.fxml";
    public static final String CHOOSE_TOWER = "chooseTower.fxml";
    public static final String NICK = "requestUser.fxml";
    private Stage stage;
    private Scene currentScene;
    private GameBean gameBean;
    private String nickname;
    private SocketClient socketClient;
    private AnswerHandler answerHandler;
    private ArrayList<Integer> priorities = new ArrayList<>();
    private ArrayList<Color> cloudColors = new ArrayList<>();

    // Map each Scene to an explanatory String
    private final HashMap<String, Scene> sceneMap = new HashMap<>();

    // Map each controller to an explanatory String
    private final HashMap<String, GUIController> controllerMap = new HashMap<>();

    /**
     * start method start the actual application
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        setup();
        stage = primaryStage;
        stage.setTitle("Eryantis");
        stage.getIcons().add(new Image(getClass().getResource("/eriantys_banner_no_items.png").toExternalForm()));
        stage.setScene(currentScene);
        stage.setResizable(false);
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    /**
     * setup method fill the controllerMap that associate each controller to his scene
     * @throws IOException
     */
    public void setup() throws IOException {
        ArrayList<String> fxmList = new ArrayList<>(Arrays.asList(MAIN_SCENE_FOR2, MAIN_SCENE_FOR3, MENU, SETUP, GENERIC, CHOOSE_CARD, CHOOSE_TOWER, NICK));
        for (String path : fxmList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path));
            sceneMap.put(path, new Scene(loader.load()));
            GUIController controller = loader.getController();
            controller.setGUI(this);
            controllerMap.put(path, controller);
        }
        currentScene = sceneMap.get(MENU);
    }

    /**
     * method changeStage change the current scene with the new scene
     * @param newStage is the new scene
     */
    public void changeStage(String newStage) {
        currentScene = sceneMap.get(newStage);
        stage.setScene(currentScene);
        stage.show();
    }

    /**
     * This method will show content based on the event that the client receives, thus it updates the view
     * @param evt event occurred due to server
     */
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            //case "stopSending" -> closeUserInterface();
            case "genericMessage" -> displayGenericMessage((String)evt.getNewValue());
            //case "requestNickname" -> reqNickname((String)evt.getNewValue());
            case "requestNumPlayer" -> reqNumOfPlayer((String)evt.getNewValue());
            case "requestExpertMode" -> reqExpertMode((String)evt.getNewValue());
            case "startMessage" -> startGame((String)evt.getNewValue());
            case "towerChoice" -> displaySelectableTower((ArrayList<Tower>) evt.getNewValue());
            case "cardBackChoice" -> displaySelectableCardBack((ArrayList<CardBack>) evt.getNewValue());
            case "win" -> displayWinner((String)evt.getNewValue());
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
                ArrayList<BoardBean> updatedBoardList = ((ToHallUpdateAnswer)evt.getNewValue()).getUpdatedBoardList();


                for(int i = 0; i < gameBean.getPlayers().size(); i++){
                    PlayerBean player = gameBean.getPlayers().get(i);
                    player.setBoard(updatedBoardList.get(i));
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

    /**
     * this method call the controller's method that display the winner
     * @param winner winner of the game
     */
    public void displayWinner(String winner) {
        GenericController controller = (GenericController) controllerMap.get(GENERIC);
        if(winner.equals(nickname)){
            Platform.runLater(() -> {
                controller.winnerInfo(winner);
            });
        }else{
            Platform.runLater(() -> {
                controller.notWinnerInfo(winner);
            });
        }

    }

    /**
     * this method call the controller's method that request the number of player
     * @param message message received from the server
     */
     public void reqNumOfPlayer(String message) {
         Platform.runLater(() -> {
             GenericController controller = (GenericController) controllerMap.get(GENERIC);
             controller.requestNumOfPlayer(message);
         });
    }

    /**
     * This method call the controller's method that request if the game should be in expert mode or not
     * @param message message received from the server
     */
    public void reqExpertMode(String message){
        Platform.runLater(() -> {
            GenericController controller = (GenericController) controllerMap.get(GENERIC);
            controller.requestExpertMode(message);
        });
    }

    /**
     * This method change the scene when the game start
     * @param message message received from the server
     */
    public void startGame(String message) {
        GenericController controller = (GenericController) controllerMap.get(GENERIC);
        Platform.runLater(() -> {
            changeStage(GENERIC);
            controller.setMyLabel(message);
        });
    }

    /**
     * This method is used to show a generic message received from the server
     * @param message message received from the server
     */
     public void displayGenericMessage(String message){
        if(message.contains("Username not available")) {
            MainMenuController controller = (MainMenuController) controllerMap.get(MENU);
                Platform.runLater(() -> {
                    controller.userNameNotAvailable(message);
                    changeStage(NICK);
                });
                this.nickname = controller.getNickname();
            }

        else if(message.contains("Select where you want to move your students")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.chooseMovementInfo(message, gameBean.isExpertGame());
                showInfoMessage(message);
            });
        }

        else if(message.contains("Choose the number of islands you want to travel")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.numOfIslandToTravel(message, gameBean.isExpertGame());
                showInfoMessage(message);
            });
        }

        else if(message.contains("Select one of the clouds")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.selectCloudInfo(message, gameBean.isExpertGame());
                showInfoMessage(message);
            });
        }

       else if(message.contains("Please login another time on the server to play")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.disconnection(message);
            });
        }

        else if(message.contains("Wait for")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.setMyLabel(message);
                showInfoMessage(message);
            });
        }

        else if(message.contains("Please reconnect to restart this game")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.disconnection(message);
            });
        }

        else if(message.contains("Please select the color to put in the bag")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.colorsToChoose(message);
            });
        }

        else if(message.contains("Please select the color to ignore for the influence calculation")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.colorsToChoose(message);
            });
        }

        else  {
         showInfoMessage(message);
        }
    }


    /**
     * This method is used to show the messages from the server in the user interface
     * @param message message received from the server
     */
    public void showInfoMessage (String message) {
        Platform.runLater(() -> {
            MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
            controller.showInfoMessage(message);
        });
        Platform.runLater(() -> {
            MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
            controller.showInfoMessage(message);
        });
    }

    /**
     * This method is used to show the Card Back during the choosing phase
     * @param selectableCardBack remaining selectable card back
     */
    public void displaySelectableCardBack(ArrayList<CardBack> selectableCardBack) {
        Platform.runLater(() -> {
            ChoiceController controller = (ChoiceController) controllerMap.get(CHOOSE_CARD);
            changeStage(CHOOSE_CARD);
            controller.setHeader("Choose your favourite card back");
            controller.setCardVisibility();
            for (CardBack cardBack : selectableCardBack) {
                controller.showCard(cardBack);
            }
        });
    }

    /**
     * This method is used to show the Towers during the choosing phase
     * @param selectableTowers remaining selectable tower
     */
    public void displaySelectableTower(ArrayList<Tower> selectableTowers) {
        Platform.runLater(() -> {
            ChoiceController controller = (ChoiceController) controllerMap.get(CHOOSE_TOWER);
            changeStage(CHOOSE_TOWER);
            controller.setTowerVisibility();
            for (Tower tower : selectableTowers) {
                controller.showTower(tower);
            }
        });
    }

    /**
     * This method is used to show the entire game view
     */
    public void displayAllGame() {
        ArrayList<Integer> otherPlayersIndex = new ArrayList<>();
        Platform.runLater(() -> {
            if (gameBean.getPlayers().size() == 2) {
                changeStage(MAIN_SCENE_FOR2);
            } else if (gameBean.getPlayers().size() == 3) {
                changeStage(MAIN_SCENE_FOR3);
            }
            for (int i = 0; i < gameBean.getPlayers().size(); i++) {
                displayBoard(gameBean.getPlayers().get(i).getBoard(), gameBean.isExpertGame(), gameBean.getPlayers().get(i).getNickname().equals(nickname), gameBean.getPlayers().indexOf(gameBean.getPlayers().get(i)));
                if (gameBean.getPlayers().get(i).getNickname().equals(nickname)) {
                    displayCard(gameBean.getPlayers().get(i));
                }
                if(!gameBean.getPlayers().get(i).getNickname().equals(nickname)) {
                    otherPlayersIndex.add(gameBean.getPlayers().indexOf(gameBean.getPlayers().get(i)));
                    MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
                    controller.setOtherPlayersIndex(otherPlayersIndex);
                }
            }
            displayClouds();
            displayArchipelago();
            if (gameBean.isExpertGame()) {
                displayExpertCard();
            }
        });
    }

    /**
     * This method is used to show the players' board
     * @param board board to be showed
     * @param expertGame indicates if the game is expert mode or not
     * @param isMyBoard indicates if the board is of the gui's player or not
     * @param playerIndex is the index of the players in the list that contains all of them
     */
    public void displayBoard(BoardBean board, boolean expertGame, boolean isMyBoard, int playerIndex) {
        ArrayList<Color> entranceColors = new ArrayList<>();
        ArrayList<Color> professorsColors = new ArrayList<>();
        ArrayList<Color> hallColors = new ArrayList<>();
        entranceColors.removeAll(entranceColors);
        hallColors.removeAll(hallColors);
        professorsColors.removeAll(professorsColors);
        for (Color color : Color.values()) {
            for (int i = 0; i < board.getEntranceStudent().get(color); i++) {
                entranceColors.add(color);
            }
        }
        for (Color color : Color.values()) {
            for (int i = 0; i < board.getHallStudent().get(color); i++) {
                hallColors.add(color);
            }
        }
        professorsColors.addAll(board.getProfessors());

        if (gameBean.getPlayers().size() == 2) {
            MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
            if(isMyBoard) {
                Platform.runLater(() -> {
                    if (expertGame) {
                        controller.showMyCoin(board.getNumCoins());
                    }
                    controller.showEntranceStudents(entranceColors);
                    controller.showHallStudents(hallColors);
                    controller.showMyTower(board.getTowerColor(), board.getNumTowers());
                    controller.showMyProfessors(professorsColors);
                });
            } else {
                Platform.runLater(() -> {
                    if (expertGame) {
                        controller.showOtherCoin(board.getNumCoins());
                    }
                    controller.showOtherPlayerEntrance(entranceColors);
                    controller.showOtherPlayerProfessors(professorsColors);
                    controller.showOtherTower(board.getTowerColor(), board.getNumTowers());
                    controller.showOtherPLayerHall(hallColors);
                });
            }
        } else if (gameBean.getPlayers().size() == 3) {
            MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
            if (isMyBoard) {
                Platform.runLater(() -> {
                    if (expertGame) {
                        controller.showMyCoin(board.getNumCoins());
                    }
                    controller.showEntranceStudents(entranceColors);
                    controller.showHallStudents(hallColors);
                    controller.showMyTower(board.getTowerColor(), board.getNumTowers());
                    controller.showMyProfessors(professorsColors);
                });
            } else {
                Platform.runLater(() -> {
                    if (expertGame) {
                        controller.showOtherCoin(board.getNumCoins(), playerIndex);
                    }
                    controller.showOtherPlayerEntrance(entranceColors, playerIndex);
                    controller.showOtherPlayersHall(hallColors, playerIndex);
                    controller.showOtherPlayersProfessors(professorsColors, playerIndex);
                    controller.showOtherPlayesrsTowers(board.getTowerColor(), board.getNumTowers(), playerIndex);
                });
            }
        }
    }

    /**
     * This method returns the player's associated with the gui last played card
     * @return the priority of my last played card
     */
    public int myLastPlayedCard() {
        int lastPriority = 0;
        for (int i = 0; i < gameBean.getPlayers().size(); i++) {
            if (gameBean.getPlayers().get(i).getNickname().equals(nickname)) {
                if(gameBean.getPlayers().get(i).getPlayedCard() != null) {
                    lastPriority = gameBean.getPlayers().get(i).getPlayedCard().getPriority();
                    return lastPriority;
                }
            }
        }
        return 0;
    }

    /**
     * This method returns the others players last played card
     * @return the priority of the others players card
     */
    public int otherLastPlayedCard() {
        int lastPriority;
        for (int i = 0; i < gameBean.getPlayers().size(); i++) {
            if (!gameBean.getPlayers().get(i).getNickname().equals(nickname)) {
                if(gameBean.getPlayers().get(i).getPlayedCard() != null) {
                    lastPriority = gameBean.getPlayers().get(i).getPlayedCard().getPriority();
                    return lastPriority;
                }
            }
        }
        return 0;
    }


    /**
     * This method is used to display the assistant card
     * @param playerBean is the player bean that contains the card information
     */
    public void displayCard(PlayerBean playerBean) {
        priorities.removeAll(priorities);
        if (gameBean.getPlayers().size() == 2) {
            MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
            for (AssistantCard assistantCard : playerBean.getHand()) {
                priorities.add(assistantCard.getPriority());
                Platform.runLater(() -> {
                    controller.showAssistantCards(priorities);
                });
            }
            Platform.runLater(() -> {
                controller.showLastPlayedCard(myLastPlayedCard());
                controller.showOtherLastPlayedCard(otherLastPlayedCard());
            });
        } else if (gameBean.getPlayers().size() == 3) {
            MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
            for (AssistantCard assistantCard : playerBean.getHand()) {
                priorities.add(assistantCard.getPriority());
                Platform.runLater(() -> {
                    controller.showAssistantCards(priorities);
                });
            }
            Platform.runLater(() -> {
                controller.showLastPlayedCard(myLastPlayedCard());
                int otherLastPriority = 0;
                for (int i = 0; i < gameBean.getPlayers().size(); i++) {
                    if (!gameBean.getPlayers().get(i).getNickname().equals(nickname)) {
                        if(gameBean.getPlayers().get(i).getPlayedCard() != null) {
                            otherLastPriority = gameBean.getPlayers().get(i).getPlayedCard().getPriority();
                            controller.showOtherLastPlayedCard(otherLastPriority, gameBean.getPlayers().indexOf(gameBean.getPlayers().get(i)));
                        }
                    }

                }

            });
        }
    }


    /**
     * This method is used to display the archipelago
     */
    public void displayArchipelago() {
        HashMap<Integer, ArrayList<Color>> islandColorsMap = new HashMap<>();
        HashMap<Integer, Tower> towerColorMap = new HashMap<>();
        HashMap<Integer, Integer> numTowersMap = new HashMap<>();
        HashMap<Integer, Boolean> bannedIslands = new HashMap<>();
        ArrayList<Color> islandStudents = new ArrayList<>();
        Tower towerColor;
        int numTowers;
        for (IslandBean island : gameBean.getArchipelago()){
            islandStudents.removeAll(islandStudents);
            towerColor = island.getTowerColor();
            numTowers = island.getNumTowers();
            for (Color color : Color.values()) {
                for (int i = 0; i < island.getStudents().get(color); i ++){
                    islandStudents.add(color);
                }
            }
            ArrayList<Color> copy = new ArrayList<>(islandStudents);
            islandColorsMap.put(gameBean.getArchipelago().indexOf(island), copy);
            towerColorMap.put(gameBean.getArchipelago().indexOf(island), towerColor);
            numTowersMap.put(gameBean.getArchipelago().indexOf(island), numTowers);
            bannedIslands.put(gameBean.getArchipelago().indexOf(island), island.isBanToken());
        }
        Platform.runLater(() -> {
            if (gameBean.getPlayers().size() == 2) {
                MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
                controller.showArchipelago(gameBean.getArchipelago().size(), gameBean.getMotherNature(), islandColorsMap, towerColorMap, numTowersMap, bannedIslands );
            } else if (gameBean.getPlayers().size() == 3){
                MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
                controller.showArchipelago(gameBean.getArchipelago().size(), gameBean.getMotherNature(), islandColorsMap, towerColorMap, numTowersMap, bannedIslands);
            }
        });
    }

    /**
     * This method is used to display the expert card
     */
    public void displayExpertCard() {
        ArrayList<ExpertCard_ID> expertCards = new ArrayList<>();
        ArrayList<Color> cardColors = new ArrayList<>();
        HashMap<Integer, ArrayList<Color>> studBufferColor = new HashMap<>();
        ArrayList<Boolean> usedCard = new ArrayList<>();

        expertCards.removeAll(expertCards);
        for (ExpertCardBean expertCardBean : gameBean.getExpertCards()){
            expertCards.add(expertCardBean.getName());
            usedCard.add(expertCardBean.isPlayed());
            if(expertCardBean instanceof StudBufferExpertCardBean){
                cardColors.removeAll(cardColors);
                for (Color color : Color.values()){
                    for (int j = 0; j < ((StudBufferExpertCardBean) expertCardBean).getStudentBuffer().get(color); j++) {
                        cardColors.add(color);
                    }
                }
                ArrayList<Color> copy = new ArrayList<>(cardColors);
                studBufferColor.put(gameBean.getExpertCards().indexOf(expertCardBean), copy);
            }
        }
        if (gameBean.getPlayers().size() == 2) {
            MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
            Platform.runLater(() -> {
                controller.showExpertCard(expertCards, studBufferColor, usedCard);
            });
        } else if (gameBean.getPlayers().size() == 3) {
            MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
            Platform.runLater(() -> {
                controller.showExpertCard(expertCards, studBufferColor, usedCard);
            });
        }
    }

    /**
     * This method is used to display the clouds
     */
    public void displayClouds() {
        HashMap<Integer, ArrayList<Color>> cloudColorsMap = new HashMap<>();
        cloudColors.removeAll(cloudColors);
        for(CloudBean cloudBean : gameBean.getCloudTiles()) {
            cloudColors.removeAll(cloudColors);
            for (Color color : Color.values()) {
                for(int i = 0; i < cloudBean.getStudents().get(color); i ++) {
                        cloudColors.add(color);
                }
            }
            ArrayList<Color> copy = new ArrayList<>(cloudColors);
            cloudColorsMap.put(gameBean.getCloudTiles().indexOf(cloudBean), copy);
        }

        if(gameBean.getPlayers().size() == 2) {
            MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
            Platform.runLater(() -> {
                controller.showClouds(cloudColorsMap);
            });
        } else if (gameBean.getPlayers().size() == 3) {
            MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
            Platform.runLater(() -> {
                controller.showClouds(cloudColorsMap);
            });
        }
    }


    /**
     * This method is used to start the connection with the server
     * @param answerHandler is the object that handle the messages from the server
     * @param socketClient is the socket
     */
    public void startConnection(AnswerHandler answerHandler, SocketClient socketClient) {
        this.answerHandler = answerHandler;
        this.socketClient = socketClient;
        this.answerHandler.addPropertyChangeListener(this);
        this.socketClient.startListening();
        this.socketClient.startPinging();
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public HashMap<String, GUIController> getControllerMap() {
        return  this.controllerMap;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}



