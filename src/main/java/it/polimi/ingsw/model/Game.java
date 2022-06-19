package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreator;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorThreePlayers;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorTwoPlayers;
import it.polimi.ingsw.model.expertGame.ExpertCard;
import it.polimi.ingsw.model.statePattern.InfluenceCalculator;
import it.polimi.ingsw.model.statePattern.StandardCalculator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class represent the game itself, because it contains the game's logic.
 * This class contains the players and the other game's object: the bag, mother nature and
 * the sets of islands and clouds.
 *
 * @author Dario d'Abate
 * @author Lorenzo Corrado
 */
public class Game implements RoundObserver, RefillInterface, Serializable {
    protected GameConstants gameConstants;//contains all the game's constants
    protected ArrayList<Player> players;
    protected final int numGamePlayers; //number of players for a particular game
    protected Bag startBag; //bag used only for the initial distribution of students on the islands
    protected Bag actionBag;//bag used during the game
    protected ArrayList<CloudTile> cloudTiles;
    private ArrayList<Tower> availableTowerColor; //a player can choose his own tower's color
    protected ArrayList<CardBack> availableCardsBack;
    protected ArrayList<IslandTile> archipelago;
    protected int motherNature; //motherNature as an index corresponding to an island
    protected int maxMovement; //maxMovement that mother nature can do
    protected Round round;

    protected GameState gameState; //state of the game
    protected final int maxNumStudMoves; //maximum number of student movements
    protected int actualNumStudMoves; //number of movements of students that has done

