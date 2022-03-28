package it.polimi.ingsw;

import java.util.HashSet;
import java.util.Set;

public class Board {
    private Tower towerColor;
    private int numTower;//actual number of towers
    private StudentsHandler entrance;
    private StudentsHandler hall;
    private Set<Color> professors;


    private static final int  HALLSIZE = 10; //maximum size for every color
    private static final int ENTRANCESIZE2PLAYER = 7;
    private static final int ENTRANCESIZE3PLAYER = 9;
    private static final int NUMTOWER = 8;

    //we can handle game for 2 or 3 players
    public Board(int numPlayer) {
        numTower = NUMTOWER;
        professors = new HashSet<>();
        //the size specified into the constructor represent the maximum size for a single color
        if (numPlayer == 2)
            entrance = new StudentsHandler(ENTRANCESIZE2PLAYER);
        else if (numPlayer == 3)
            entrance = new StudentsHandler(ENTRANCESIZE3PLAYER);
        else
            throw new IllegalArgumentException("Illegal number of players");

        hall = new StudentsHandler(HALLSIZE);
    }


    //into "Game" class we select the right color
    public void chooseTower(Tower color){ this.towerColor = color;}

    public Tower getTowerColor (){
        return towerColor;
    }

    public int getNumTower(){ return numTower;}

    public void decreaseNumTower(){
        if(this.numTower > 0)
            -- this.numTower;
    }



    public void addProfessor(Color color){
        if(color != null)
            professors.add(color);
    }

    public void removeProfessor(Color color){professors.remove(color);}

    //shallow copy
    public Set<Color> getProfessors (){ return new HashSet<>(professors);}

    //move a student from entrance to hall
    //if the movement cannot be done, entrance and hall are unchanged
    //there's a method in another class that puts students in hall
    public void entranceToHall(Color studentColor){
        if(studentColor != null && entrance.isRemovable(studentColor) && hall.isAddable(studentColor) ){
            entrance.remove(studentColor);
            hall.add(studentColor);
        }
    }

    /*
    //method used only for testing entranceToHall, it will be removed
    public StudentsHandler getEntrance(){return this.entrance;}

    //method used only for testing entranceToHall, it will be removed
    public StudentsHandler getHall(){return this.hall;}

     */
}
