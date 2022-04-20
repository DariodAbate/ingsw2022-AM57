package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.*;
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
public class ExpertGame extends Game implements PseudoMotherNature, IncrementMaxMovement, InfluenceCluster, StudentsBufferCluster, SwapStudents, BannedIsland, PutThreeStudentsInTheBag, TakeProfessorEqualStudents{

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
     * This method is called by the controller to activate the effect of a card, identified by an index
     * @param indexCard represent a character card if the effect to activate
     * @throws NotExistingStudentException when the effect of a card involving a non-existent student is activated
     */
        public void playEffect(int indexCard) throws NotExistingStudentException{
        expertCards.get(indexCard).effect();
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
     * This method is used to populate the student cluster
     * @return color drown from the bag
     */
    public Color draw(){
        return actionBag.draw();
    }


    /**
     * This method is used to move a student from the man's expert card of the student cluster to an
     * existing island. The student is removed inside the student cluster class
     * @param idxChosenIsland index of the island to which the student will be moved
     * @param colorStudentToBeMoved color of the student to be moved
     * @throws IndexOutOfBoundsException when it is passed an index which does not have a
     * corresponding island tile in the archipelago arrayList
     */
    public void fromManCardToIsland(int idxChosenIsland, Color colorStudentToBeMoved){
        if(idxChosenIsland < 0 || idxChosenIsland > archipelago.size())
            throw new IndexOutOfBoundsException("The specified island tile does not exist");

        archipelago.get(idxChosenIsland).add(colorStudentToBeMoved);
    }

    /**
     * This method is used to swap a student from the clown's expert card of the student cluster, with a
     * student in the board's hall of the current player.
     * @param colorStudentOnCard color of the student on the card
     * @param colorStudentInEntrance color of the student in the hall
     * @throws NotExistingStudentException when  there is no student of the specified color to move from the entrance
     */
    public void fromClownCardToEntrance(Color colorStudentOnCard, Color colorStudentInEntrance) throws NotExistingStudentException{
        Board currentPlayerBoard = getCurrentPlayer().getBoard();
        if(currentPlayerBoard.studentInEntrance(colorStudentInEntrance))
            currentPlayerBoard.removeStudentFromEntrance(colorStudentInEntrance);
        else
            throw new NotExistingStudentException("Lo studente da muovere non è presente nell'ingresso!");
        currentPlayerBoard.fillEntrance(colorStudentOnCard);
    }

    /**
     * This method is used to move a student from the woman's expert card of the student cluster to the hall
     * of the current player. The student is removed inside the student cluster class.
     * If the hall cannot contain the student of the specified color, the hall remains unchanged and the student is not removed
     * from the top of the card.
     * @param colorStudentToBeMoved color of the student to be moved
     * @return true if movement has done, false otherwise
     */
    public boolean fromWomanCardToHall( Color colorStudentToBeMoved){
        Board currentPlayerBoard = getCurrentPlayer().getBoard();
        if(currentPlayerBoard.hallIsFillable(colorStudentToBeMoved)){
            currentPlayerBoard.fillHall(colorStudentToBeMoved);
            return true;
        }else
            return false;

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

    //TODO method to be tested
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

    /**
     * This method allows the current player  to get the professor of a certain color even if it has
     * the same number of students as the player who currently owns that professor
     */
    @Override
    public void setTakeProfessorEqualStudents() {
        this.notAbsoluteMax = true;
    }
}
