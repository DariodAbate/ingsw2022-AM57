package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.statePattern.InfluenceCalculator;
import it.polimi.ingsw.model.statePattern.StandardCalculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * This subclass of game is instantiated when selecting Expert Mode, it adds the coin and expert cards system
 * @author Lorenzo Corrado
 */
public class ExpertGame extends Game implements Serializable, PseudoMotherNature, IncrementMaxMovement, InfluenceCluster, StudentsBufferCluster, SwapStudents, BannedIsland, PutThreeStudentsInTheBag, TakeProfessorEqualStudents, BanTile{

    private final static int NUMBER_OF_EXPERT_CARDS = 3;
    private int coinBank;
    private ArrayList<ExpertCard> expertCards;
    private int banTile = 4;
    private boolean cardHasBeenPlayed;//one player can play only one card in this turn

    /**
     * This constructor adds coins and expert Cards
     * @param nickPlayer Name of the first player to create the lobby
     * @param numGamePlayers Number of players
     */
    public ExpertGame(String nickPlayer, int numGamePlayers){
        super(nickPlayer, numGamePlayers);
        cardHasBeenPlayed = false;
    }

    @Override
    public void startGame(){
        if(getNumPlayers() < numGamePlayers)
            throw new IllegalStateException("Established number of player has not yet been reached");
        //initialize a round
        initRound();

        //initialize set of island tiles
        initArchipelago();

        //initialize bags
        initBags();

        //put mother nature on casual island
        putMotherNature();
        //put student on island from startBag
        initIslandWithStudent();

        //initialize clouds
        initClouds();

        //fill clouds with students
        bagToClouds();

        //fill entrance for each player's board
        initEntrancePlayers();

        //setGameState(GameState.PLANNING_STATE);

        //determine casually the first player
        initBank();

        pickCards();
    }

    /**
     *
     * @return true if an ExpertCard has been played, false otherwise
     */
    public boolean CardHasBeenPlayed() {
        return cardHasBeenPlayed;
    }

    /**
     * This method initializes the coinBank
     */
    private void initBank(){
        this.coinBank = gameConstants.getMaxCoinSize();
    }

    public int getCoinBank() {
        return coinBank;
    }

    /**
     * This method initializes the cards in the game
     */
    private void pickCards() {
        expertCards = new ArrayList<>();
        ArrayList<Integer> cardsPlaceHolder = new ArrayList<>();
        Random rand = new Random();
        int temp;
        for (int j = 1; j <= 12; j++) {
            cardsPlaceHolder.add(j);
        }
        for(int i=0; i<NUMBER_OF_EXPERT_CARDS; i++){
            temp = rand.nextInt((cardsPlaceHolder.size()));
            switch (cardsPlaceHolder.get(temp)) {
                case 1 -> expertCards.add(new BannedIslandCard(this));
                case 2 -> expertCards.add(new InfluenceCardsCluster(0, this));
                case 3 -> expertCards.add(new InfluenceCardsCluster(1, this));
                case 4 -> expertCards.add(new InfluenceCardsCluster(2, this));
                case 5 -> expertCards.add(new PseudoMotherNatureCard(motherNature, this));
                case 6 -> expertCards.add(new IncrementMaxMovementCard(this));
                case 7 -> expertCards.add(new PutThreeStudentsInTheBagCard(this));
                case 8 -> expertCards.add(new StudentsBufferCardsCluster(0, this));
                case 9 -> expertCards.add(new StudentsBufferCardsCluster(1, this));
                case 10 -> expertCards.add(new StudentsBufferCardsCluster(2, this));
                case 11 -> expertCards.add(new SwapStudentsCard(this));
                case 12 -> expertCards.add(new TakeProfessorEqualStudentsCard(this));
            }
            cardsPlaceHolder.remove(temp);
        }
    }

    /**
     * This method is invoked by the current player to move a single student from its entrance to its hall.
     * When the move is made, if the player does not have the professor of the specified color and has the most students
     * of that color among all the players, then he gets the professor.
     * If a player gains some coins, they will be removed from the coins reserve  as long as it has them. If the coin reserve does not contain enough coins,
     * the player should discard them.
     * @param colorStudentToBeMoved color of the student to be moved
     * @throws IllegalArgumentException when a player does not have a student of the specified color
     * in his board's entrance
     * @throws IllegalStateException when the hall of a board cannot accept a student of the specified color
     */
    public void entranceToHall(Color colorStudentToBeMoved){
        Board currentPlayerBoard = getCurrentPlayer().getBoard();

        if( ! currentPlayerBoard.studentInEntrance(colorStudentToBeMoved))
            throw new IllegalArgumentException("The current player does not have a student for the specified color");

        if(!currentPlayerBoard.hallIsFillable(colorStudentToBeMoved))
            throw new IllegalStateException("The hall cannot accept a student of the specified color");

        int oldNumCoin = currentPlayerBoard.getNumCoin();
        currentPlayerBoard.entranceToHall(colorStudentToBeMoved);
        int newNumCoin = currentPlayerBoard.getNumCoin();

        //coin management
        if(newNumCoin - oldNumCoin > 0){
            if(coinBank > 0)
                --coinBank;
            else
                currentPlayerBoard.removeCoin(1);
        }

        //assignment of the professor
        if(!currentPlayerBoard.hasProfessor(colorStudentToBeMoved) && hasMaxStudents(colorStudentToBeMoved)){
            takeBackProfessor(colorStudentToBeMoved);
            currentPlayerBoard.addProfessor(colorStudentToBeMoved);
        }

    }


