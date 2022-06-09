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

public class GUI extends Application implements PropertyChangeListener,UI{

    public static final String MAIN_SCENE_FOR2 = "mainScene2Player.fxml";
    public static final String MAIN_SCENE_FOR3 = "mainScene3Player.fxml";
    public static final String MENU = "mainMenu.fxml";
    public static final String SETUP = "setup.fxml";
    public static final String LOADING = "loading.fxml";
    public static final String GENERIC = "genericScene.fxml";
    public static final String CHOICE = "chooseCardBack.fxml";
    private Stage stage;
    private Scene currentScene;
    private GameBean gameBean;
    private String nickname;
    private CardBack cardBack;
    private SocketClient socketClient;
    private AnswerHandler answerHandler;
    private ArrayList<Integer> priorities = new ArrayList<>();
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
        ArrayList<String> fxmList = new ArrayList<>(Arrays.asList(MAIN_SCENE_FOR2, MAIN_SCENE_FOR3, MENU, SETUP, LOADING, GENERIC, CHOICE));
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

    public void closeUserInterface() {

     }

     public void reqNickname(String message) {
         System.out.println(message + " (request nickname message)");
         Platform.runLater(() -> {
             MainMenuController controller = (MainMenuController) controllerMap.get(MENU);
             nickname = controller.getNickname();
         });
     }

     public void reqNumOfPlayer(String message) {
         System.out.println(message + " (request numPlayer message)");
         Platform.runLater(() -> {
             LoaderController controller = (LoaderController) controllerMap.get(LOADING);
             controller.requestNumOfPlayer(message);
         });
    }

    public void reqExpertMode(String message){
        Platform.runLater(() -> {
            LoaderController controller = (LoaderController) controllerMap.get(LOADING);
            controller.requestExpertMode(message);
        });
    }

    public void startGame(String message) {
        System.out.println(message + " (start message)");
        ChoiceController controller = (ChoiceController) controllerMap.get(CHOICE);
        MainController2 mainController = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
        Platform.runLater(() -> {
            controller.setVisibility();
            mainController.showInfoMessage(message);
            changeStage(CHOICE);
        });
    }

     public void displayGenericMessage(String message){
        System.out.println(message + " (generic message)");
        if(message.contains("Username not available")) {   //TODO gestire Username not available
                Platform.runLater(() -> {

                });
            }


        /*else if(message.contains("Please select the priority of the card")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.priorityCardInfo();
            });
        } */

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
                controller.numOfIslandToTravel(message);
            });
        }

        else if(message.contains("Select one of the clouds")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.selectCloudInfo(message);
            });
        }

        else if(message.contains("valid number of steps")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.numOfIslandToTravel(message);
            });
        }

        /*else if(message.contains("Select the island where you want to place your student")) {
            Platform.runLater(() -> {
                GenericController controller = (GenericController) controllerMap.get(GENERIC);
                controller.selectIslandInfo(message);
            });
        }*/

        else  {
            Platform.runLater(() -> {
                MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
                controller.showInfoMessage(message);
            });
            Platform.runLater(() -> {
                MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
                controller.showInfoMessage(message);
                });
        }
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
        });
    }

    public void displayAllGame() {
        Platform.runLater(() -> {
            if (gameBean.getPlayers().size() == 2) {
                changeStage(MAIN_SCENE_FOR2);
            } else if (gameBean.getPlayers().size() == 3) {
                changeStage(MAIN_SCENE_FOR3);
            }
            for (int i = 0; i < gameBean.getPlayers().size(); i++) {
                displayBoard(gameBean.getPlayers().get(i).getBoard(), gameBean.isExpertGame(), gameBean.getPlayers().get(i).getNickname().equals(nickname));
                if (gameBean.getPlayers().get(i).getNickname().equals(nickname)) displayCard(gameBean.getPlayers().get(i));
            }
            displayClouds();
            displayArchipelago();
        });
    }

    @Override
    public void displayBoard(BoardBean board, boolean expertGame) {     //FIXME metodo dell' interfaccia UI

    }

    public void displayBoard(BoardBean board, boolean expertGame, boolean isMyBoard) {
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
                    controller.showEntranceStudents(entranceColors);
                    controller.showHallStudents(hallColors);
                    controller.showMyTower(board.getTowerColor(), board.getNumTowers());
                    controller.showMyProfessors(professorsColors);
                });

                //TODO per gli altri giocatori fare una mappa <indice player, board>
            }
        }
    }


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

    public int otherLastPlayedCard() {   //TODO per adesso funziona solo per 2
                                         //TODO per 3 fare una mappa <indice player, ultima carta giocata>
        int lastPriority = 0;
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
        }
    }

    public void displayArchipelago() {
        HashMap<Integer, ArrayList<Color>> islandColorsMap = new HashMap<>();
        HashMap<Integer, Tower> towerColorMap = new HashMap<>();
        HashMap<Integer, Integer> numTowersMap = new HashMap<>();
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
        }
        Platform.runLater(() -> {
            if (gameBean.getPlayers().size() == 2) {
                MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
                controller.showArchipelago(gameBean.getArchipelago().size(), gameBean.getMotherNature(), islandColorsMap, towerColorMap, numTowersMap);
            } else if (gameBean.getPlayers().size() == 3){
                MainController3 controller = (MainController3) controllerMap.get(MAIN_SCENE_FOR3);
                controller.showArchipelago(gameBean.getArchipelago().size(), gameBean.getMotherNature(), islandColorsMap, towerColorMap, numTowersMap);
            }
        });
    }

    public void displayExpertCard() {
        ArrayList<ExpertCard_ID> expertCards = new ArrayList<>();
        MainController2 controller = (MainController2) controllerMap.get(MAIN_SCENE_FOR2);
        for (ExpertCardBean expertCardBean : gameBean.getExpertCards()){
            expertCards.add(expertCardBean.getName());
        }
    }

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
}


