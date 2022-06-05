package it.polimi.ingsw.network.client.view.Controllers;


import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.messages.ColorChosen;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;



public class MainController2 implements GUIController {

    private GUI gui;

    @FXML private ImageView prio1;
    @FXML private ImageView prio2;
    @FXML private ImageView prio3;
    @FXML private ImageView prio4;
    @FXML private ImageView prio5;
    @FXML private ImageView prio6;
    @FXML private ImageView prio7;
    @FXML private ImageView prio8;
    @FXML private ImageView prio9;
    @FXML private ImageView prio10;
    @FXML private ImageView myLastPlayedCard;
    @FXML private ImageView otherLastPlayedCard;
    @FXML private ImageView myEntranceStud0;
    @FXML private ImageView myEntranceStud1;
    @FXML private ImageView myEntranceStud2;
    @FXML private ImageView myEntranceStud3;
    @FXML private ImageView myEntranceStud4;
    @FXML private ImageView myEntranceStud5;
    @FXML private ImageView myEntranceStud6;
    @FXML private ImageView otherEntranceStud0;
    @FXML private ImageView otherEntranceStud1;
    @FXML private ImageView otherEntranceStud2;
    @FXML private ImageView otherEntranceStud3;
    @FXML private ImageView otherEntranceStud4;
    @FXML private ImageView otherEntranceStud5;
    @FXML private ImageView otherEntranceStud6;
    @FXML private GridPane myHall;
    @FXML private Circle myTower0;
    @FXML private Circle myTower1;
    @FXML private Circle myTower2;
    @FXML private Circle myTower3;
    @FXML private Circle myTower4;
    @FXML private Circle myTower5;
    @FXML private Circle myTower6;
    @FXML private Circle myTower7;
    @FXML private ImageView cloud1Stud0;
    @FXML private ImageView cloud1Stud1;
    @FXML private ImageView cloud1Stud2;
    @FXML private ImageView cloud2Stud0;
    @FXML private ImageView cloud2Stud1;
    @FXML private ImageView cloud2Stud2;
    private int lastGreenStudentHall = 0;
    private int lastRedStudentHall = 0;
    private int lastYellowStudentHall = 0;
    private int lastPinkStudentHall = 0;
    private int lastBlueStudentHall = 0;


    private final static Paint BLACK = javafx.scene.paint.Color.BLACK;
    private final static Paint WHITE = javafx.scene.paint.Color.WHITE;
    private final static Paint GRAY = javafx.scene.paint.Color.GRAY;

    private final HashMap<Integer, Image> hand = new HashMap<>();
    private HashMap<ImageView, Color> entrance = new HashMap<>();
    private HashMap<Color, Image> students = new HashMap<>();

    private ArrayList<ImageView> movedStudents = new ArrayList<>();



