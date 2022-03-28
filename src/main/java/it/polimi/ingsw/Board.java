package it.polimi.ingsw;

import java.util.HashSet;
import java.util.Set;

public class Board {
    private Tower towerColor;
    private int numTower;//actual number of towers
    private StudentsHandler entrance;
    private StudentsHandler hall;
    private Set<Color> professors;
    private final int maxSizeEntr;

    private static final int HALLSIZE = 10; //maximum size for every color
    private static final int ENTRANCESIZE2PLAYER = 7; //maximum size of the entrance, not for the single color
    private static final int ENTRANCESIZE3PLAYER = 9; //maximum size of the hall, not for the single color
    private static final int NUMTOWER = 8;

    //we can handle game for 2 or 3 players
    public Board(int numPlayer) {
        numTower = NUMTOWER;
        professors = new HashSet<>();

        if (numPlayer == 2) {
            entrance = new StudentsHandler(ENTRANCESIZE2PLAYER);
            maxSizeEntr = ENTRANCESIZE2PLAYER;
        }
        else if (numPlayer == 3) {
            entrance = new StudentsHandler(ENTRANCESIZE3PLAYER);
            maxSizeEntr = ENTRANCESIZE3PLAYER;
        }
        else
            throw new IllegalArgumentException("Illegal number of players");

        hall = new StudentsHandler(HALLSIZE);
    }

    //a player can choose his own color of tower. The control over it, is handled in Game class
    public void chooseTower(Tower color){
        if(color != null)
            this.towerColor = color;
        else
            throw new NullPointerException();
    }

    public Tower getTowerColor (){ return towerColor; }

    public int getNumTower(){ return numTower;}

    public void decNumTower(){
        if(this.numTower > 0)
            -- this.numTower;
    }

    //overall number of students in the entrance
    public int entranceSize(){ return entrance.numStudents(); }

    //number of  students of a determined color in the entrance
    public int entranceSize(Color color){
        if(color == null)
            throw new NullPointerException();
        return entrance.numStudents(color);
    }

    //overall number of students in the hall
    public int hallSize(){ return hall.numStudents(); }

    //number of  students of a determined color in the hall
    public int hallSize(Color color){
        if(color == null)
            throw new NullPointerException();
        return hall.numStudents(color);
    }

    public boolean entranceIsFillable(){ return entrance.numStudents() < maxSizeEntr; }


    public void addProfessor(Color color){
        if(color != null)
            professors.add(color);
        else
            throw new NullPointerException();
    }

    public void removeProfessor(Color color){
        if(color != null)
            professors.remove(color);
        else
            throw new NullPointerException();
    }

    //shallow copy
    public Set<Color> getProfessors (){ return new HashSet<>(professors);}

    //put one student in entrance
    public void fillEntrance(Color color){
        if(color == null)
            throw new NullPointerException();
        else if (entranceIsFillable()){
            entrance.add(color);
        }

    }
    //move a student from entrance to hall
    //if the movement cannot be done, entrance and hall are unchanged
    public void entranceToHall(Color studentColor){
        if(studentColor != null && entrance.isRemovable(studentColor) && hall.isAddable(studentColor) ){
            entrance.remove(studentColor);
            hall.add(studentColor);
        }
    }
}
