package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.statePattern.InfluenceCalculator;

import java.util.ArrayList;

/**
 * This subclass of game is instantiated when selecting Expert Mode, it adds the coin and expert cards system
 * @author Lorenzo Corrado
 */

public class ExpertGame extends Game implements PseudoMotherNature, IncrementMaxMovement, InfluenceCluster, SwapStudents,
                                                BannedIsland, PutThreeStudentsInTheBag{

    private int coinBank;
    private ArrayList<ExpertCard> expertCards;

    /**
     * This constructor adds coins and expert Cards
     * @param nickPlayer Name of the first player to create the lobby
     * @param numGamePlayers Number of players
     */
    public ExpertGame(String nickPlayer, int numGamePlayers){
        super(nickPlayer, numGamePlayers);
        initBank();
    }

    /**
     * This method initializes the coinBank
     */
    private void initBank(){
        this.coinBank = gameConstants.getMaxCoinSize();
    }

    /**
     * This method initializes the cards in the game
     */
    private void pickCards(){
        expertCards = new ArrayList<>();
        //card generation
    }

    /**
     * This method increments the max movement possible for the player by 2
     * Is utilized from Increment Max Movement Card
     */
    @Override
    public void plus2MaxMovement(){
        this.maxMovement = maxMovement+2;
    }

    /**
     * This method merges create a pseudo mother nature for the purpose of conquering and merging the islands
     * @param i The island where is going to be placed the pseudo mother nature
     */
    @Override
    public void independentMerge(int i){
        int tempMotherNature = motherNature;//store the initial motherNature
        IslandTile tempIsland = getCurrentIsland();
        motherNature = i;
        getCurrentIsland().conquer(players);
        mergeIslandTile();
        if(archipelago.contains(tempIsland)){
            motherNature = archipelago.indexOf(tempIsland);
        }
    }

    /**
     * This method implements the changeCalculator of game
     * @param calc the calculator
     */
    @Override
    public void changeCalculator(InfluenceCalculator calc) {
        this.calc = calc;
    }

    /**
     * This method swaps a student from the entrance to the hall and vice versa.
     * @param entranceStudentColor is the color of the student in the entrance that need to be swapped
     * @param hallStudentColor is the color of the student in the hall that need to be swapped.
     * @throws IllegalArgumentException when the selected color student isn't in the entrance or in the hall
     */
    @Override
    public void swapStudents(Color entranceStudentColor, Color hallStudentColor){
        if (getCurrentPlayer().getBoard().entranceSize(entranceStudentColor) == 0)
            throw new IllegalArgumentException("You don't have a student with this color in your entrance");
        if (getCurrentPlayer().getBoard().hallSize(hallStudentColor) == 0)
            throw new IllegalArgumentException("You don't have a student with this color in your hall");
       getCurrentPlayer().getBoard().entranceToHall(entranceStudentColor);
       getCurrentPlayer().getBoard().hallToEntrance(hallStudentColor);
    }

    //TODO method still to be finished
    @Override
    public void banIsland(int islandIndex) {
        getArchipelago().get(islandIndex).setBanned(true);
    }

    @Override
    public void putThreeStudentsInTheBag(Color studentColor){
        for (Player player : players) {
            int studentRemoved = 0;
            if (player.getBoard().hallSize(studentColor) < 3) {
                while (player.getBoard().hallSize(studentColor) != 0) {
                    player.getBoard().removeStudentFromHall(studentColor);
                    studentRemoved += 1;
                }
                actionBag.add(studentColor, studentRemoved);
            } else {
                for (studentRemoved = 0; studentRemoved < 3; studentRemoved ++) {
                    player.getBoard().removeStudentFromHall(studentColor);
                }
                actionBag.add(studentColor,3);
            }
        }
    }
}
