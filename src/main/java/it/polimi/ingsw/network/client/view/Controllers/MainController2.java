package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.messages.ColorChosen;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
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


    private final static Paint BLACK = javafx.scene.paint.Color.BLACK;
    private final static Paint WHITE = javafx.scene.paint.Color.WHITE;
    private final static Paint GRAY = javafx.scene.paint.Color.GRAY;

    private final HashMap<Integer, Image> hand = new HashMap<>();
    private final HashMap<Color, Image> studentsColor = new HashMap<>();
    private final HashMap<Color, Image> professorsColor = new HashMap<>();


    private ArrayList<ImageView> myProfessors = new ArrayList<>();
    private ArrayList<ImageView> otherProfessors = new ArrayList<>();
    private ArrayList<ImageView> myEntranceStudents = new ArrayList<>();
    private ArrayList<ImageView> otherEntranceStudents = new ArrayList<>();
    private ArrayList<ImageView> myHallStudents = new ArrayList<>();
    private ArrayList<ImageView> otherHallStudents = new ArrayList<>();
    private ArrayList<ImageView> cards = new ArrayList<>();
    private ArrayList<ImageView> cloud1Students = new ArrayList<>();
    private ArrayList<ImageView> cloud2Students = new ArrayList<>();
    private ArrayList<Circle> towers = new ArrayList<>();
    private ArrayList<AnchorPane> archipelago = new ArrayList<>();
    private ArrayList<ImageView> coins = new ArrayList<>();
    private ArrayList<AnchorPane> clouds = new ArrayList<>();

    private final static Image GREEN_STUDENT = new Image("/Students/student_green.png");
    private final static Image RED_STUDENT = new Image("/Students/student_red.png");
    private final static Image YELLOW_STUDENT = new Image("/Students/student_yellow.png");
    private final static Image PINK_STUDENT = new Image("/Students/student_pink.png");
    private final static Image BLUE_STUDENT = new Image("Students/student_blue.png");

    private final static Image GREEN_PROF = new Image("/Professors/teacher_green.png");
    private final static Image RED_PROF = new Image("/Professors/teacher_red.png");
    private final static Image YELLOW_PROF = new Image("/Professors/teacher_yellow.png");
    private final static Image PINK_PROF = new Image("/Professors/teacher_pink.png");
    private final static Image BLUE_PROF = new Image("/Professors/teacher_blue.png");

    private final static Image ISLAND = new Image("/island2.png");

    private final static Image  PRIO1 = new Image("/Card/Animali_1_1@3x.png");
    private final static Image  PRIO2 = new Image("/Card/Animali_1_2@3x.png");
    private final static Image  PRIO3 = new Image("/Card/Animali_1_3@3x.png");
    private final static Image  PRIO4 = new Image("/Card/Animali_1_4@3x.png");
    private final static Image  PRIO5 = new Image("/Card/Animali_1_5@3x.png");
    private final static Image  PRIO6 = new Image("/Card/Animali_1_6@3x.png");
    private final static Image  PRIO7 = new Image("/Card/Animali_1_7@3x.png");
    private final static Image  PRIO8 = new Image("/Card/Animali_1_8@3x.png");
    private final static Image  PRIO9 = new Image("/Card/Animali_1_9@3x.png");
    private final static Image  PRIO10 = new Image("/Card/Animali_1_10@3x.png");

    private final static Image COIN = new Image("/Monetabase.png");

    private final static Image CLOUD = new Image("/cloud_card.png");


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
            myHallStudents.add(hallStudent);
            /*if (isMyBoard) */
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

    public void showArchipelago(int archipelagoSize, int motherNature, HashMap<Integer, ArrayList<Color>> islandsStudents, HashMap<Integer, Tower> towerColorMap, HashMap<Integer, Integer> numTowersMap) {
        double lastCloudX = 650;
        double lastCloudY = 100;
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
                student.setLayoutY(20);
            }

            //Tower
            for (int k = 0; k < numTowersMap.get(i); k ++) {
                Circle tower = new Circle();
                islandPane.getChildren().add(tower);
                tower.setRadius(10);
                tower.setLayoutX(20 + 17 * k);
                tower.setLayoutY(60);
                switch (towerColorMap.get(i)) {
                    case WHITE -> tower.setFill(WHITE);
                    case BLACK -> tower.setFill(BLACK);
                    case GRAY -> tower.setFill(GRAY);
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

    public void showExpertCard() {

    }

    public void showInfoMessage(String message) {
        infoMessage.setText(message);
    }
}
