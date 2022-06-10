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

public class MainController3 implements GUIController{
    private GUI gui;

    @FXML private AnchorPane mainPane;
    @FXML private AnchorPane myBoard;
    @FXML private AnchorPane otherFirstBoard;
    @FXML private AnchorPane otherSecondBoard;
    @FXML private Label infoMessage;

    private final HashMap<Integer, Image> hand = new HashMap<>();
    private final HashMap<Color, Image> studentsColor = new HashMap<>();
    private final HashMap<Color, Image> professorsColor = new HashMap<>();

    private ArrayList<AnchorPane> archipelago = new ArrayList<>();
    private ArrayList<ImageView> myEntranceStudents = new ArrayList<>();
    private ArrayList<ImageView> myHallStudents = new ArrayList<>();
    private ArrayList<Circle> towers = new ArrayList<>();
    private ArrayList<ImageView> myProfessors = new ArrayList<>();
    private ArrayList<ImageView>  cards = new ArrayList<>();
    private ArrayList<AnchorPane> clouds = new ArrayList<>();

    private final static Paint BLACK = javafx.scene.paint.Color.BLACK;
    private final static Paint WHITE = javafx.scene.paint.Color.WHITE;
    private final static Paint GRAY = javafx.scene.paint.Color.GRAY;

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

    public MainController3() {
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

    public void showEntranceStudents(ArrayList<Color> colors) {    //TODO gestire con un unico metodo le board con il booleano come parametro
        for(ImageView entranceStudent : myEntranceStudents) {
            myBoard.getChildren().remove(entranceStudent);
        }
        myEntranceStudents.removeAll(myEntranceStudents);
        for (int i = 0; i < colors.size(); i ++) {
            int finalI = i;
            ImageView entranceStudent = new ImageView(studentsColor.get(colors.get(i)));
            myBoard.getChildren().add(entranceStudent);
            entranceStudent.setFitHeight(17);
            entranceStudent.setFitWidth(17);
            entranceStudent.setLayoutX(25);
            entranceStudent.setLayoutY(10 + 18 * i);
            myEntranceStudents.add(entranceStudent);
            entranceStudent.setOnMouseClicked( event -> {
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
        double lastGreenStudentX = 76;
        double lastRedStudentX = 76;
        double lastYellowStudentX = 76;
        double lastPinkStudentX = 76;
        double lastBlueStudentX = 76;
        for(ImageView hallStudent : myHallStudents) {
            myBoard.getChildren().remove(hallStudent);
        }
        myHallStudents.removeAll(myHallStudents);
        for (int i = 0; i < colors.size(); i++) {
            int finalI = i;
            ImageView hallStudent = new ImageView(studentsColor.get(colors.get(i)));
            myBoard.getChildren().add(hallStudent);
            hallStudent.setFitHeight(18);
            hallStudent.setFitWidth(18);
            myHallStudents.add(hallStudent);
            /*if (isMyBoard) */
            hallStudent.setOnMouseClicked(event -> {           //TODO gestire con un unico metodo le board con il booleano come parametro
                sendStudentToMove(colors.get(finalI));
            });
            switch (colors.get(i)) {
                case GREEN -> {
                    hallStudent.setLayoutX(lastGreenStudentX + 19.7);
                    hallStudent.setLayoutY(22);
                    lastGreenStudentX += 19.7;
                }
                case RED -> {
                    hallStudent.setLayoutX(lastRedStudentX + 19.7);
                    hallStudent.setLayoutY(51);
                    lastRedStudentX += 19.7;
                }
                case YELLOW -> {
                    hallStudent.setLayoutX(lastYellowStudentX + 19.7);
                    hallStudent.setLayoutY(81);
                    lastYellowStudentX += 19.7;
                }
                case PINK -> {
                    hallStudent.setLayoutX(lastPinkStudentX + 19.7);
                    hallStudent.setLayoutY(110.5);
                    lastPinkStudentX += 19.7;
                }
                case BLUE -> {
                    hallStudent.setLayoutX(lastBlueStudentX + 19.7);
                    hallStudent.setLayoutY(139);
                    lastBlueStudentX += 19.7;
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

    private void setTowerColor(Paint paint, int numTowers, AnchorPane Board) {
        for (Circle tower : towers) {
            Board.getChildren().remove(tower);
        }
        for (int i = 0; i < numTowers; i ++) {
            Circle tower = new Circle();
            Board.getChildren().add(tower);
            tower.setRadius(9);
            tower.setFill(paint);
            tower.setLayoutX(357);
            tower.setLayoutY(14 + 22 * i);
            towers.add(tower);
        }
    }

    public void showMyProfessors(ArrayList<Color> professorsColors) {
        showProfessors(professorsColors, myProfessors, myBoard);
    }

    private void showProfessors(ArrayList<Color> professorsColors, ArrayList<ImageView> professorsList, AnchorPane Board) {
        for (ImageView professor : professorsList) {
            Board.getChildren().remove(professor);
        }
        professorsList.removeAll(professorsList);
        for (int i = 0; i < professorsColors.size(); i ++) {
            ImageView professor = new ImageView(professorsColor.get(professorsColors.get(i)));
            Board.getChildren().add(professor);
            professor.setFitHeight(20);
            professor.setFitWidth(20);
            professor.setLayoutX(292);
            professorsList.add(professor);
            switch (professorsColors.get(i)) {
                case GREEN -> professor.setLayoutY(22);
                case RED -> professor.setLayoutY(51);
                case YELLOW -> professor.setLayoutY(81);
                case PINK -> professor.setLayoutY(110.5);
                case BLUE -> professor.setLayoutY(139);
            }
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
            card.setLayoutY(675);
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

    public void showClouds(HashMap<Integer, ArrayList<Color>> cloudColorsMap) {
        double lastCloudY = 15;
        for (AnchorPane cloud : clouds) {
            mainPane.getChildren().remove(cloud);
        }
        clouds.removeAll(clouds);
        for (int i = 0; i < 3; i ++) {
            AnchorPane cloudPane = new AnchorPane();
            mainPane.getChildren().add(cloudPane);
            cloudPane.setPrefHeight(120);
            cloudPane.setPrefWidth(120);
            cloudPane.setLayoutX(270);
            cloudPane.setLayoutY(lastCloudY + 154);
            lastCloudY += 154;
            clouds.add(cloudPane);
            ImageView cloud = new ImageView(CLOUD);
            cloudPane.getChildren().add(cloud);
            cloud.setFitWidth(120);
            cloud.setFitHeight(120);
            int finalI = i;
            cloud.setOnMouseClicked( event -> {
                sendCloudIndex(finalI);
            });
            for (int j = 0; j < cloudColorsMap.get(i).size(); j ++) {
                ImageView student = new ImageView(studentsColor.get(cloudColorsMap.get(i).get(j)));
                cloudPane.getChildren().add(student);
                student.setFitWidth(17);
                student.setFitHeight(17);
                student.setLayoutX(25 + 15 * j);
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


    public void showArchipelago(int archipelagoSize, int motherNature, HashMap<Integer, ArrayList<Color>> islandsStudents, HashMap<Integer, Tower> towerColorMap, HashMap<Integer, Integer> numTowersMap) {
        double lastCloudX = 380;
        double lastCloudY = 59;
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
                tower.setLayoutY(40);
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

    public void showInfoMessage(String message) {
        infoMessage.setText(message);
    }
}