    private final static Image RED_STUDENT = new Image("/Students/student_red.png",20, 20, false, false);
    private final static Image BLUE_STUDENT = new Image("Students/student_blue.png",20, 20, false, false);
    private final static Image GREEN_STUDENT = new Image("/Students/student_green.png",20, 20, false, false);
    private final static Image PINK_STUDENT = new Image("/Students/student_pink.png",20, 20, false, false);
    private final static Image YELLOW_STUDENT = new Image("/Students/student_yellow.png",20, 20, false, false);

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
        students.put(Color.RED, RED_STUDENT);
        students.put(Color.GREEN, GREEN_STUDENT);
        students.put(Color.PINK, PINK_STUDENT);
        students.put(Color.BLUE, BLUE_STUDENT);
        students.put(Color.YELLOW, YELLOW_STUDENT);
    }

    public void showEntranceStudents(ArrayList<Color> color) {     //TODO controllare la put con la stessa chiave
        if(color.size() == 7) {
            myEntranceStud0.setImage(students.get(color.get(0)));
            entrance.put(myEntranceStud0, color.get(0));
            myEntranceStud1.setImage(students.get(color.get(1)));
            entrance.put(myEntranceStud1, color.get(1));
            myEntranceStud2.setImage(students.get(color.get(2)));
            entrance.put(myEntranceStud2, color.get(2));
            myEntranceStud3.setImage(students.get(color.get(3)));
            entrance.put(myEntranceStud3, color.get(3));
            myEntranceStud4.setImage(students.get(color.get(4)));
            entrance.put(myEntranceStud4, color.get(4));
            myEntranceStud5.setImage(students.get(color.get(5)));
            entrance.put(myEntranceStud5, color.get(5));
            myEntranceStud6.setImage(students.get(color.get(6)));
            entrance.put(myEntranceStud6, color.get(6));
        }
    }

    public void student0ToMove() {
        sendColor(myEntranceStud0);
    }

    public void student1ToMove() {
        sendColor(myEntranceStud1);
    }

    public void student2ToMove() {
        sendColor(myEntranceStud2);
    }

    public void student3ToMove() {
        sendColor(myEntranceStud3);
    }

    public void student4ToMove() {
        sendColor(myEntranceStud4);
    }

    public void student5ToMove() {
        sendColor(myEntranceStud5);
    }

    public void student6ToMove() {
        sendColor(myEntranceStud6);
    }

    private void sendColor(ImageView myEntranceStud) {
        try {
            gui.getSocketClient().send(new ColorChosen(entrance.get(myEntranceStud)));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        myEntranceStud.setVisible(false);
        movedStudents.add(myEntranceStud);
        switch (entrance.get(myEntranceStud)) {
            case GREEN -> {
                myHall.add(new ImageView(GREEN_STUDENT), lastGreenStudentHall, 0, 1, 1);
                lastGreenStudentHall += 1;
            }
            case RED -> {
                myHall.add(new ImageView(RED_STUDENT), lastRedStudentHall,1, 1, 1);
                lastRedStudentHall += 1;
            }
            case YELLOW -> {
                myHall.add(new ImageView(YELLOW_STUDENT), lastYellowStudentHall,2, 1, 1);
                lastYellowStudentHall += 1;
            }
            case PINK -> {
                myHall.add(new ImageView(PINK_STUDENT), lastPinkStudentHall,3, 1, 1);
                lastPinkStudentHall += 1;
            }
            case BLUE -> {
                myHall.add(new ImageView(BLUE_STUDENT), lastBlueStudentHall,4, 1, 1);
                lastBlueStudentHall += 1;
            }
        }
    }

    public void showTower(Tower towerColor) {
        switch(towerColor) {
            case WHITE -> setTowerColor(WHITE);
            case BLACK -> setTowerColor(BLACK);
            case GRAY -> setTowerColor(GRAY);
        }
    }

    private void setTowerColor(Paint paint) {
            myTower0.setFill(paint);
            myTower1.setFill(paint);
            myTower2.setFill(paint);
            myTower3.setFill(paint);
            myTower4.setFill(paint);
            myTower5.setFill(paint);
            myTower6.setFill(paint);
            myTower7.setFill(paint);
    }

    public void updateClouds(ArrayList<Color> color, int numCloud) {
        cloud1Stud0.setVisible(true);
        cloud1Stud1.setVisible(true);
        cloud1Stud2.setVisible(true);
        cloud2Stud0.setVisible(true);
        cloud2Stud1.setVisible(true);
        cloud2Stud2.setVisible(true);
        if(color.size() == 3) {
        cloud1Stud0.setImage(RED_STUDENT);
        cloud1Stud1.setImage(YELLOW_STUDENT);
        cloud1Stud2.setImage(YELLOW_STUDENT);}
        System.out.println("size = " + color.size());
        if(color.size() == 3) {
            if (numCloud == 0) {
                cloud1Stud0.setImage(students.get(color.get(0)));
                cloud1Stud1.setImage(students.get(color.get(1)));
                cloud1Stud2.setImage(students.get(color.get(2)));
            } else if (numCloud == 1) {
                cloud2Stud0.setImage(students.get(color.get(0)));
                cloud2Stud1.setImage(students.get(color.get(1)));
                cloud2Stud2.setImage(students.get(color.get(2)));
            }
        }
    }
    

    public void selectedCloud1() {
        try {
            gui.getSocketClient().send(new IntegerMessage(1));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        updateStudents();
        cloud1Stud0.setVisible(false);
        cloud1Stud1.setVisible(false);
        cloud1Stud2.setVisible(false);
    }

    public void selectedCloud2() {
        try {
            gui.getSocketClient().send(new IntegerMessage(2));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        updateStudents();
        cloud2Stud0.setVisible(false);
        cloud2Stud1.setVisible(false);
        cloud2Stud2.setVisible(false);
    }

    public void updateStudents() {
        for(ImageView movedStudent : movedStudents) {
            movedStudent.setVisible(true);
        }
        movedStudents.removeAll(movedStudents);
    }

    public void chosenCard_1() {
        System.out.println("scelta prio 1");
        myLastPlayedCard.setImage(PRIO1);
        prio1.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(1));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_2() {
        System.out.println("scelta prio 2");
        myLastPlayedCard.setImage(PRIO2);
        prio2.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(2));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_3() {
        System.out.println("scelta prio 3");
        myLastPlayedCard.setImage(PRIO3);
        prio3.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(3));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_4() {
        System.out.println("scelta prio 4");
        myLastPlayedCard.setImage(PRIO4);
        prio4.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(4));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_5() {
        System.out.println("scelta prio 5");
        myLastPlayedCard.setImage(PRIO5);
        prio5.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(5));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_6() {
        System.out.println("scelta prio 6");
        myLastPlayedCard.setImage(PRIO6);
        prio6.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(6));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_7() {
        System.out.println("scelta prio 7");
        myLastPlayedCard.setImage(PRIO7);
        prio7.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(7));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_8() {
        System.out.println("scelta prio 8");
        myLastPlayedCard.setImage(PRIO8);
        prio8.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(8));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_9() {
        System.out.println("scelta prio 9");
        myLastPlayedCard.setImage(PRIO9);
        prio9.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(9));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void chosenCard_10() {
        System.out.println("scelta prio 10");
        myLastPlayedCard.setImage(PRIO10);
        prio10.setVisible(false);
        try {
            gui.getSocketClient().send(new IntegerMessage(10));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void updateOtherLastPlayedCard() {
        if (gui.getOtherPlayersLastCard() != null) {
            otherLastPlayedCard.setImage(hand.get(gui.getOtherPlayersLastCard().getPriority()));
        }
    }
}
