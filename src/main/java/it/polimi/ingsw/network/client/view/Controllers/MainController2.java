package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.messages.ColorChosen;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.messages.StopMessage;
import it.polimi.ingsw.network.client.view.ExpertCard_ID;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainController2 implements GUIController {

    private GUI gui;

    @FXML private AnchorPane mainPane;
    @FXML private AnchorPane myBoard;
    @FXML private AnchorPane otherBoard;
    @FXML private AnchorPane myCoin;
    @FXML private AnchorPane otherCoin;
    @FXML private Label infoMessage;
    @FXML private Button stopButton;



    private final static Paint BLACK = javafx.scene.paint.Color.BLACK;
    private final static Paint WHITE = javafx.scene.paint.Color.WHITE;
    private final static Paint GRAY = javafx.scene.paint.Color.GRAY;

    private final HashMap<Integer, Image> hand = new HashMap<>();
    private final HashMap<Color, Image> studentsColor = new HashMap<>();
    private final HashMap<Color, Image> professorsColor = new HashMap<>();
    private final HashMap<ExpertCard_ID, Image> characters = new HashMap<>();


    private ArrayList<ImageView> myProfessors = new ArrayList<>();
    private ArrayList<ImageView> otherProfessors = new ArrayList<>();
    private ArrayList<ImageView> myEntranceStudents = new ArrayList<>();
    private ArrayList<ImageView> otherEntranceStudents = new ArrayList<>();
    private ArrayList<ImageView> myHallStudents = new ArrayList<>();
    private ArrayList<ImageView> otherHallStudents = new ArrayList<>();
    private ArrayList<ImageView> cards = new ArrayList<>();
    private ArrayList<Circle> towers = new ArrayList<>();
    private ArrayList<AnchorPane> archipelago = new ArrayList<>();
    private ArrayList<ImageView> coins = new ArrayList<>();
    private ArrayList<AnchorPane> clouds = new ArrayList<>();

    //private final Image GREEN_STUDENT = new Image(String.valueOf(getClass().getResource("/Students/student_green.png")));
    private final Image GREEN_STUDENT = new Image(getClass().getResource("/Students/student_green.png").toExternalForm());
    private final  Image RED_STUDENT = new Image(String.valueOf(getClass().getResource("/Students/student_red.png")));
    private final  Image YELLOW_STUDENT = new Image(String.valueOf(getClass().getResource("/Students/student_yellow.png")));
    private final  Image PINK_STUDENT = new Image(String.valueOf(getClass().getResource("/Students/student_pink.png")));
    private final  Image BLUE_STUDENT = new Image(String.valueOf(getClass().getResource("/Students/student_blue.png")));

    private final  Image GREEN_PROF = new Image(String.valueOf(getClass().getResource("/Professors/teacher_green.png")));
    private final  Image RED_PROF = new Image(String.valueOf(getClass().getResource("/Professors/teacher_red.png")));
    private final  Image YELLOW_PROF = new Image(String.valueOf(getClass().getResource("/Professors/teacher_yellow.png")));
    private final  Image PINK_PROF = new Image(String.valueOf(getClass().getResource("/Professors/teacher_pink.png")));
    private final  Image BLUE_PROF = new Image(String.valueOf(getClass().getResource("/Professors/teacher_blue.png")));

    private final  Image ISLAND = new Image(String.valueOf(getClass().getResource("/island2.png")));

    private final  Image  PRIO1 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_1@3x.png")));
    private final  Image  PRIO2 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_2@3x.png")));
    private final  Image  PRIO3 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_3@3x.png")));
    private final  Image  PRIO4 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_4@3x.png")));
    private final  Image  PRIO5 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_5@3x.png")));
    private final  Image  PRIO6 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_6@3x.png")));
    private final  Image  PRIO7 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_7@3x.png")));
    private final  Image  PRIO8 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_8@3x.png")));
    private final  Image  PRIO9 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_9@3x.png")));
    private final  Image  PRIO10 = new Image(String.valueOf(getClass().getResource("/Card/Animali_1_10@3x.png")));

    private final  Image COIN = new Image(String.valueOf(getClass().getResource("/Monetabase.png")));

    private final  Image CLOUD = new Image(String.valueOf(getClass().getResource("/cloud_card.png")));

    private final Image MONK = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front.jpg")));
    private final Image HERALD = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front2.jpg")));
    private  final Image DELIVERYMAN = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front3.jpg")));
    private final Image HEALER = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front4.jpg")));
    private final Image CENTAUR = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front5.jpg")));
    private final Image JOKER = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front6.jpg")));
    private final Image KNIGHT = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front7.jpg")));
    private final Image POISONER = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front8.jpg")));
    private final Image BARD = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front9.jpg")));
    private  final Image PRINCESS = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front10.jpg")));
    private final Image MONEYLENDER = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front11.jpg")));
    private final Image HOST = new Image(String.valueOf(getClass().getResource("/Expert_card/CarteTOT_front12.jpg")));

    private final Image BAN = new Image(String.valueOf(getClass().getResource("/deny_island_icon.png")));

    private final Image BOARD = new Image(getClass().getResource("/PLANCIA_GIOCO_2.png").toExternalForm());

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public MainController2() {
        hand.put(1, PRIO1);
        hand.put(2, PRIO2);
        hand.put(3, PRIO3);
        hand.put(4, PRIO4);
        hand.put(5, PRIO5);
        hand.put(6, PRIO6);
        hand.put(7, PRIO7);
        hand.put(8, PRIO8);
        hand.put(9, PRIO9);
        hand.put(10, PRIO10);
        studentsColor.put(Color.RED, RED_STUDENT);
        studentsColor.put(Color.GREEN, GREEN_STUDENT);
        studentsColor.put(Color.PINK, PINK_STUDENT);
        studentsColor.put(Color.BLUE, BLUE_STUDENT);
        studentsColor.put(Color.YELLOW, YELLOW_STUDENT);
        professorsColor.put(Color.GREEN, GREEN_PROF);
        professorsColor.put(Color.RED, RED_PROF);
        professorsColor.put(Color.YELLOW, YELLOW_PROF);
        professorsColor.put(Color.PINK, PINK_PROF);
        professorsColor.put(Color.BLUE, BLUE_PROF);
        characters.put(ExpertCard_ID.MONK, MONK);
        characters.put(ExpertCard_ID.HERALD, HERALD);
        characters.put(ExpertCard_ID.DELIVERYMAN, DELIVERYMAN);
        characters.put(ExpertCard_ID.HEALER, HEALER);
        characters.put(ExpertCard_ID.CENTAUR, CENTAUR);
        characters.put(ExpertCard_ID.JOKER, JOKER);
        characters.put(ExpertCard_ID.KNIGHT, KNIGHT);
        characters.put(ExpertCard_ID.POISONER, POISONER);
        characters.put(ExpertCard_ID.BARD, BARD);
        characters.put(ExpertCard_ID.PRINCESS, PRINCESS);
        characters.put(ExpertCard_ID.MONEYLENDER, MONEYLENDER);
        characters.put(ExpertCard_ID.HOST, HOST);
    }

    public void showEntranceStudents(ArrayList<Color> colors) {
            for(ImageView entranceStudent : myEntranceStudents) {
               myBoard.getChildren().remove(entranceStudent);
            }
        myEntranceStudents.removeAll(myEntranceStudents);
        for (int i = 0; i < colors.size(); i ++) {
            int finalI = i;
            ImageView entranceStudent = new ImageView(studentsColor.get(colors.get(i)));
            myBoard.getChildren().add(entranceStudent);
            entranceStudent.setFitHeight(20);
            entranceStudent.setFitWidth(20);
            entranceStudent.setLayoutX(30);
            entranceStudent.setLayoutY(10 + 25 * i);
            entranceStudent.setCursor(Cursor.HAND);
            myEntranceStudents.add(entranceStudent);
            entranceStudent.setOnMouseClicked(event -> {
                sendStudentToMove(colors.get(finalI));
            });
        }
    }

    public void sendStudentToMove(Color studentColor) {
        try {
            gui.getSocketClient().send(new ColorChosen(studentColor));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void showHallStudents(ArrayList<Color> colors) {
        double lastGreenStudentX = 71.8;
        double lastRedStudentX = 71.8;
        double lastYellowStudentX = 71.8;
        double lastPinkStudentX = 71.8;
        double lastBlueStudentX = 71.8;
        for(ImageView hallStudent : myHallStudents) {
            myBoard.getChildren().remove(hallStudent);
        }
        myHallStudents.removeAll(myHallStudents);
        for (int i = 0; i < colors.size(); i++) {
            int finalI = i;
            ImageView hallStudent = new ImageView(studentsColor.get(colors.get(i)));
            myBoard.getChildren().add(hallStudent);
            hallStudent.setFitHeight(20);
            hallStudent.setFitWidth(20);
            hallStudent.setCursor(Cursor.HAND);
            myHallStudents.add(hallStudent);
            hallStudent.setOnMouseClicked(event -> {
                sendStudentToMove(colors.get(finalI));
            });
            switch (colors.get(i)) {
                case GREEN -> {
                    hallStudent.setLayoutX(lastGreenStudentX + 24.2);
                    hallStudent.setLayoutY(28);
                    lastGreenStudentX += 24.2;
                }
                case RED -> {
                    hallStudent.setLayoutX(lastRedStudentX + 24.2);
                    hallStudent.setLayoutY(64);
                    lastRedStudentX += 24.2;
                }
                case YELLOW -> {
                    hallStudent.setLayoutX(lastYellowStudentX + 24.2);
                    hallStudent.setLayoutY(100);
                    lastYellowStudentX += 24.2;
                }
                case PINK -> {
                    hallStudent.setLayoutX(lastPinkStudentX + 24.2);
                    hallStudent.setLayoutY(136);
                    lastPinkStudentX += 24.2;
                }
                case BLUE -> {
                    hallStudent.setLayoutX(lastBlueStudentX + 24.2);
                    hallStudent.setLayoutY(172);
                    lastBlueStudentX += 24.2;
                }
            }
        }
    }


    public void showMyTower(Tower towerColor, int numTowers) {
        switch(towerColor) {
            case WHITE -> setTowerColor(WHITE, numTowers, myBoard);
            case BLACK -> setTowerColor(BLACK, numTowers, myBoard);
            case GRAY -> setTowerColor(GRAY, numTowers, myBoard);
        }
    }

    public void showOtherTower(Tower towerColor, int numTowers) {
        switch(towerColor) {
            case WHITE -> setTowerColor(WHITE, numTowers, otherBoard);
            case BLACK -> setTowerColor(BLACK, numTowers, otherBoard);
            case GRAY -> setTowerColor(GRAY, numTowers, otherBoard);
        }
    }

    private void setTowerColor(Paint paint, int numTowers, AnchorPane Board) {
        for (Circle tower : towers) {
            Board.getChildren().remove(tower);
        }
        for (int i = 0; i < numTowers; i ++) {
            Circle tower = new Circle();
            Board.getChildren().add(tower);
            tower.setRadius(10);
            tower.setFill(paint);
            tower.setLayoutX(450);
            tower.setLayoutY(32 + 22 * i);
            towers.add(tower);
        }
    }

    public void showMyProfessors(ArrayList<Color> professorsColors) {
        showProfessors(professorsColors, myProfessors, myBoard);
    }

    public void showClouds(HashMap<Integer, ArrayList<Color>> cloudColorsMap) {
        double lastCloudX = -43;
        for (AnchorPane cloud : clouds) {
            mainPane.getChildren().remove(cloud);
        }
        clouds.removeAll(clouds);
        for (int i = 0; i < 2; i ++) {
            AnchorPane cloudPane = new AnchorPane();
            mainPane.getChildren().add(cloudPane);
            cloudPane.setPrefHeight(128);
            cloudPane.setPrefWidth(128);
            cloudPane.setLayoutX(lastCloudX + 212);
            lastCloudX += 212;
            cloudPane.setLayoutY(269);
            clouds.add(cloudPane);
            ImageView cloud = new ImageView(CLOUD);
            cloudPane.getChildren().add(cloud);
            cloud.setFitWidth(128);
            cloud.setFitHeight(128);
            cloud.setCursor(Cursor.HAND);
            int finalI = i;
            cloud.setOnMouseClicked( event -> {
                sendCloudIndex(finalI);
            });
            for (int j = 0; j < cloudColorsMap.get(i).size(); j ++) {
                ImageView student = new ImageView(studentsColor.get(cloudColorsMap.get(i).get(j)));
                cloudPane.getChildren().add(student);
                student.setFitWidth(20);
                student.setFitHeight(20);
                student.setLayoutX(30 + 25 * j);
                student.setLayoutY(40);
            }
        }
    }

    public void sendCloudIndex(int index) {
        try {
            gui.getSocketClient().send(new IntegerMessage((index+1)));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public void showAssistantCards(ArrayList<Integer> priorities) {
        double lastCardX = 90;
        for (ImageView card : cards) {
            mainPane.getChildren().remove(card);
        }
        cards.removeAll(cards);
        for (Integer priority : priorities) {
            ImageView card = new ImageView(hand.get(priority));
            mainPane.getChildren().add(card);
            card.setFitHeight(132);
            card.setFitWidth(88);
            card.setLayoutX(lastCardX + 90);
            card.setLayoutY(650);
            card.setCursor(Cursor.HAND);
            lastCardX += 90;
            cards.add(card);
            card.setOnMouseClicked(event -> {
                sendChosenCard(priority);
            });
        }
    }

    public void sendChosenCard(Integer priority) {
        try {
            gui.getSocketClient().send(new IntegerMessage(priority));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void showLastPlayedCard(int lastPriority) {
        ImageView lastPlayedCard = new ImageView(hand.get(lastPriority));
        mainPane.getChildren().add(lastPlayedCard);
        lastPlayedCard.setFitWidth(88);
        lastPlayedCard.setFitHeight(132);
        lastPlayedCard.setLayoutX(634);
        lastPlayedCard.setLayoutY(505);
    }

    public void showOtherPlayerEntrance(ArrayList<Color> colors) {
        for(ImageView entranceStudent : otherEntranceStudents) {
            otherBoard.getChildren().remove(entranceStudent);
        }
        otherEntranceStudents.removeAll(otherEntranceStudents);
        for (int i = 0; i < colors.size(); i ++) {
            ImageView entranceStudent = new ImageView(studentsColor.get(colors.get(i)));
            otherBoard.getChildren().add(entranceStudent);
            entranceStudent.setFitHeight(20);
            entranceStudent.setFitWidth(20);
            entranceStudent.setLayoutX(30);
            entranceStudent.setLayoutY(10 + 25 * i);
            otherEntranceStudents.add(entranceStudent);
        }
    }
    
    public void showOtherLastPlayedCard(int lastPriority) {
        ImageView lastPlayedCard = new ImageView(hand.get(lastPriority));
        mainPane.getChildren().add(lastPlayedCard);
        lastPlayedCard.setFitWidth(88);
        lastPlayedCard.setFitHeight(132);
        lastPlayedCard.setLayoutX(634);
        lastPlayedCard.setLayoutY(22);
    }

    public void showOtherPlayerProfessors(ArrayList<Color> professorsColors) {
        showProfessors(professorsColors, otherProfessors, otherBoard);
    }
    
    public void showOtherPLayerHall(ArrayList<Color> colors) {
        double lastGreenStudentX = 71.8;
        double lastRedStudentX = 71.8;
        double lastYellowStudentX = 71.8;
        double lastPinkStudentX = 71.8;
        double lastBlueStudentX = 71.8;
        for(ImageView hallStudent : otherHallStudents) {
            myBoard.getChildren().remove(hallStudent);
        }
        otherHallStudents.removeAll(otherHallStudents);
        for (int i = 0; i < colors.size(); i++) {
            ImageView hallStudent = new ImageView(studentsColor.get(colors.get(i)));
            otherBoard.getChildren().add(hallStudent);
            hallStudent.setFitHeight(20);
            hallStudent.setFitWidth(20);
            otherHallStudents.add(hallStudent);
            switch (colors.get(i)) {
                case GREEN -> {
                    hallStudent.setLayoutX(lastGreenStudentX + 24.2);
                    hallStudent.setLayoutY(28);
                    lastGreenStudentX += 24.2;
                }
                case RED -> {
                    hallStudent.setLayoutX(lastRedStudentX + 24.2);
                    hallStudent.setLayoutY(64);
                    lastRedStudentX += 24.2;
                }
                case YELLOW -> {
                    hallStudent.setLayoutX(lastYellowStudentX + 24.2);
                    hallStudent.setLayoutY(100);
                    lastYellowStudentX += 24.2;
                }
                case PINK -> {
                    hallStudent.setLayoutX(lastPinkStudentX + 24.2);
                    hallStudent.setLayoutY(136);
                    lastPinkStudentX += 24.2;
                }
                case BLUE -> {
                    hallStudent.setLayoutX(lastBlueStudentX + 24.2);
                    hallStudent.setLayoutY(172);
                    lastBlueStudentX += 24.2;
                }
            }
        }
    }

    private void showProfessors(ArrayList<Color> professorsColors, ArrayList<ImageView> professorsList, AnchorPane Board) {
        for (ImageView professor : professorsList) {
            Board.getChildren().remove(professor);
        }
        professorsList.removeAll(professorsList);
        for (int i = 0; i < professorsColors.size(); i ++) {
            ImageView professor = new ImageView(professorsColor.get(professorsColors.get(i)));
            Board.getChildren().add(professor);
            professor.setFitHeight(25);
            professor.setFitWidth(25);
            professor.setLayoutX(359);
            professorsList.add(professor);
            switch (professorsColors.get(i)) {
                case GREEN -> professor.setLayoutY(26);
                case RED -> professor.setLayoutY(62);
                case YELLOW -> professor.setLayoutY(98);
                case PINK -> professor.setLayoutY(134);
                case BLUE -> professor.setLayoutY(170);
            }
        }
    }

    public void showArchipelago(int archipelagoSize, int motherNature, HashMap<Integer, ArrayList<Color>> islandsStudents, HashMap<Integer, Tower> towerColorMap, HashMap<Integer, Integer> numTowersMap, HashMap<Integer, Boolean> bannedIslands) {
        double lastCloudX = 650;
        double lastCloudY = 100;
        int studCount = 0;
        int count = 0;
        for (AnchorPane islandPane : archipelago) {
            mainPane.getChildren().remove(islandPane);
        }
        for (int i = 0; i < archipelagoSize; i ++) {
            int finalI = i;
            AnchorPane islandPane = new AnchorPane();
            mainPane.getChildren().add(islandPane);
            islandPane.setPrefHeight(128);
            islandPane.setPrefWidth(128);
            if (count < 4) {
                islandPane.setLayoutX(lastCloudX + 100);
                islandPane.setLayoutY(lastCloudY);
                lastCloudX += 100;
                count += 1;
            }
            else if (count >= 4 && count <= 6) {
                islandPane.setLayoutX(lastCloudX);
                islandPane.setLayoutY(lastCloudY + 100);
                lastCloudY += 100;
                count += 1;
            }
            else if (count >= 7 && count <= 9) {
                islandPane.setLayoutX(lastCloudX - 100);
                islandPane.setLayoutY(lastCloudY);
                lastCloudX -= 100;
                count += 1;
            }
            else if (count >= 10 && count <= 11) {
                islandPane.setLayoutX(lastCloudX);
                islandPane.setLayoutY(lastCloudY - 100);
                lastCloudY -= 100;
                count += 1;
            }

            // Islands
            ImageView island = new ImageView(ISLAND);
            island.setFitHeight(128);
            island.setFitWidth(128);
            islandPane.getChildren().add(island);
            island.setCursor(Cursor.HAND);
            island.setOnMouseClicked(event -> {
                sendIslandIndex(finalI);
            });

            //Mother Nature
            if (i == motherNature) {
                Circle mother = new Circle();
                islandPane.getChildren().add(mother);
                mother.setFill(javafx.scene.paint.Color.ORANGE);
                mother.setRadius(10);
                mother.setLayoutX(25);
                mother.setLayoutY(85);
            }

            //Island students
            for (int j = 0; j < islandsStudents.get(i).size(); j ++) {
                ImageView student = new ImageView(studentsColor.get(islandsStudents.get(i).get(j)));
                islandPane.getChildren().add(student);
                student.setFitWidth(15);
                student.setFitHeight(15);
                student.setLayoutX(20 + 17 * j);
                //TODO sistemare la x. quando vado a capo non viene resettata
                /*if (islandsStudents.get(i).indexOf(islandsStudents.get(i).get(j)) > 4) {
                    student.setLayoutY(35);
                } else {*/
                    student.setLayoutY(20);
                //}
            }

            //Tower
            for (int k = 0; k < numTowersMap.get(i); k ++) {
                Circle tower = new Circle();
                islandPane.getChildren().add(tower);
                tower.setRadius(10);
                tower.setLayoutX(25 + 20 * k);
                tower.setLayoutY(60);
                switch (towerColorMap.get(i)) {
                    case WHITE -> tower.setFill(WHITE);
                    case BLACK -> tower.setFill(BLACK);
                    case GRAY -> tower.setFill(GRAY);
                }
            }

            //Banned Islands
            for (int h = 0; h < bannedIslands.size(); h ++) {
                if (bannedIslands.get(i)) {
                    ImageView banTile = new ImageView(BAN);
                    islandPane.getChildren().add(banTile);
                    banTile.setFitWidth(20);
                    banTile.setFitHeight(20);
                    banTile.setLayoutX(85);
                    banTile.setLayoutY(85);
                }
            }
            archipelago.add(islandPane);
        }
    }

    public void sendIslandIndex(int index) {
        try {
            gui.getSocketClient().send(new IntegerMessage(index + 1));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void showCoin(int numCoin, AnchorPane Board) {
        for (ImageView coin : coins) {
            Board.getChildren().remove(coin);
        }
        for (int i = 0; i < numCoin; i++) {
            ImageView coin = new ImageView(COIN);
            Board.getChildren().add(coin);
            coin.setFitWidth(50);
            coin.setFitHeight(50);
            coin.setLayoutX(10);
            coin.setLayoutY(10 + 20 * i);
            coins.add(coin);
        }
    }

    public void showMyCoin(int numCoin) {
        showCoin(numCoin, myCoin);
    }

    public void showOtherCoin(int numCoin) {
        showCoin(numCoin, otherCoin);
    }

    public void showExpertCard(ArrayList<ExpertCard_ID> expertCards, HashMap<Integer, ArrayList<Color>> studBufferColor) {
        double lastExpertCardX = 810;
        for (int i = 0; i < expertCards.size(); i ++) {
            AnchorPane expertCardPane = new AnchorPane();
            mainPane.getChildren().add(expertCardPane);
            expertCardPane.setPrefHeight(88);
            expertCardPane.setPrefWidth(59);
            expertCardPane.setLayoutX(lastExpertCardX + 62);
            expertCardPane.setLayoutY(218);
            lastExpertCardX += 62;
            ImageView expertCard = new ImageView(characters.get(expertCards.get(i)));
            expertCardPane.getChildren().add(expertCard);
            expertCard.setFitHeight(88);
            expertCard.setFitWidth(59);
            expertCard.setCursor(Cursor.HAND);
            int finalI = i;
            expertCard.setOnMouseClicked( event -> {
                sendExpertCard(finalI, expertCards.get(finalI));
            });

            //Stud buffer color
            if (studBufferColor.get(i) != null) {
                for (int j = 0; j < studBufferColor.get(i).size(); j++) {
                    ImageView studCardColor = new ImageView(studentsColor.get(studBufferColor.get(i).get(j)));
                    expertCardPane.getChildren().add(studCardColor);
                    studCardColor.setFitWidth(15);
                    studCardColor.setFitHeight(15);
                    studCardColor.setLayoutX(10 * j);
                    studCardColor.setLayoutY(50);
                    int finalJ = j;
                    studCardColor.setOnMouseClicked(event -> {
                        sendStudentToMove(studBufferColor.get(finalI).get(finalJ));
                    });
                }
            }
        }
    }

    public void sendExpertCard(int index, ExpertCard_ID expertcard) {
        if (expertcard == ExpertCard_ID.BARD || expertcard == ExpertCard_ID.JOKER) {
            stopButton.setVisible(true);
        }
        try {
            gui.getSocketClient().send(new IntegerMessage(index +1));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void stopButton () {
        try {
            gui.getSocketClient().send(new StopMessage());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        stopButton.setVisible(false);
    }

    public void showInfoMessage(String message) {
        infoMessage.setText(message);
    }
}
