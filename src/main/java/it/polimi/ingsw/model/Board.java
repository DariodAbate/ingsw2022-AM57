package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represent the board, called in italian "Plancia", of each individual player.
 * It contains the towers a player has chosen, the professors earned by him and the students
 * present in the entrance and hall.
 * It also contains
 *
 * @author Dario d'Abate
 */
public class Board {
    private Tower towerColor;
    private int numTower;//actual number of towers
    private StudentsHandler entrance;
    private StudentsHandler hall;
    private Set<Color> professors;
    private GameConstants gameConstants;
    private int coinReserve;
    private boolean[][] addCoinChecker;

    /**
     * Constructor of the class. It can handle games for 2 or 3 players
     * @throws IllegalArgumentException if it is passed a number of player that's neither 2 nor 3
     * @param gameConstants is the object with all the constants in the game, so that Board ignores the number of players
     */
    public Board(GameConstants gameConstants) {
        this.gameConstants = gameConstants;
        professors = new HashSet<>();
        hall = new StudentsHandler(gameConstants.HALL_SIZE);
        entrance = new StudentsHandler(gameConstants.getEntranceSize());
        hall = new StudentsHandler(gameConstants.HALL_SIZE);
        numTower = gameConstants.getNumTowersOnBoard();
        addCoinChecker = new boolean[5][3]; //five colors, 3 checkPoint
        coinReserve = 1; //one coin for each player at the starting
    }

    /**
     *This method is invoked by a player to choose his tower's color
     * @param color It is the color chosen by each individual player
     */
    public void chooseTower(Tower color){
        if(color != null) {
            this.towerColor = color;
        }
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
        if(numTower < gameConstants.getNumTowersOnBoard())
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
    public boolean entranceIsFillable(){return entrance.numStudents() < gameConstants.getEntranceSize(); }

    /**
     *Indicates whether a student of the specified color can be placed in the hall
     * @param color of the student to be added
     * @return true if the hall can be filled with a student of the specified color, false otherwise
     */
    public boolean hallIsFillable(Color color){return hall.numStudents(color) < gameConstants.HALL_SIZE; }

    /**
     * Indicates if there is at least one student of the specified color in the entrance
     * @param color of the students in entrance
     * @return true if in the board's entrance there is at least one student of the specified color, false otherwise
     */
    public boolean studentInEntrance(Color color){return entrance.numStudents(color) > 0;}

    /**
     * If in the entrance there is at least one student of the specified color this method removes it,
     * otherwise the entrance is unchanged
     * @param color of the student to be removed from entrance
     */
    public void removeStudentFromEntrance(Color color){
        if(studentInEntrance(color))
            entrance.remove(color);
    }

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
     * @param color color of the professor
     * @return true if the set of professors contains that one of the specified color
     */
    public boolean hasProfessor( Color color){
        return getProfessors().contains(color);
    }

    /**
     * Getter method that return the set of professor contained in a player's board
     * @return the copy of the set of professors
     */
    public Set<Color> getProfessors (){ return new HashSet<>(professors);} //shallow copy

    /**
     * This method put a single student in the entrance. If that student can not be added, the entrance
     * is unchanged
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
     * entrance and hall are unchanged.
     * It also gives a coin to a player if there are sufficient students in the hall
     * @param studentColor It is the color of the student to be moved from entrance to hall
     */
    public void entranceToHall(Color studentColor){
        if(studentColor != null && entrance.isRemovable(studentColor) && hall.isAddable(studentColor)){
            entrance.remove(studentColor);
            hall.add(studentColor);

            //A coin must be added only the first time that a player owe it
            if(hall.numStudents(studentColor) % 3 == 0){
                if(!addCoinChecker[getRowFromColor(studentColor)][(hall.numStudents(studentColor) / 3) - 1]){
                    addCoinChecker[getRowFromColor(studentColor)][(hall.numStudents(studentColor) / 3) - 1] = true;
                    coinReserve++;
                }
            }
        }
    }

    /**
     * Helper method used in entranceToHall
     * @return index corresponding to a specified color in the board's hall
     */
    private int getRowFromColor(Color color){
        return switch (color) {
            case GREEN -> 0;
            case RED -> 1;
            case YELLOW -> 2;
            case PINK -> 3;
            case BLUE -> 4;
        };
    }

    /**
     * This method moves a student from hall to entrance if the movement can be done, otherwise
     * entrance and hall are unchanged. This method is used in the expert game mode
     * @param studentColor It is the color of the student to be moved from hall to entrance
     */
    public void hallToEntrance(Color studentColor){
        if(studentColor != null && hall.isRemovable(studentColor) && entrance.isAddable(studentColor)){
            hall.remove(studentColor);
            entrance.add(studentColor);
        }
    }

    /**
     * This method put a single student of the specified color in the hall. If that student can not be added, the hall
     * is unchanged. This method is used in the expert game mode
     * @param color Color of the student to be added
     */
    public void fillHall(Color color){
        if(color == null)
            throw new NullPointerException();
        else if (hallIsFillable(color)){
            hall.add(color);
        }
    }



    /**
     * Remove a student of the specified Color form the hall.
     * @param studentColor is the color of the student to be removed
     */
    public void removeStudentFromHall(Color studentColor) {
        hall.remove(studentColor);
    }

    //TODO Add a method that returns the color of the students that the board has both in the entrance and in the hall

    /**
     * Gets the number of coin that a player have
     * @return the number of coin that a player have
     */
    public int getNumCoin() {
        return coinReserve;
    }

    /**
     * @param activationCost Number of coins to spend to activate the effect of an Expert card
     * @return true if the player has enough coins to activate a card, false otherwise
     */
    public boolean hasCoin(int activationCost){return coinReserve - activationCost >= 0;}

    /**
      * If the player has enough coins this method removes the specified number of coins from the reserve, otherwise nothing happens
      * @param numCoins Number of coins to remove from the coin reserve
     */
    public void removeCoin(int numCoins) {
        if(hasCoin(numCoins))
            coinReserve -= numCoins;
    }

    public StudentsHandler getEntrance() {
        return entrance;
    }
}
