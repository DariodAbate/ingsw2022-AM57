package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class represent the game itself, because it contains the game's logic.
 * This class contains the players and the other game's object: the bag, mother nature and
 * the sets of islands and clouds.
 *
 * @author Dario d'Abate
 * @author Lorenzo Corrado
 */
public class Game {
    protected GameConstants gameConstants;//contains all the game's constants
    protected ArrayList<Player> players;
    protected final int numGamePlayers; //number of players for a particular game
    protected Bag startBag; //bag used only for the initial distribution of students on the islands
    protected Bag actionBag;//bag used during the game
    protected ArrayList<CloudTile> cloudTiles;
    //private ArrayList<Tower> availableTowerColor; //a player can choose his own tower's color TODO
    protected ArrayList<IslandTile> archipelago;
    protected int motherNature; //motherNature as an index corresponding to an island
    protected int maxMovement; //maxMovement that mother nature can do
    protected RoundStub round; //FIXME
    //using a stub for round
    protected InfluenceCalculator calc; //calculator for the influence

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
     * @param nickPlayer Nickname of the first player that connects to the server
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

        }
        else
            throw new IllegalArgumentException("Illegal parameter for first player");

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

        //choose tower color for each player FIXME
        //Now a player cannot choose
        associatePlayerToTower();

        //choose cardBack for each player FIXME
        // Now a player cannot choose
        associatePlayerToCardBack();

        //fill entrance for each player's board
        initEntrancePlayers();

        //determine casually the first player TODO

    }

    //initialize  a round through which the current player can be selected
    protected void initRound(){round = new RoundStub(players);} //FIXME

    //initialize an archipelago with a standard influence's calculator for each islandTile
    protected void initArchipelago(){
        archipelago = new ArrayList<>();
        for(int i = 0; i < gameConstants.INITIAL_ARCHIPELAGO_SIZE ; i++) {
            //standard calculator for influence
            StandardCalculator influenceCalculator = new StandardCalculator();
            archipelago.add(new IslandTile(influenceCalculator));
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
                if(colorDrawn == null){throw new IllegalStateException("At this stage the bag cannot be empty");/*FIXME*/}
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
    protected void associatePlayerToTower(){
        players.get(0).getBoard().chooseTower(Tower.BLACK);
        players.get(1).getBoard().chooseTower(Tower.WHITE);
        if(numGamePlayers == 3)
            players.get(2).getBoard().chooseTower(Tower.GRAY);
    }
    //in later version a player will be able to choose his own card's back
    protected void associatePlayerToCardBack(){
        players.get(0).chooseBack(CardBack.DRUID);
        players.get(1).chooseBack(CardBack.WITCH);
        if(numGamePlayers == 3)
            players.get(2).chooseBack(CardBack.SAGE);
    }

    protected void initEntrancePlayers(){
        for(Player player: players){
            while(player.getBoard().entranceIsFillable()){
                Color colorDrawn = actionBag.draw();
                if(colorDrawn == null){throw new IllegalStateException("At this stage the bag cannot be empty");/*FIXME*/}
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
        for (CloudTile cloudTile : cloudTiles) {
            if (!cloudTile.isEmpty())
                throw new IllegalStateException("Cloud tiles need to be empty");
        }
        for (CloudTile cloudTile : cloudTiles){
            while (cloudTile.isFillable()) {
                Color colorDrawn = actionBag.draw();
                //check for end game condition TODO
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

    }

    //TO BE TESTED TODO
    //Chang javadoc TODO
    /**
     * This method is invoked by the current player to move a single student from its board to an
     * island tile
     */
    public void entranceToIsland(int idxChosenIsland, Color colorStudentToBeMoved){
        if(idxChosenIsland < 0 || idxChosenIsland > archipelago.size())
            throw new IndexOutOfBoundsException("The specified island tile does not exist");

        Board currentPlayerBoard = getCurrentPlayer().getBoard();
        if( ! currentPlayerBoard.studentInEntrance(colorStudentToBeMoved))
            throw new IllegalStateException("The current player does not have a student for the specified color");
        currentPlayerBoard.removeStudentFromEntrance(colorStudentToBeMoved);
        archipelago.get(idxChosenIsland).add(colorStudentToBeMoved);
        //check conquering condition TODO
    }

    //TO BE TESTED TODO
    /**
     * Causes mother nature to move by as many positions as indicated by the parameter.
     * It also changes the InfluenceCalculator on the island, then it try to conquer the island if possible, and checks
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
        mergeIslandTile();
    }

    /**
     * This method merge two or three adjacent islands with the same towers' color, it starts from the currentIsland
     * and check if it is possible to merge first with the right and then the left island.
     * If there are less than 3 islands in the archipelago it calls endgame()
     */
    public void mergeIslandTile(){
        IslandTile rightIsland = archipelago.get((cyclicNumber(motherNature+1)));
        IslandTile currentIsland = getCurrentIsland();
        if (currentIsland.getTowerColor() == null ){ return;}//First checks if there is a tower on the island
        mergeTwoIsland(rightIsland, currentIsland, AdjacentIslands.RIGHT);//Check the matching color for the right island
        currentIsland = getCurrentIsland();
        if(archipelago.size()<=3){
            //call endgame
            return;
        }
        IslandTile leftIsland = archipelago.get((cyclicNumber(motherNature-1)));
        mergeTwoIsland(leftIsland, currentIsland, AdjacentIslands.LEFT);//Check the matching color for the left island
        if(archipelago.size()<=3){
            //call endgame
        }
    }//TODO endgame call

    /**
     * This is an helper method, it helps to merge two adjacent islands with the same towers' color together
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
            Boolean isNewIslandAdded = false;
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
     * This method is an helper method that transforms an index in an appropriate index for out cyclic array
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
     * This method calls the end of the game due to the finished placement of towers.
     * It returns the name of the winning player
     * @param namePlayer The name of the winning player
     */
    public void endGame(String namePlayer){ //this method ends the game with the winner namePlayer
        //message to controller
    }

    /**
     * This methods checks the winner of the game due to an alternative endgame condition
     * It returns the name of the winning player
     */
    public void endGame(){//in this method its needed to check the winner TODO CHANGE TYPE OF METHOD
        int minTowers=players.get(0).getBoard().getNumTower();
        int index=0;
        int maxProfessors=0;
        ArrayList<Player> tempPlayers = new ArrayList<>();
        for(Player player:players){
            if(minTowers>player.getBoard().getNumTower()){
                minTowers=player.getBoard().getNumTower();
                index= players.indexOf(player);
            }
        }
        tempPlayers.add(players.get(index));
        for(Player player:players){
            if(minTowers==player.getBoard().getNumTower() && index != players.indexOf(player)){
                tempPlayers.add(player);
            }
        }
        if(tempPlayers.size()>1){
            for(Player player: tempPlayers){
                if(maxProfessors < player.getBoard().getProfessors().size()){
                    maxProfessors = player.getBoard().getProfessors().size();
                    index = tempPlayers.indexOf(player);
                }
            }
        }
        //message to controller
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
}
