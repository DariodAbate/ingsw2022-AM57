package it.polimi.ingsw;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represent the board, called in italian "Plancia", of each individual player.
 * It contains the towers a player has chosen, the professors earned by him and the students
 * present in the entrance and hall
 *
 * @author Dario d'Abate
 */
public class Board {
    private Tower towerColor;
    private int numTower;//actual number of towers
    private StudentsHandler entrance;
    private StudentsHandler hall;
    private Set<Color> professors;
    private final int maxSizeEntr;
    private final int numPlayer;

    private static final int HALLSIZE = 10; //maximum size for every color
    private static final int ENTRANCESIZE2PLAYER = 7; //maximum size of the entrance, not for the single color
    private static final int ENTRANCESIZE3PLAYER = 9; //maximum size of the hall, not for the single color
    private static final int NUMTOWER2PLAYER = 8;
    private static final int NUMTOWER3PLAYER = 6;

    /**
     * Constructor of the class. It can handle games for 2 or 3 players
     * @param numPlayer Number of player
     * @throws IllegalArgumentException if it is passed a number of player that's neither 2 nor 3
     */
    public Board(int numPlayer) {
        this.numPlayer = numPlayer;
        professors = new HashSet<>();

        if (numPlayer == 2) {
            entrance = new StudentsHandler(ENTRANCESIZE2PLAYER);
            maxSizeEntr = ENTRANCESIZE2PLAYER;
            numTower = NUMTOWER2PLAYER;
        }
        else if (numPlayer == 3) {
            entrance = new StudentsHandler(ENTRANCESIZE3PLAYER);
            maxSizeEntr = ENTRANCESIZE3PLAYER;
            numTower = NUMTOWER3PLAYER;
        }
        else
            throw new IllegalArgumentException("Illegal number of players");

        hall = new StudentsHandler(HALLSIZE);
    }

    /**
     *This method is invoked by a player to choose his tower's color
     * @param color It is the color chosen by each individual player
     */
    public void chooseTower(Tower color){
        if(color != null)
            this.towerColor = color;
        else
            throw new NullPointerException();
    }

    /**
     *Getter that return the tower's color
     * @return the tower's color chosen by a player
     */
    public Tower getTowerColor (){ return towerColor; }

    /**
     *Getter that return the number of remaining towers
     * @return number of remaining towers of a player
     */
    public int getNumTower(){ return numTower;}

    /**
     * This method is invoked every time a player conquer an island.
     * If there are remaining towers it decreases the number by one,
     * otherwise unchanged
     */
    public void decNumTower(){
        if(this.numTower > 0)
            -- this.numTower;
    }

    /**
     * This method is invoked every time a player has to retrieve his towers
     * from an island.
     * If there are some missing towers in the board it increases the number by one,
     * otherwise the number is unchanged
     */
    public void incNumTower(){
        if(this.numTower < NUMTOWER2PLAYER && numPlayer == 2 ||
                this.numTower < NUMTOWER3PLAYER && numPlayer == 3)
            ++ this.numTower;
    }

    /**
     *Gets the total number of students in the entrance
     * @return  the overall number of students in the entrance
     */
    public int entranceSize(){ return entrance.numStudents(); }

    /**
     *Gets the number of students in the entrance of the color specified as parameter
     * @param color Color of the students in the entrance
     * @return Number of  students of a determined color in the entrance
     */
    public int entranceSize(Color color){
        if(color == null)
            throw new NullPointerException();
        return entrance.numStudents(color);
    }

    /**
     * Gets the total number of students in the hall
     * @return the overall number of students in the hall
     */
    public int hallSize(){ return hall.numStudents(); }

    /**
     *Gets the number of students in the hall of the color specified as parameter
     * @param color color of the students in the hall
     * @return number of  students of a determined color in the hall
     */
    public int hallSize(Color color){
        if(color == null)
            throw new NullPointerException();
        return hall.numStudents(color);
    }

    /**
     *Indicates whether a student can be placed in the entrance
     * @return true if the entrance can be filled with a student, false otherwise
     */
    public boolean entranceIsFillable(){ return entrance.numStudents() < maxSizeEntr; }

    /**
     * This method add a professor to a player's board if
     * it is not contained in the set, otherwise the sei is unchanged
     * @param color color of the professor to be added
     */
    public void addProfessor(Color color){
        if(color != null)
            professors.add(color);
        else
            throw new NullPointerException();
    }

    /**
     * This method removes an existing professor from a player's board,
     * otherwise the set of professor is unchanged
     * @param color color of the professor to be removed
     */
    public void removeProfessor(Color color){
        if(color != null)
            professors.remove(color);
        else
            throw new NullPointerException();
    }

    /**
     * Getter method that return the set of professor contained in a player's board
     * @return the copy of the set of professors
     */
    public Set<Color> getProfessors (){ return new HashSet<>(professors);} //shallow copy

    /**
     * This method put a single student in the entrance
     * @param color Color of the student to be added
     */
    public void fillEntrance(Color color){
        if(color == null)
            throw new NullPointerException();
        else if (entranceIsFillable()){
            entrance.add(color);
        }
    }

    /**
     * This method moves a student from entrance to hall if the movement can be done, otherwise
     * entrance and hall are unchanged
     * @param studentColor It is the color of the student to be moved from entrance to hall
     */
    public void entranceToHall(Color studentColor){
        if(studentColor != null && entrance.isRemovable(studentColor) && hall.isAddable(studentColor) ){
            entrance.remove(studentColor);
            hall.add(studentColor);
        }
    }
}