    protected InfluenceCalculator calc; //calculator for the influence
    protected boolean notAbsoluteMax; //flag used to implement an expertCard

    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this); //with this object we will fire the property change event

    /*
    Game creation rules, as indicated by specifications:
    If there are no games in the start phase, a new game is created, otherwise the user
    automatically joins the game in the start phase.
    The player who creates the game chooses the number of players who are part of it.
    The game starts as soon as the expected number of players is reached
     */


    /**
     * Constructor of the class. It initializes the sets of players and add the first player.
     * The first player choose the number of players for a game
     * @param nickPlayer nickname of the first player that connects to the server
     * @param numGamePlayers Number of players for a game
     * @throws IllegalArgumentException if it is passed a number of player that's neither 2 nor 3, or nickname's player is an empty string
     */
    public Game(String nickPlayer, int numGamePlayers){
        if (numGamePlayers == 2 || numGamePlayers == 3 && !nickPlayer.equals("")) {
            initGameConstants(numGamePlayers);
            this.numGamePlayers = numGamePlayers;
            Player p1 = new Player(nickPlayer, gameConstants);
            players = new ArrayList<>();
            players.add(p1);
            this.gameState = GameState.JOIN_STATE; //When the game is initialized, we wait for all the player to join
            this.maxNumStudMoves = gameConstants.getMaxNumStudMovements();
            actualNumStudMoves = 0;
            availableTowerColor = new ArrayList<>();
            Collections.addAll(availableTowerColor,Tower.values());

            availableCardsBack = new ArrayList<>();
            Collections.addAll(availableCardsBack, CardBack.values());

        }
        else
            throw new IllegalArgumentException("Illegal parameter for first player");

    }

    /**
     * This method is used to register a GameHandler object as a listener of this Game object
     * @param pcl GameHandler object
     */
    public void addListener(PropertyChangeListener pcl){
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    //helper method for initializing game constants with a factory pattern
    protected void initGameConstants(int numPlayer){
        GameConstantsCreator gameConstantsCreator;
        if(numPlayer == 2)
            gameConstantsCreator = new GameConstantsCreatorTwoPlayers();
        else
            gameConstantsCreator = new GameConstantsCreatorThreePlayers();
        gameConstants = gameConstantsCreator.create();
    }

    /**
     * Indicates whether a player can join a started game.
     * This method has to be called in the server, for multiple games.
     * @return true if a player can join a game, false otherwise
     */
    public boolean playerCanJoin(){return players.size() < numGamePlayers;}

    /**
     * Appends a player to the end of player's list. This is done if maximum number of player hasn't yet reached,
     * otherwise the list of players is unchanged
     * @param nickPlayer Player's nickname that joins a game
     * @throws IllegalArgumentException if player's nickname is an empty string
     * @throws NullPointerException if player's nickname is null
     */
    public void addPlayer(String nickPlayer){
        if(nickPlayer == null)
            throw new NullPointerException();
        if(nickPlayer.equals(""))
            throw new IllegalArgumentException("Illegal player's nickname");
        if(playerCanJoin()) //redundant check, it has to be inserted in the server class
            players.add(new Player(nickPlayer, gameConstants));

    }

    /**
     * @return player's number that has joined in a game
     */
    public int getNumPlayers(){ return players.size();}

    /**
     * If all players have joined, this method sets up the
     * playing field and initializes each player's game items.
     */
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

        //set planning state

    }

    public void setGameState(GameState state){
        this.gameState = state;
    }
    //initialize  a round through which the current player can be selected
    protected void initRound(){
        round = new Round(players);
        round.setRefillInterface(this);
    }



    //initialize an archipelago with a standard influence's calculator for each islandTile
    protected void initArchipelago(){
        archipelago = new ArrayList<>();
        for(int i = 0; i < gameConstants.INITIAL_ARCHIPELAGO_SIZE ; i++) {
            //standard calculator for influence
            StandardCalculator influenceCalculator = new StandardCalculator();
            archipelago.add(new IslandTile(influenceCalculator));
            influenceCalculator.setContext(archipelago.get(i));
        }
        calc = new StandardCalculator();
    }

    //initializes two bags, one for filling the archipelago and another one
    //for playing
    public void initBags(){
        startBag = new Bag(2); // see game rule
        actionBag = new Bag(gameConstants.MAX_SIZE_STUDENT_FOR_COLOR - 2);
    }

    //returns a pseudorandom, uniformly distributed int value between min (inclusive)
    // and max value (inclusive)
    //Used only for mother nature
    protected int randomNumber(){
        int min = 0;
        int max = 11;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public void setMotherNature(int i){motherNature = i; }
    protected void putMotherNature(){
        motherNature = randomNumber();
    }

    //putting one student on each island Tile, except for that one containing mother nature
    //and the one at its opposite
    protected void initIslandWithStudent(){
        //Index of the island opposite to the one with mother nature
        int idxEmptyIsland = (motherNature + gameConstants.INITIAL_ARCHIPELAGO_SIZE/2) % gameConstants.INITIAL_ARCHIPELAGO_SIZE;
        for(int i = 0; i < gameConstants.INITIAL_ARCHIPELAGO_SIZE ; i++){
            if(i != motherNature && i != idxEmptyIsland){
                Color colorDrawn = startBag.draw();
                if(colorDrawn == null){throw new IllegalStateException("At this stage the bag cannot be empty");}
                archipelago.get(i).add(colorDrawn);
            }
        }
    }

    //initializes the clouds without students
    protected void initClouds(){
        cloudTiles = new ArrayList<>();
        for(int i = 0; i < gameConstants.getNumClouds(); ++i)
            cloudTiles.add(new CloudTile(gameConstants));
    }

    //in later version a player will be able to choose his own tower's color
    public void associatePlayerToCardsToBack(CardBack back, Player player){
        player.chooseBack(back);
        availableCardsBack.remove(back);
    }
    //in later version a player will be able to choose his own card's back
    public void associatePlayerToTower(Tower color, Player player){
        player.getBoard().chooseTower(color);
        availableTowerColor.remove(color);
    }

    protected void initEntrancePlayers(){
        for(Player player: players){
            while(player.getBoard().entranceIsFillable()){
                Color colorDrawn = actionBag.draw();
                if(colorDrawn == null){throw new IllegalStateException("At this stage the bag cannot be empty");}
                player.getBoard().fillEntrance(colorDrawn);
            }
        }
    }


    /**
     * This method is invoked whenever the set of cloud tiles needs to be filled
     * with students.
     * @throws IllegalStateException when there's at least one cloud tile that's not empty
     */
    public void bagToClouds() {

        for (CloudTile cloudTile : cloudTiles){
            while (cloudTile.isFillable()) {
                Color colorDrawn = actionBag.draw();

                if(colorDrawn == null){//last round to play
                    propertyChangeSupport.firePropertyChange("emptyBagWinning", "", "notEmpty");
                    return;
                }
                cloudTile.fill(colorDrawn);
            }
        }
    }

    /**
     * This method is invoked when the current player need to move students from a cloud tile
     * to its board's entrance
     * @param idxChosenCloud is the index of the cloudTile to be emptied
     * @throws IndexOutOfBoundsException when it is passed an index which does not have a
     * corresponding cloud in the cloud arrayList
     * @throws IllegalStateException when a player has not moved all the students that has to move
     * from the entrance
     */
    public void cloudToBoard(int idxChosenCloud){
        if(idxChosenCloud < 0 || idxChosenCloud > gameConstants.getNumClouds())
            throw new IndexOutOfBoundsException("The specified cloud tile does not exist");

        CloudTile cloudTile = cloudTiles.get(idxChosenCloud);
        StudentsHandler tempCloud = cloudTile.getTile();
        Board currentPlayerBoard = getCurrentPlayer().getBoard();

        if(currentPlayerBoard.entranceSize() != gameConstants.getEntranceSize()- gameConstants.getNumStudentsOnCloud())
            throw new IllegalStateException("The current player can still moves students");

        while(currentPlayerBoard.entranceIsFillable()){
            for(Color color: Color.values()){
                while(tempCloud.numStudents(color) > 0){
                    currentPlayerBoard.fillEntrance(color);
                    tempCloud.remove(color);
                }
            }
        }
        nextTurn();

    }

    /**
     * This method is invoked by the current player to move a single student from its entrance to its hall.
     * When the move is made, if the player does not have the professor of the specified color and has the most students
     * of that color among all the players, then he gets the professor
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
        //controlMovementStudents();//one movement of student has done
        currentPlayerBoard.entranceToHall(colorStudentToBeMoved);

        //assignment of the professor
        if(!currentPlayerBoard.hasProfessor(colorStudentToBeMoved) && hasMaxStudents(colorStudentToBeMoved)){
            takeBackProfessor(colorStudentToBeMoved);
            currentPlayerBoard.addProfessor(colorStudentToBeMoved);
        }

    }

    /**
     * Helper method used to determine if the current player has the maximum number of students,
     * of the specified color, in the hall
     * @param color color of student
     * @return true if the current player has the maximum number of students among all players, false otherwise
     */
    protected boolean hasMaxStudents(Color color){
        int idxCurrentPlayer = players.indexOf(getCurrentPlayer());
        int numStudCurrentPlayer = getCurrentPlayer().getBoard().hallSize(color);
        int numStudPlayer;

        for(int i = 0; i < players.size(); i++){
            numStudPlayer = players.get(i).getBoard().hallSize(color);
            //notAbsoluteMax = true  -> expert card activated
            //notAbsoluteMax = false -> expert card not activated
            //remember to set false the flag in the controller after returned false
            if(i != idxCurrentPlayer &&
                    (numStudCurrentPlayer < numStudPlayer && notAbsoluteMax || numStudCurrentPlayer <= numStudPlayer && !notAbsoluteMax))
                return false;
        }
        return true;
    }

    /**
     * Helper method used to move the professor of the specified color on the current player board.
     * It removes the professor from the board of the player who held it up to that moment.
     * If no one had that professor, the boards remain the same
     * @param color color of the professor to be removed
     */
    protected void takeBackProfessor(Color color){
        int idxCurrentPlayer = players.indexOf(getCurrentPlayer());
        Board board;
        for(int i = 0; i < players.size(); i++){
            board = players.get(i).getBoard();
            if(i != idxCurrentPlayer && board.hasProfessor(color))
                board.removeProfessor(color);
        }
    }


    /**
     * This method is invoked by the current player to move a single student from its board to an
     * island tile
     * @param colorStudentToBeMoved color of the student to be moved
     * @param idxChosenIsland index of the island to which the student will be moved
     * @throws IndexOutOfBoundsException when it is passed an index which does not have a
     * corresponding island tile in the archipelago arrayList
     * @throws IllegalArgumentException when a player does not have a student of the specified color
     * in his board's entrance
     */
    public void entranceToIsland(int idxChosenIsland, Color colorStudentToBeMoved){
        if(idxChosenIsland < 0 || idxChosenIsland > archipelago.size())
            throw new IndexOutOfBoundsException("The specified island tile does not exist");

        Board currentPlayerBoard = getCurrentPlayer().getBoard();
        if( ! currentPlayerBoard.studentInEntrance(colorStudentToBeMoved))
            throw new IllegalArgumentException("The current player does not have a student for the specified color");

        //controlMovementStudents();//one movement of student has done
        currentPlayerBoard.removeStudentFromEntrance(colorStudentToBeMoved);
        archipelago.get(idxChosenIsland).add(colorStudentToBeMoved);
    }


    /**
     * Thi method is called when the current player have to play a card. It also sets the maximum
     * number of island mother movement can travel.
     * @param idxCard Index of the card chosen by the player
     * @throws IllegalArgumentException when the index does not correspond to an existing card
     */
    public void playCard(int idxCard){
        try{
            getCurrentPlayer().playCard(idxCard);
            if(getCurrentPlayer().getHand().size() == 0){
                propertyChangeSupport.firePropertyChange("endRoundWinning", "", "notEmpty");
            }
            nextTurn();
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * This method is used every time a player has to move mother nature. It set the maximum
     * number of island mother nature can travel
     */
    protected void setMovesMotherNature(){
        AssistantCard cardPlayed = getCurrentPlayer().viewLastCard();
        setMaxMovement(cardPlayed.getMovement());
    }

    /**
     * This method is used every time a player ends his turn. When the turn is an action, it sets the maximum number
     * of island mother nature can travel. After this method, motherMovement() can be invoked
     */
    public void nextTurn(){
        round.nextTurn();
        if(! round.isPlanning())
            setMovesMotherNature();
        if(round.isPlanning())
            setGameState(GameState.PLANNING_STATE); // after the last turn in the action phase, the game passes to planning state
        else
            setGameState(GameState.MOVING_STUDENT_STATE);// after a player that's not the last  has completed the action phase, the next player should play

    }

    /**
     * Causes mother nature to move by as many positions as indicated by the parameter.
     * It also changes the InfluenceCalculator on the island, then it tries to conquer the island if possible, and checks
     * if it needs to merge the current island with the adjacent ones.
     * @param moves indicates the number of island mother nature has to travel
     * @throws IllegalArgumentException if the parameter is not greater than zero
     */
    public void motherMovement(int moves){
        if(moves <= 0 || moves > maxMovement)
            throw  new IllegalArgumentException("Illegal moves for mother nature");
        motherNature = (motherNature + moves) % archipelago.size();
        archipelago.get(motherNature).changeCalculator(calc);
        archipelago.get(motherNature).conquer(players); //this is the only method that calls conquer()
        checkInstantWinner();//no tower remaining


        mergeIslandTile();

        checkInstantWinner(); //3 group of island

        setGameState(GameState.CLOUD_TO_ENTRANCE_STATE); //after a player has moved mother nature, he must claim a cloud tile
    }


    /**
     * This method notify GameHandler whenever a player win because he ran out of tower or because
     * remains 3 groups of island.
     * To GameHandler is passed the nickname of that player
     */
    protected void checkInstantWinner() {
        Board playerBoard;
        for(Player player: getPlayers()){
            playerBoard = player.getBoard();
            if(playerBoard.getNumTower() <= 0){
                propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "instantWinning","", player.getNickname()));
                return;
            }
        }

        if(archipelago.size() <= 3)
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "instantWinning", "", alternativeWinner()));
    }

    /**
     * This method merge two or three adjacent islands with the same towers' color, it starts from the currentIsland
     * and check if it is possible to merge first with the right and then the left island.
     * If there are 3 unified islands group in the archipelago, the game ends without a winner
     */
    public void mergeIslandTile(){
        IslandTile rightIsland = archipelago.get((cyclicNumber(motherNature+1)));
        IslandTile currentIsland = getCurrentIsland();
        if (currentIsland.getTowerColor() == null ){ return;}//First checks if there is a tower on the island
        mergeTwoIsland(rightIsland, currentIsland, AdjacentIslands.RIGHT);//Check the matching color for the right island
        currentIsland = getCurrentIsland();
        if(archipelago.size()<=3){
            return;
        }
        IslandTile leftIsland = archipelago.get((cyclicNumber(motherNature-1)));
        mergeTwoIsland(leftIsland, currentIsland, AdjacentIslands.LEFT);//Check the matching color for the left island
    }

    /**
     * This is a helper method, it helps to merge two adjacent islands with the same towers' color together
     * reducing the size of the archipelago
     * @param adjacentIsland The island adjacent to the first one
     * @param currentIsland The island with the mother nature on it
     * @param direction The direction where you check the merge
     */
    protected void mergeTwoIsland(IslandTile adjacentIsland, IslandTile currentIsland, AdjacentIslands direction) {
        int temp = direction==AdjacentIslands.RIGHT?1:-1;
        if(currentIsland.getTowerColor() == adjacentIsland.getTowerColor()){
            ArrayList<IslandTile> newArchipelago = new ArrayList<>();
            IslandTile newIsland = sumOfTwoIsland(currentIsland, adjacentIsland);
            boolean isNewIslandAdded = false;
            //create the new archipelago
            for (int i=0; i< archipelago.size(); i++){
                if((motherNature != i && cyclicNumber(motherNature+temp) != i)){
                    newArchipelago.add(archipelago.get(i));
                }
                else if(!isNewIslandAdded)
                {
                    newArchipelago.add(newIsland);
                    isNewIslandAdded = true;
                }
            }
            motherNature = newArchipelago.indexOf(newIsland);
            this.archipelago = newArchipelago;
        }
    }

    /**
     * This method add two islands together creating a new island with the same number of towers and students of the
     * previous two islands
     * @param islandOne first island to be summed
     * @param islandTwo second island to be summed
     * @return The island as a result of the sum
     */
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
        return newIsland;
    }

    /**
     * This method is a helper method that transforms an index in an appropriate index for out cyclic array
     * @param number the number of the index
     * @return the transformed number
     * */
    protected int cyclicNumber(int number){
        if(number>=0){
            number = number % archipelago.size();
        }
        else{
            number = archipelago.size() + number;
        }
        return number;
    }


    /**
     * This method checks the winner of the game due to an alternative endgame condition.
     * Called at the end of the round in which the last student was drawn from the bag or
     * if a player runs out of assistant cards in his hand
     * @return the name of the winning player
     */
    public String alternativeWinner(){
        /*
        The player who built the most towers on the islands wins the game.
        In case of a tie, the player who controls the most professors wins
         */

        int minTowers=players.get(0).getBoard().getNumTower();
        int index = 0;
        int maxProfessors = 0;
        String winner;
        ArrayList<Player> tempPlayers = new ArrayList<>();
        for(Player player:players){
            if(minTowers > player.getBoard().getNumTower()){
                minTowers = player.getBoard().getNumTower();
                index = players.indexOf(player);
            }
        }
        tempPlayers.add(players.get(index));
        winner = tempPlayers.get(0).getNickname();

        for(Player player:players){
            if(minTowers == player.getBoard().getNumTower() && index != players.indexOf(player)){
                tempPlayers.add(player);
            }
        }
        if(tempPlayers.size()>1){
            for(Player player: tempPlayers){
                if(maxProfessors < player.getBoard().getProfessors().size()){
                    maxProfessors = player.getBoard().getProfessors().size();
                    winner = player.getNickname();
                }
            }
        }
        return winner;
    }

    /**
     * @return the references to the island with mother nature on it
     */
    public IslandTile getCurrentIsland(){
        return archipelago.get(motherNature);
    }

    /**
     * @return the references to the current player
     */
    public Player getCurrentPlayer(){ return round.getCurrentPlayer();}

    /**
     * @return the maximum number of island mother nature can travel
     */
    public int getMaxMovement(){return maxMovement;}

    /**
     * Set the maximum number of island mother nature can travel
     * @param maxMovement maximum number of island mother nature can travel
     */
    public void setMaxMovement(int maxMovement){
        this.maxMovement = maxMovement;
    }

    /**
     *
     * @return the island with mother nature on it
     */
    public int getMotherNature(){return motherNature;}

    /**
     * Method used only for testing
     * @return the set of cloud tiles
     */
    public ArrayList<CloudTile> getCloudTiles() {
        return cloudTiles;
    }

    /**
     * Method used only for testing
     * @return the set of island tiles
     */
    public ArrayList<IslandTile> getArchipelago() {
        return archipelago;
    }

    /**
     * Method used only for testing
     * @return the set of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * @return the current player
     */
    @Override
    public Player getRoundPlayer() {
        return round.getCurrentPlayer();
    }

    public GameState getGameState() {
        return gameState;
    }

    public ArrayList<Tower> getAvailableTowerColor() {
        return new ArrayList<>(availableTowerColor);
    }

    public ArrayList<CardBack> getAvailableCardsBack() {
        return new ArrayList<>(availableCardsBack);
    }

    @Override
    public void resetCalc() {
        this.calc = new StandardCalculator();
    }

    public int getActualNumStudMoves() {
        return actualNumStudMoves;
    }

    public void addActualNumStudMoves(){
        actualNumStudMoves++;
    }

    public void removeActualNumStudMoves(){
        actualNumStudMoves--;
    }

    public void setActualNumStudMoves(int actualNumStudMoves) {
        this.actualNumStudMoves = actualNumStudMoves;
    }


    //expertCard methods
    public ArrayList<ExpertCard> getExpertCards(){return new ArrayList<>();}
    public void playEffect(int indexCard){}
    public boolean CardHasBeenPlayed(){return true;}
    public void playVoidEffects(ExpertCard card){}
    public int getCoinBank(){return 0;}
}
