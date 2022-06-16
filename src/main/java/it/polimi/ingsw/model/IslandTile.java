package it.polimi.ingsw.model;

import it.polimi.ingsw.model.expertGame.BanTile;
import it.polimi.ingsw.model.statePattern.InfluenceCalculator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the single Islands in the game, with methods that can control their state and handle the conquest
 * of an island after the movement of mother nature.
 *
 * The influence calculator has been handled with a state pattern
 *
 * @author Lorenzo Corrado
 */
public class IslandTile implements Serializable {
    private StudentsHandler islandStudents;
    private int numTowers;
    private Tower towerColor;
    private InfluenceCalculator calc;
    private boolean isBanned = false;
    private int banTile = 0;
    public BanTile banTileAdder;


    /**
     * Constructor of the class, the island starts with 0 towers, and a maximum size students
     * @param calc Specify the calculator
     *
     */
    public IslandTile(InfluenceCalculator calc){
        islandStudents = new StudentsHandler();
        this.calc = calc;
    }

    public int getNumTowers() {
        return numTowers;
    }

    public Tower getTowerColor() {
        if(numTowers <= 0){
            return null;
        }
        return towerColor;
    }

    /**
     * Method used only for testing
     * @return the reference to the studentsHandler contained in the class
     */
    public StudentsHandler getIslandStudents() {
        return islandStudents;
    }

    /**
     * Add one single student to the island
     * @param color the color of the student
     */
    public void add(Color color){
        islandStudents.add(color);
    }

    /**
     * Add one single tower to the island
     */
    public void addTower(){
        numTowers +=1;
    }

    /**
     * Change the color of the towers on the island
     * @param color of the tower that will be set
     * @throws IllegalStateException if there are 0 towers
     */
    public void changeTowerColor(Tower color){
        if(numTowers <= 0){
            throw new IllegalStateException("Need to be at least one tower to call this method");
        }
        towerColor = color;
    }

    public int getInfluenceTower(){
        return numTowers;
    }

    public int getInfluenceColor(Color color){
        return islandStudents.numStudents(color);
    }

    /**
     * Calling the state pattern
     * @param player check influence of that player on the island
     * @return influence as an int
     */
    public int checkInfluence(Player player){
        return calc.checkInfluence(player);
    }

    /**
     * Update state
     * @param calc new state
     */
    public void changeCalculator(InfluenceCalculator calc){
        calc.setContext(this);
        this.calc = calc;
    }

    /**
     * Check the influence for every player on the island, then proceed to change the color of the towers
     * with the color of the player's tower with the maximum influence.
     * The add and removal of the tower from the board to the island is handled by the method
     * @param players The players in Game
     */
    public void conquer(ArrayList<Player> players){
        int max = 0;
        int currentindex = 0;
        int index = 0;
        if (isBanned) {
            banTile -= 1;
            if (banTile == 0) {
                isBanned = false;
            }
            banTileAdder.addBanTile();
            return;
        }

        //If the Island is already controlled we need to get the index of the controlling player
        if(numTowers!=0){
            for (Player player:players) {
                if(player.getBoard().getTowerColor() == towerColor){
                    index = players.indexOf(player);
                    currentindex = players.indexOf(player);
                }
            }
        }

        //Check the maximum value for influence of that island
        for (Player player:players) {
            if(checkInfluence(player)>max){
                max = checkInfluence(player);
                index = players.indexOf(player);
            }
        }
        //Check if one or more players has the same influence
        for (Player player:players) {
            if(index != players.indexOf(player) && checkInfluence(player)==max){
                return;
            }
        }

        //if there are tokens, but the island is not controlled by anyone
        if(numTowers==0){
            towerColor = players.get(index).getBoard().getTowerColor();
            addTower();
            players.get(index).getBoard().decNumTower();
            return;
        }

        //If someone as more influence than the current tower holder, swap towers
        if(towerColor != players.get(index).getBoard().getTowerColor()){
            for(int i=0;i<numTowers;i++) {
                players.get(index).getBoard().decNumTower(); //remove towers from the board of the new player
                players.get(currentindex).getBoard().incNumTower(); //add towers on the board of the previous player
            }
            towerColor = players.get(index).getBoard().getTowerColor();
        }
    }

    public void setBanned(boolean isBanned, BanTile banTileAdder) {
        this.isBanned = isBanned;
        this.banTileAdder = banTileAdder;
        banTile += 1;
    }

    public BanTile getBanTileAdder() {
        return banTileAdder;
    }

    public void setBanTile(int banTile) {
        this.banTile = banTile;
    }

    public boolean  getIsBanned() {
        return this.isBanned;
    }

    public int getBanTile() {
        return this.banTile;
    }
}