    /**
     * This method is called by the controller to activate the effect of a card, identified by an index.
     * it also sets cardHasBeenPlayed to true
     * @param indexCard Represent a character card of the effect to activate
     * @throws IllegalArgumentException When the effect of a card involving a non-existent student is activated
     * @throws IllegalArgumentException If a card corresponding to the provided index does not exist
     * @throws IllegalCallerException If the player who wants to activate a card does not have enough coins to do so
     */
        public void playEffect(int indexCard){
            if(indexCard < 0 || indexCard > NUMBER_OF_EXPERT_CARDS)
                throw new IllegalArgumentException("Such card does not exists");

            int cardCost = expertCards.get(indexCard).getPrice();
            if(getCurrentPlayer().getBoard().hasCoin(cardCost)) {
                getCurrentPlayer().getBoard().removeCoin(cardCost);
                coinBank += cardCost;
                cardHasBeenPlayed = true;
                expertCards.get(indexCard).effect();
            }
            else
                throw new IllegalCallerException("Current player does not have enough coin to activate this card");
    }

    public void playVoidEffects(ExpertCard card){
        if(getCurrentPlayer().getBoard().hasCoin(card.getPrice())) {
            getCurrentPlayer().getBoard().removeCoin(card.getPrice());
            coinBank += card.getPrice();
            cardHasBeenPlayed = true;
        }
        else
            throw new IllegalCallerException("Current player does not have enough coin to activate this card");
    }
    /**
     * This method is used every time a player ends his turn. When the turn is an action, it sets the maximum number
     * of island mother nature can travel and sets cardHasBeenPlayed to false. After this method, motherMovement() can be invoked
     */
    @Override
    public void nextTurn(){
        round.nextTurn();
        if(! round.isPlanning()) {
            setMovesMotherNature();
            cardHasBeenPlayed = false;
        }
        if(round.isPlanning())
            setGameState(GameState.PLANNING_STATE);
        else
            setGameState(GameState.MOVING_STUDENT_STATE);
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
        getCurrentIsland().changeCalculator(new StandardCalculator());
        getCurrentIsland().conquer(players);
        mergeIslandTile();
        if(archipelago.contains(tempIsland)){
            motherNature = archipelago.indexOf(tempIsland);
        }
        //call for end game due to no towers remaining
        checkInstantWinner();
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
     * This method is used to populate the student cluster. If is draw the last student, it notify the controller
     * @return color drown from the bag
     */
    public Color draw(){
        Color colorDrawn = actionBag.draw();
        if(colorDrawn == null)
            propertyChangeSupport.firePropertyChange("emptyBagWinning", "", "notEmpty");
        return colorDrawn;
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
     * @throws IllegalArgumentException when  there is no student of the specified color to move from the entrance
     */
    public void fromClownCardToEntrance(Color colorStudentOnCard, Color colorStudentInEntrance){
        Board currentPlayerBoard = getCurrentPlayer().getBoard();
        if(currentPlayerBoard.studentInEntrance(colorStudentInEntrance))
            currentPlayerBoard.removeStudentFromEntrance(colorStudentInEntrance);
        else
            throw new IllegalArgumentException("Lo studente da muovere non è presente nell'ingresso!");
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

    /**
     * This method represent the action of putting the ban tile of the ban card on a selected island
     * @param islandIndex is the index of the selected island to ban
     */
    @Override
    public void banIsland(int islandIndex) {
        if(banTile > 0)
            getArchipelago().get(islandIndex).setBanned(true, this);
        else
            throw new IllegalStateException("No more ban cards available");
        banTile -= 1;
    }

    /**
     * This method add a ban tile to the general reserve
     */
    @Override
    public void addBanTile() {
        if(banTile < 4)
            banTile += 1;
        else
            throw new IllegalStateException("All the four ban cards are available");
    }

    /**
     * @return the number of tile available in the general reserve
     */
    public int getBanTile() {
        return banTile;
    }

    /**
     * This method represent the card that allows the players to put three students of the specified
     * color from the hall of their board to the bag. If the players doesn't have enough student of the
     * color in the hall they have to put all the students they have in the hall in the bag.
     * @param studentColor is the color chosen by the player that play this effect card
     */
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
    @Override
    protected IslandTile sumOfTwoIsland(IslandTile islandOne, IslandTile islandTwo){
        IslandTile newIsland = new IslandTile(new StandardCalculator());
        //The new Island has the sum of the towers of the previous two islands
        for (int i=0; i< islandTwo.getNumTowers()+ islandOne.getNumTowers(); i++){
            newIsland.addTower();
        }
        newIsland.changeTowerColor(islandOne.getTowerColor());
        //the new island has the total of all students of the previous two islands
        for(Color color : Color.values()){
            for(int i=0; i< islandOne.getInfluenceColor(color)+islandTwo.getInfluenceColor(color); i++){
                newIsland.add(color);
            }
        }
        if(islandOne.getIsBanned() || islandTwo.getIsBanned()) {
            newIsland.setBanned(true, this);
            newIsland.setBanTile(islandOne.getBanTile() + islandTwo.getBanTile());
        }
        return newIsland;
    }
    /**
     * This method allows the current player  to get the professor of a certain color even if it has
     * the same number of students as the player who currently owns that professor
     */
    @Override
    public void setTakeProfessorEqualStudents() {
        this.notAbsoluteMax = true;
    }
    public ArrayList<ExpertCard> getExpertCards(){
        return expertCards;
    }

    public boolean isCardHasBeenPlayed() {
        return cardHasBeenPlayed;
    }
}
