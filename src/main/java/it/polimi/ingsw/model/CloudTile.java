package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represent the cloud tile, shared by every player.
 * It contains an attribute that represent the sets of students and the maximum size
 * of this set
 * @author Dario d'Abate
 */
public class CloudTile implements Serializable {
    private StudentsHandler cloudStud;
    GameConstants gameConstants;

    /**
     * Constructor of the class. It can handle games for 2 or 3 players
     * @param gameConstants is the object with all the constants in the game
     *@throws IllegalArgumentException if it is passed a number of player that's neither 2 nor 3
     */
    public CloudTile(GameConstants gameConstants){
        this.gameConstants = gameConstants;
        cloudStud = new StudentsHandler(gameConstants.getNumStudentsOnCloud());
    }

    /**
     *Getter method for the number of students on this tile
     *@return Total number of students on the tile
     */
    public int numStudOn(){
        return cloudStud.numStudents();
    }

    /**
     *Getter method for the number of students of a specified color on a tile
     * @param color Color of the students on the tile
     * @return Number of  students of a determined color on the tile
     */
    public int numStudOn(Color color){
        if(color == null)
            throw new NullPointerException();
        return cloudStud.numStudents(color);
    }

    /**
     * Indicates if a tile has no students on it
     * @return True if a tile does not contain a student, false otherwise
     */
    public boolean isEmpty(){ return cloudStud.numStudents() == 0; }

    /**
     * This method is invoked as a helper when filling a cloud tile.
     * It indicates that the cloud tile can be filled with another student
     * @return True if the tile can be filled with one more student, false otherwise
     */
    public boolean isFillable(){ return cloudStud.numStudents() < gameConstants.getNumStudentsOnCloud(); }

    /**
     *This method put a single student on a tile as long as the cloud can contain it
     * @param color Color of the student that is added on the tile
     */
    public void fill(Color color){
        if(color == null)
            throw new NullPointerException();
        else if (isFillable() && cloudStud.isAddable(color)) //have to check the total number of student on a tile
            cloudStud.add(color);
    }

    //we can obtain students from a tile iff the tile is full, otherwise unchanged

    /**
     * Getter method for the sets of students on a tile, thus it removes them from a tile.
     * Students are obtainable from a tile iff the tile is full,
     * otherwise the sets of students is unchanged
     * @return Sets of students on a tile
     * @throws IllegalStateException when a tile is not full of students
     */
    public StudentsHandler getTile(){
        if(isEmpty() || isFillable())
            throw new IllegalStateException();

        StudentsHandler temp = new StudentsHandler(gameConstants.getNumStudentsOnCloud());
        for(Color color: Color.values()){
            while(cloudStud.numStudents(color) > 0){
                temp.add(color);
                cloudStud.remove(color);
            }
        }
        return temp;
    }

    /**
     * @return returns the color of students that are on a cloud tile
     */
    public ArrayList<Color> colorsAvailable() {
        return cloudStud.colorsAvailable();
    }
}
