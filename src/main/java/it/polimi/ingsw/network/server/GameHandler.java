package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.constantFactory.ThreePlayersConstants;
import it.polimi.ingsw.model.constantFactory.TwoPlayersConstants;
import it.polimi.ingsw.model.expertGame.*;
import it.polimi.ingsw.network.client.messages.*;
import it.polimi.ingsw.network.server.exception.GameDisconnectionException;
import it.polimi.ingsw.network.server.exception.SetupGameDisconnectionException;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the controller of the game, handles all the messages from the players, sending messages and request for any object
 * It also handles the various phases of the game, the wrong input of the parameters and the endgame condition
 * @author Lorenzo Corrado
 */
public class GameHandler implements PropertyChangeListener {
    private final MultiServer server;
    private final ArrayList<ServerClientHandler> playersConnections;//list of the sockets

    //this two maps connect the Player object to their client
    private final Map<ServerClientHandler, Player> clientToPlayer;
    private final Map<Player, ServerClientHandler> playerToClient;

    private final int numPlayer;//number of players in the game
    private final Game game; //reference to the model
    private boolean expertGame; //the mode of the game

    private volatile boolean endGameInRound;// true if this game end at the end of a round
    private volatile boolean continueGame;//false if this game end now


    /**
     * This is the standard constructor of GameHandler
     * @param numPlayer is the number of player in the game
     * @param expertGame is the game mode
     * @param playersConnections the reference to the connected players
     */
    public GameHandler(int numPlayer, boolean expertGame, ArrayList<ServerClientHandler> playersConnections, MultiServer server) {
        this.server = server;
        this.numPlayer = numPlayer;
        this.playersConnections = playersConnections;
        this.expertGame = expertGame;
        if(!expertGame) {
            game = new Game(playersConnections.get(0).getNickname(), numPlayer);
        }else
            game = new ExpertGame(playersConnections.get(0).getNickname(), numPlayer);

        game.addListener(this);

        clientToPlayer = new HashMap<>();
        playerToClient = new HashMap<>();

        continueGame = true;
        endGameInRound = false;
    }

    /**
     * This method is invoked when a player wins the game
     * @param evt type of victory associated to an event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("instantWinning")){
            String winner = (String) evt.getNewValue();
            try {
                continueGame = false;
                notifyWinner(winner);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(evt.getPropertyName().equals("endRoundWinning")){
            endGameInRound = true;
        }
    }

    /**
     * @return the reference to this game object
     */
    public Game getGame() {
        return game;
    }

    /**
     * This constructor is used for restarting an already started game
     * @param restartedGame game to be restarted
     * @param playersConnections list of client handler that belongs to that game
     * @param server server instance
     */
    public GameHandler(Game restartedGame, ArrayList<ServerClientHandler> playersConnections, MultiServer server){
        this.server = server;
        this.game = restartedGame;
        this.playersConnections = playersConnections;
        this.numPlayer = game.getNumPlayers();
        clientToPlayer = new HashMap<>();
        playerToClient = new HashMap<>();

        for(int i=0; i < numPlayer; i++){
            clientToPlayer.put(playersConnections.get(i), game.getPlayers().get(i));
        }

        for(int i=0; i < numPlayer; i++){
            playerToClient.put(game.getPlayers().get(i), playersConnections.get(i));
        }
        //WARNING: playersConnections should have the same order as the arraylist of players saved in the game
    }

    /**
     * This method is used to unregister all the player of this game in the server, so a new player can choose his nickname
     */
    private void unregisterPlayersFromServer(){
        for(String player: getNicknamePlayers()){
            server.unregisterPlayer(player);
        }
    }

    /**
     * Helper method used to get a list of nickname of the players in this game
     * @return arrayList of nicknames of players in this game
     */
    public ArrayList<String> getNicknamePlayers(){
        ArrayList<String> nickNamePlayers = new ArrayList<>();
        for(ServerClientHandler client: playersConnections){
            nickNamePlayers.add(client.getNickname());
        }
        return nickNamePlayers;
    }

    /**
     * This method handles the first phase after all the players
     * are connected, makes every player choose a Card Back anda Tower.
     * Then it starts the real game
     * @see ServerClientHandler for exceptions
     */
    public synchronized void setup() throws IOException, ClassNotFoundException, SetupGameDisconnectionException, GameDisconnectionException {

        for(int i=1; i<numPlayer; i++){
            game.addPlayer(playersConnections.get(i).getNickname());
        }

        for(int i=0; i<numPlayer; i++){
            clientToPlayer.put(playersConnections.get(i), game.getPlayers().get(i));
        }

        for(int i=0; i<numPlayer; i++){
            playerToClient.put(game.getPlayers().get(i), playersConnections.get(i));
        }

        game.startGame();

        // If a player logs out while choosing the tower or the back of the card,
        // that game lobby is cleared and players will be forced to log into the server.
        try {
            for (ServerClientHandler client : playersConnections) {
                askCardsBackSetup(client);
                askColorsSetup(client);
            }
        }catch (SocketTimeoutException e){
            broadcastMessage("A player has disconnected. Closing this game...");
            broadcastMessage("Please login another time on the server to play.");
            broadcastShutDown();
            unregisterPlayersFromServer();

            throw new SetupGameDisconnectionException();
        }

        game.setGameState(GameState.PLANNING_STATE);


        // If a player disconnects after logging into the server, he is kept on the server
        // and the game reconnection policy is initiated.
        try {
            gameTurns();
        }catch (GameDisconnectionException e){//save the game
            broadcastMessage("A player has disconnected. Closing this game...");
            broadcastMessage("Please reconnect to restart this game!");
            broadcastShutDown();
            throw new GameDisconnectionException();
        }
    }

    /**
     * This method is used to send a message in broadcast to all the players connected to this game handler
     * @param message message to be sent
     */
    private void broadcastMessage(String message) throws IOException {
        for (ServerClientHandler client : playersConnections)
            client.sendMessageToClient(message);
    }

    /**
     * This method is used to send a message of shutdown to the client, so that it will terminate
     */
    private void broadcastShutDown() throws IOException {
        for (ServerClientHandler client : playersConnections)
            client.sendShutDownToClient();
    }

    /**
     * Helper method that helps with the choice of the Tower Color
     * It will ask the player to send a message until a correct message with correct parameters is sent
     * Needs a ColorChosen type of message
     * @param client the current player
     * @see ServerClientHandler for exceptions
     */
    private synchronized void askColorsSetup(ServerClientHandler client) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Select the preferred tower color");
        client.sendMessageToClient("The available tower colors are: ");

        ArrayList<String> towerColors = new ArrayList<>();
        for(int i=0; i<game.getAvailableTowerColor().size(); i++){
            //client.sendMessageToClient(game.getAvailableTowerColor().get(i).name());
            towerColors.add(game.getAvailableTowerColor().get(i).name());
        }
        client.sendMessageToClient(towerColors.toString());
        waitForColorsSetup(client);
    }

    /**
     * This method waits for the player to chose a tower color
     * @param client that ask for the client color
     * @see ServerClientHandler for exceptions
     */
    private synchronized void waitForColorsSetup(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean towerChosen = false;
        Object message;
        Tower color;
        while(!towerChosen){
            message = client.readMessageFromClient();
            if(message instanceof ChooseTowerColor && game.getGameState()==GameState.JOIN_STATE){
                color = ((ChooseTowerColor) message).getColor();
                if(game.getAvailableTowerColor().contains(color)){
                    game.associatePlayerToTower(color, clientToPlayer.get(client));
                    client.sendMessageToClient("Your color of tower is " + color.name());
                    towerChosen = true;
                }
                else{
                    client.sendMessageToClient("The selected tower color is not available");
                }

            }
            else{
                client.sendMessageToClient("Command not inserted, please insert a valid command");
            }
        }
    }
    /**
     * Helper method that helps with the choice of the Card Back
     * It will ask the player to send a message until a correct message with correct parameters is sent
     * Needs a ColorChosen type of message
     * @param client the current player
     * @see ServerClientHandler for exceptions
     */
    private synchronized void askCardsBackSetup(ServerClientHandler client) throws IOException , ClassNotFoundException {
        client.sendMessageToClient("Insert the preferred card back");
        client.sendMessageToClient("The available card backs are: ");

        ArrayList<String> backs = new ArrayList<>();
        for(int i = 0; i<game.getAvailableCardsBack().size(); i++){
            //client.sendMessageToClient(game.getAvailableCardsBack().get(i).name());
            backs.add(game.getAvailableCardsBack().get(i).name());
        }
        client.sendMessageToClient(backs.toString());
        waitForCardBackAnswer(client);
    }

    /**
     * This method ask the player for a Card Back during the setup phase
     * You can't chose a card back selected from another player
     * @param client that send the card back
     * @see ServerClientHandler for exceptions
     */
    private synchronized void waitForCardBackAnswer(ServerClientHandler client) throws IOException , ClassNotFoundException{
        boolean backChosen = false;
        Message message = null;
        CardBack card;
        while(!backChosen){
            try {
                message = client.readMessageFromClient();

            }catch (StreamCorruptedException e){
                System.out.println(e.getMessage());
            }
            if(message instanceof ChooseCardBack && game.getGameState() == GameState.JOIN_STATE){
                card = ((ChooseCardBack) message).getMessage();
                //card = CardBack.valueOf(((ChooseCardBack)message).getMessage());
                if(game.getAvailableCardsBack().contains(card)) {
                    game.associatePlayerToCardsToBack(card, clientToPlayer.get(client));
                    client.sendMessageToClient("Your character is " + card.name());
                    backChosen = true;
                }
                else{
                    client.sendMessageToClient("Card already selected, please select another card");
                }
            }
            else
            {
                client.sendMessageToClient("Command not inserted, please insert a valid command");
            }
        }
    }

    /**This method handles all the phases of the game, switching turns and rounds until the game ends (see istant winning)
     * or until the variable endgame is switched! (it waits for the end of the turn
     * @see ServerClientHandler for exceptions
     * @throws GameDisconnectionException
     */
    public  synchronized void gameTurns() throws IOException, ClassNotFoundException, GameDisconnectionException {
        while(!endGameInRound && continueGame){
            try{
            planningPhase();
            actionPhase();
            }catch(SocketTimeoutException e){//start the mechanism to save the game
                broadcastMessage("A player has disconnected. Closing this game...");
                broadcastMessage("Please reconnect to restart this game!");
                broadcastShutDown();
                throw new GameDisconnectionException();
            }
        }
        if(endGameInRound)
            notifyWinner();//winning at the end of a round

        unregisterPlayersFromServer();
    }

    /**
     * This method is used to notify the players about the winner
     */
    private void notifyWinner() throws IOException {
        String winner = game.alternativeWinner();
        broadcastMessage(winner + " has won!");
        broadcastShutDown();
    }

    /**
     * This method is used to notify the players about the winner
     * @param winner nickname of the winner
     */
    private void notifyWinner(String winner) throws IOException {
        broadcastMessage(winner + " has won!");
        broadcastShutDown();
    }


    /**
     * This method handles the planning phase, letting each player, turn by turn, playing his card
     * The player with lower priority will be the first to start the action phase
     * @see ServerClientHandler for exceptions
     */
    private synchronized void planningPhase() throws IOException, ClassNotFoundException{
        Message message;
        ServerClientHandler client;
        ArrayList<Integer> cardsPlayed = new ArrayList<>();
        while(game.getGameState() == GameState.PLANNING_STATE && continueGame){
            client = playerToClient.get(game.getCurrentPlayer());
            client.sendMessageToClient("Please select which assistant card do you wanna play");
            client.sendMessageToClient("The remaining assistant cards are:");

            ArrayList<String> hand = new ArrayList<>();
            for(AssistantCard card : game.getCurrentPlayer().getHand()){
                hand.add(String.valueOf(card.getPriority()));
            }
            client.sendMessageToClient(hand.toString());
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState() == GameState.PLANNING_STATE){
                if(game.getCurrentPlayer().isPriorityAvailable(((IntegerMessage) message).getMessage()) &&
                        (!cardsPlayed.contains(((IntegerMessage) message).getMessage()) || cardsPlayed.containsAll(hand))){
                    int index = game.getCurrentPlayer().priorityToIndex(((IntegerMessage) message).getMessage());
                    game.playCard(index);
                    client.sendMessageToClient("You have chosen your " + ((IntegerMessage) message).getMessage() + " card");
                    cardsPlayed.add(((IntegerMessage) message).getMessage());
                }
                else if(!game.getCurrentPlayer().isPriorityAvailable(((IntegerMessage) message).getMessage())){
                    client.sendMessageToClient("You've already played this card! Play another one!");
                }
                else{
                    client.sendMessageToClient("This card has already been played by another player!");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, please insert a valid command");
            }
        }

    }

    /**
     * This method handles the action phase, using 3 submethods to handle all the turn changing
     * @see ServerClientHandler for exceptions
     */
    private synchronized void actionPhase() throws IOException, ClassNotFoundException{
        boolean areCloudsEmpty = false;
        for(CloudTile cloud : game.getCloudTiles()){ //if at least one cloud is not filled (bag is empty), skip takecloud
            if(cloud.isFillable()){
                areCloudsEmpty = true;
            }
        }
        while(game.getGameState() != GameState.PLANNING_STATE && continueGame){
            ServerClientHandler client = playerToClient.get(game.getCurrentPlayer());
            client.sendMessageToClient("It's your turn!");
            moveStudents(client);
            motherMovement(client);
            if(!areCloudsEmpty && continueGame) //to avoid problem caused to end game
                takeCloud(client);
        }
    }

    /**
     * This method permits the player to play 3 moves between moving the students
     * from entrance to hall, or entrance to island, for a limited number of time (depends on the number of players)
     * Is also possible to play a card in this phase
     * @param client to send the messages
     * @see ServerClientHandler for exceptions
     */
    private synchronized void moveStudents(ServerClientHandler client) throws IOException, ClassNotFoundException{
        int numberOfMoves = numPlayer == 3 ? new ThreePlayersConstants().getMaxNumStudMovements() : new TwoPlayersConstants().getMaxNumStudMovements();
        Message message;
        drawBoard(client, game.getCurrentPlayer());
        drawArchipelago(client);
        drawExpertCards(client);
        for(int i=0; i<numberOfMoves; i++){
            boolean correctMove = false;
            client.sendMessageToClient("Select where you want to move your students[\"hall/island\"]");
            while(!correctMove){
                message = client.readMessageFromClient();
                if(message instanceof MoveStudentMessage && game.getGameState() == GameState.MOVING_STUDENT_STATE) {
                    String command = ((MoveStudentMessage) message).getMsg().toUpperCase();
                    if (( command.equals("HALL"))){
                        availableEntranceColor(client);
                        toHall(client);
                    } else{
                        availableEntranceColor(client);
                        toIsland(client);
                    }
                    correctMove = true;
                }
                else if(message instanceof PlayExpertCard && expertGame){
                    if(!((ExpertGame) game).isCardHasBeenPlayed()) {
                        correctMove = playCard(client);
                        if(correctMove)
                            i--;
                    }
                    else{
                        client.sendMessageToClient("You have already played a card this turn!");
                    }
                }
                else if(message instanceof PlayExpertCard){
                    client.sendMessageToClient("Not in an expert game");
                }
                else
                {
                    client.sendMessageToClient("Wrong command, select Hall or Island");
                }
            }
        }
        drawArchipelago(client);
        game.setGameState(GameState.MOTHER_MOVEMENT_STATE);
    }

    /**
     * This method draws the available entrance colors
     * @see ServerClientHandler for exceptions
     */
    private void availableEntranceColor(ServerClientHandler client) throws IOException{
        client.sendMessageToClient("These are the available colors: ");

        ArrayList<String> colors = new ArrayList<>();
        for(Color color : game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable()){
            colors.add(color.name());
        }
        client.sendMessageToClient(colors.toString());
        client.sendMessageToClient("Please select one of these colors.");
    }

    /**
     * In this method the player chose the color of the player to move in the hall
     * @param client that moves his students in the hall
     * @see ServerClientHandler for exceptions
     */
    private void toHall(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean isColorChosen = false;
        Message message;
        while(!isColorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen && game.getGameState()==GameState.MOVING_STUDENT_STATE){
                if(game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable().contains(((ColorChosen) message).getColor())
                && game.getCurrentPlayer().getBoard().hallIsFillable(((ColorChosen) message).getColor())){
                    game.entranceToHall(((ColorChosen) message).getColor());
                    client.sendMessageToClient("You have placed a " + ((ColorChosen) message).getColor().name().toLowerCase()
                            + " student in the hall");
                    isColorChosen = true;
                }
                else{
                    client.sendMessageToClient("Color not available, please select another color."); //TODO another custom message for the hall
                }
            }
            else{
                client.sendMessageToClient("Wrong command, please insert the color you want to move");
            }

        }

    }
    public void toIsland(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean isColorChosen = false;
        Message message;
        while(!isColorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen && game.getGameState()==GameState.MOVING_STUDENT_STATE){
                if(game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable().contains(((ColorChosen) message).getColor())){
                    client.sendMessageToClient("Color selected " + ((ColorChosen) message).getColor().name());
                    client.sendMessageToClient("Select the island where you want to place your student.");
                    client.sendMessageToClient("There are " + game.getArchipelago().size() + " islands.");
                    islandSelection(client, ((ColorChosen) message).getColor());
                    isColorChosen = true;
                }
                else{
                    client.sendMessageToClient("Color not available, please select another color.");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, please insert the color you want to move");
            }

        }
    }

    private void islandSelection(ServerClientHandler client, Color color) throws IOException, ClassNotFoundException{
        boolean isIdxChosen = false;
        Message message;
        while(!isIdxChosen){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState()==GameState.MOVING_STUDENT_STATE){
                if(((IntegerMessage) message).getMessage() <= game.getArchipelago().size() && ((IntegerMessage) message).getMessage() >0){
                    game.entranceToIsland(((IntegerMessage) message).getMessage() -1, color);
                    client.sendMessageToClient("You have placed a " + color.name()
                            + " student on the island number " + ((IntegerMessage) message).getMessage());
                    isIdxChosen = true;
                }
                else{
                    client.sendMessageToClient("This island doesn't exists, please select another island.");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, select the idx of the island");
            }
        }
    }
    private void motherMovement(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean isIdxChosen = false;
        Message message;
        client.sendMessageToClient("Move mother nature. You can travel " + game.getMaxMovement() + " islands.");
        client.sendMessageToClient("Now she is on the island number " + game.getMotherNature());

        client.sendMessageToClient("Choose the number of islands you want to travel.");
        while(!isIdxChosen){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState()==GameState.MOTHER_MOVEMENT_STATE){
                int step = ((IntegerMessage)message).getMessage();
                if(step <= game.getArchipelago().size() && step > 0 && step <= game.getMaxMovement()){
                    client.sendMessageToClient("Mother nature will travel " + ((IntegerMessage) message).getMessage() + " islands.");
                    game.motherMovement(step);
                    drawBoard(client, game.getCurrentPlayer());
                    drawArchipelago(client);
                    isIdxChosen = true;
                }
                else if(message instanceof PlayExpertCard && expertGame){
                    if(!((ExpertGame) game).isCardHasBeenPlayed()) {
                        playCard(client);
                    }
                    else{
                        client.sendMessageToClient("You have already played a card this turn!");
                    }
                }
                else{
                    client.sendMessageToClient("Please select a valid number of steps.");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, please insert the number of islands you want to travel");
            }
        }
    }

    private void takeCloud(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean cloudTaken = false;
        Message message;
        client.sendMessageToClient("Select one of the clouds: ");

        Map<Color, Integer> students = new LinkedHashMap<>();
        int cloudIdx = 0;
        for(int i=0; i<game.getCloudTiles().size(); i++){
            cloudIdx++;
            students.clear();
            if(!game.getCloudTiles().get(i).isEmpty()){
                for(Color color: game.getCloudTiles().get(i).colorsAvailable()){
                    students.put(color, game.getCloudTiles().get(i).numStudOn(color));
                }
                client.sendMessageToClient("Cloud " + cloudIdx + " " + students);
            }
        }
        while(!cloudTaken){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState()==GameState.CLOUD_TO_ENTRANCE_STATE){
                int temp = ((IntegerMessage) message).getMessage();
                if(temp > 0 && temp<= numPlayer &&  !game.getCloudTiles().get(temp-1).isEmpty()){
                    game.cloudToBoard(temp - 1);
                    client.sendMessageToClient("You've chosen the cloud number " + temp);
                    cloudTaken = true;
                }
                else if(message instanceof PlayExpertCard && expertGame){
                    if(!((ExpertGame) game).isCardHasBeenPlayed()) {
                        playCard(client);
                    }
                    else{
                        client.sendMessageToClient("You have already played a card this turn!");
                    }
                }
                else{
                    client.sendMessageToClient("Cloud not valid, please insert a new cloud.");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, insert the number of the cloud you want to take.");
            }

        }
    }
    private void drawArchipelago(ServerClientHandler client) throws IOException{
        StringBuilder stringStudents = new StringBuilder(100);
        StringBuilder towerColor = new StringBuilder(100);
        StringBuilder string = new StringBuilder(100);
        StringBuilder mother = new StringBuilder(100);
        int stringCounter = 0;
        int motherCounter = 0;
        int towerCounter = 0;
        int studentsCounter = 0;
        for(IslandTile island : game.getArchipelago()) {

            for(int i=0; i< Math.max(island.getIslandStudents().numStudents()+3, 6); i++){
                string.append("-");
                stringCounter++;
            }


            stringStudents.append("\u001B[0m|");
            studentsCounter++;

                for (int i = 0; i < island.getIslandStudents().numStudents(Color.BLUE); i++) {
                    stringStudents.append("\u001B[34mS");
                    studentsCounter++;
                }
                for (int i = 0; i < island.getIslandStudents().numStudents(Color.YELLOW); i++) {
                    stringStudents.append("\u001B[33mS");
                    studentsCounter++;
                }
                for (int i = 0; i < island.getIslandStudents().numStudents(Color.RED); i++) {
                    stringStudents.append("\u001B[31mS");
                    studentsCounter++;
                }
                for (int i = 0; i < island.getIslandStudents().numStudents(Color.GREEN); i++) {
                    stringStudents.append("\u001B[32mS");
                    studentsCounter++;
                }
                for (int i = 0; i < island.getIslandStudents().numStudents(Color.PINK); i++) {
                    stringStudents.append("\u001B[35mS");
                    studentsCounter++;
                }
                while(stringCounter > studentsCounter){
                    stringStudents.append(" ");
                    studentsCounter++;
                }
                towerColor.append("\u001B[0m|");
                towerCounter++;
                mother.append("\u001B[0m|");
                motherCounter++;
                if(island.getNumTowers()>0) {
                    switch (island.getTowerColor()) {
                        case WHITE -> towerColor.append("\u001B[37m");
                        case BLACK -> towerColor.append("\u001B[4;34m");
                        case GRAY -> towerColor.append("\u001B[38;5;232m");
                    }
                    for(int i=0; i<island.getNumTowers(); i++){
                        towerColor.append("T\u001B[0m");
                        towerCounter++;
                    }
                }

                if(game.getArchipelago().indexOf(island) == game.getMotherNature()){
                    mother.append("o");
                    motherCounter++;
                }
                if(island.getIsBanned()){
                    mother.append("!");
                    motherCounter++;
                }
            while(studentsCounter>towerCounter){
                towerColor.append(" ");
                towerCounter++;
            }
            while(towerCounter>motherCounter){
                mother.append(" ");
                motherCounter++;
            }
            towerColor.append("\u001B[0m|/");
            towerCounter++;
            towerCounter++;
            stringStudents.append("\u001B[0m|/");
            studentsCounter++;
            studentsCounter++;
            mother.append("\u001B[0m|/");
            motherCounter++;
            motherCounter++;

        }
        client.sendMessageToClient(string.toString());
        client.sendMessageToClient(stringStudents.toString());
        client.sendMessageToClient(towerColor.toString());
        client.sendMessageToClient(mother.toString());
        client.sendMessageToClient(string.toString());

    }

    private void drawBoard(ServerClientHandler client, Player player) throws IOException{
        Map<Color, Integer> entranceStudents = new HashMap<>();
        Map<Color, Integer> hallStudents = new HashMap<>();
        Board playerBoard = player.getBoard();
        for(Color color : player.getBoard().getEntrance().colorsAvailable()){
            entranceStudents.put(color, playerBoard.getEntrance().numStudents(color));
        }
        for(Color color : Color.values()){
            hallStudents.put(color, playerBoard.getHall().numStudents(color));
        }
        client.sendMessageToClient("Your board:");
        client.sendMessageToClient("Entrance = " + entranceStudents);
        client.sendMessageToClient("Hall = " + hallStudents);
        if(expertGame) client.sendMessageToClient("Coin: " + playerBoard.getNumCoin());
        client.sendMessageToClient("Professors = " + playerBoard.getProfessors());
        client.sendMessageToClient("Tower Color = " + playerBoard.getTowerColor());
        client.sendMessageToClient("Number of Towers = " + playerBoard.getNumTower());
    }

    private void drawExpertCards(ServerClientHandler client) throws IOException{
        if(expertGame) {
            StringBuilder cards = new StringBuilder();
            cards.append("Expert Cards:");
            for(int i=0; i<3; i++){
                ExpertCard card = game.getExpertCards().get(i);
                if(card instanceof IncrementMaxMovementCard){
                    cards.append("IncrementMaxMov:").append(card.getPrice()).append(", ");
                }
                else if(card instanceof TakeProfessorEqualStudentsCard){
                    cards.append("TakeProfessor:").append(card.getPrice()).append(", ");
                }
                else if(card instanceof SwapStudentsCard card1){
                    cards.append("SwapStudents:").append(card1.getPrice()).append(", ");
                }
                else if(card instanceof  StudentsBufferCardsCluster){
                    Map<Color, Integer> students = new HashMap<>();
                    for(Color color : Color.values()){
                        students.put(color, ((StudentsBufferCardsCluster) card).getStudBuffer().numStudents(color));
                    }
                    if(((StudentsBufferCardsCluster)card).getIndex() == 0)
                        cards.append("ManStudBuffer:").append(students).append(card.getPrice()).append(", ");
                    if(((StudentsBufferCardsCluster)card).getIndex() == 1)
                        cards.append("ClownStudBuffer:").append(students).append(card.getPrice()).append(", ");
                    if(((StudentsBufferCardsCluster)card).getIndex() == 2)
                        cards.append("WomanStudBuffer:").append(students).append(card.getPrice()).append(", ");

                }
                else if (card instanceof PutThreeStudentsInTheBagCard){
                    cards.append("PutThreeStudents:").append(card.getPrice()).append(", ");
                }
                else if (card instanceof  PseudoMotherNatureCard){
                    cards.append("PseudoMother:").append(card.getPrice()).append(", ");
                }
                else if (card instanceof  InfluenceCardsCluster){
                    if(((InfluenceCardsCluster) card).getIndex() == 0)
                        cards.append("NoTower:").append(card.getPrice()).append(", ");
                    if(((InfluenceCardsCluster) card).getIndex() == 1)
                        cards.append("TwoMore:").append(card.getPrice()).append(", ");
                    if(((InfluenceCardsCluster) card).getIndex()== 2)
                        cards.append("NoColor:").append(card.getPrice()).append(", ");

                }
                else if (card instanceof BannedIslandCard){
                    cards.append("BannedIsland:").append(card.getPrice()).append(", ");
                }
            }
            client.sendMessageToClient(cards.toString());
        }
    }
    private boolean playCard(ServerClientHandler client) throws IOException, ClassNotFoundException{
        Message message;
        client.sendMessageToClient("Select the card you want to play!");
        while(true){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage){
                if(((IntegerMessage) message).getMessage()>0 && ((IntegerMessage) message).getMessage()<=3){
                    ArrayList<ExpertCard> cards = game.getExpertCards();
                    ExpertCard card = cards.get(((IntegerMessage) message).getMessage()-1);

                    if(game.getCurrentPlayer().getBoard().getNumCoin() < card.getPrice()){
                        client.sendMessageToClient("You don't have enough coin!");
                        return false;
                    }
                    if(card instanceof IncrementMaxMovementCard || card instanceof TakeProfessorEqualStudentsCard){
                        game.playEffect(((IntegerMessage) message).getMessage()-1);
                    }
                    else if(card instanceof SwapStudentsCard card1){
                        if(game.getCurrentPlayer().getBoard().getHall().numStudents() == 0){
                            client.sendMessageToClient("Your Hall is empty!");
                            return false;
                        }
                        swapStudents(client, card1);
                        game.playVoidEffects(card1);
                    }
                    else if(card instanceof  StudentsBufferCardsCluster card1){
                        int idx = ((StudentsBufferCardsCluster) card).getIndex();
                        if(idx == 0){
                            manStudentCluster(client, card1);
                            game.playEffect(((IntegerMessage) message).getMessage()-1);
                        }
                        else if(idx == 1){
                            swapCardCluster(client, card1);
                            game.playVoidEffects(card1);
                        }
                        else if(idx == 2){
                            askColorStudentsCluster(client, card1);
                            game.playEffect(((IntegerMessage) message).getMessage()-1);
                        }
                    }
                    else if (card instanceof PutThreeStudentsInTheBagCard){
                        putThreeStudentsInBagColor(client, card);
                        game.playEffect(((IntegerMessage) message).getMessage());
                    }
                    else if (card instanceof  PseudoMotherNatureCard){
                        pseudoMotherIslandSelector(client, card);
                        game.playEffect(((IntegerMessage) message).getMessage()-1);
                    }
                    else if (card instanceof  InfluenceCardsCluster card1){
                        int idx = card1.getIndex();
                        if(idx == 0){
                            game.playEffect(((IntegerMessage) message).getMessage()-1);
                        }
                        else if(idx ==1){
                            game.playEffect(((IntegerMessage) message).getMessage()-1);
                        }
                        else if(idx == 2){
                            choseColorInfluenceCalculator(client, card1);
                            game.playEffect(((IntegerMessage) message).getMessage()-1);
                        }
                    }
                    else if (card instanceof BannedIslandCard){
                        if(((ExpertGame)game).getBanTile()<=0){
                            client.sendMessageToClient("There are no ban token remaining");
                            return false;
                        }
                        bannedIslandSelector(client, card);
                        game.playEffect(((IntegerMessage) message).getMessage()-1);
                    }
                    return true;
                }
                else{
                    client.sendMessageToClient("Please select a card from to 1 to 3");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, please select which card you want to play");
            }
        }
    }
    private void swapCardCluster(ServerClientHandler client, StudentsBufferCardsCluster card) throws IOException, ClassNotFoundException {

        Message message;
        Board board = game.getCurrentPlayer().getBoard();
        for(int i=0; i<3; i++){
            client.sendMessageToClient("Please select the color of the student to take in the entrance");
            boolean entranceColor = false;
            while(!entranceColor){
                message = client.readMessageFromClient();
                if(message instanceof ColorChosen){
                    if(board.getEntrance().colorsAvailable().contains(((ColorChosen) message).getColor())){
                        client.sendMessageToClient("You have chosen" + ((ColorChosen) message).getColor().name() + "in entrance");
                        setSwapCardStudentsBuffer(client, card);
                        card.setStudentColorInEntrance(((ColorChosen) message).getColor());
                        card.effect();
                        entranceColor=true;
                    }
                    else{
                        client.sendMessageToClient("Please select one available color");
                    }
                }
                else if(message instanceof StopMessage){
                    client.sendMessageToClient("You've finished to swap your tokens");
                    return;

                }
                else{
                    client.sendMessageToClient("Wrong command, please select a color");
                }

            }
        }

    }
    private void setSwapCardStudentsBuffer(ServerClientHandler client, StudentsBufferCardsCluster card) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Please select the color of the student on the card");
        Message message;
        boolean hallColor = false;
        while(!hallColor){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(card.getStudBuffer().colorsAvailable().contains(((ColorChosen) message).getColor())){
                    card.setStudentColorToBeMoved(((ColorChosen) message).getColor());
                    client.sendMessageToClient("You have selected the" +  ((ColorChosen) message).getColor() + "student");
                    hallColor = true;
                }
                else{
                    client.sendMessageToClient("This color is not available please select another one");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, select a new one");
            }
        }
    }
    private void manStudentCluster(ServerClientHandler client, ExpertCard card) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Please select the color of the student to take");
        Message message;
        boolean colorChosen = false;
        while(!colorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(((StudentsBufferCardsCluster)card).getStudBuffer().colorsAvailable().contains(((ColorChosen) message).getColor())){
                    card.setStudentColorToBeMoved(((ColorChosen) message).getColor());
                    colorChosen = true;
                    islandSelectionManCluster(client, card);

                }
            }
        }
    }
    private void islandSelectionManCluster(ServerClientHandler client, ExpertCard card) throws IOException, ClassNotFoundException{
        Message message;
        boolean choseIsland = false;
        client.sendMessageToClient("Please select an island");
        while(!choseIsland){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage){
                if(((IntegerMessage) message).getMessage()>0 && ((IntegerMessage) message).getMessage()<=game.getArchipelago().size()){
                    card.setIdxChosenIsland(((IntegerMessage) message).getMessage()-1);
                    choseIsland = true;
                }
                else{
                    client.sendMessageToClient("This island not exists, select another island");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, select a correct one");
            }
        }
    }
    private void askColorStudentsCluster(ServerClientHandler client, ExpertCard card) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Please select the color to move to your hall");
        Message message;
        boolean colorChosen = false;
        while(!colorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(((StudentsBufferCardsCluster)card).getStudBuffer().colorsAvailable().contains(((ColorChosen) message).getColor())){
                    card.setStudentColorToBeMoved(((ColorChosen) message).getColor());
                    colorChosen = true;
                }
                else{
                    client.sendMessageToClient("Color not available, select a new one");
                }
            }
            else{
                client.sendMessageToClient("Wrong Command, chose a color");
            }
        }
    }
    private void putThreeStudentsInBagColor(ServerClientHandler client, ExpertCard card) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Please select the color to put in the bag");
        Message message;
        boolean colorChosen = false;
        while(!colorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(((ColorChosen) message).getColor() != null){
                    ((PutThreeStudentsInTheBagCard) card).setStudentColor(((ColorChosen) message).getColor());
                    colorChosen = true;
                }
                else{
                    client.sendMessageToClient("!!!!ALARM!!!!! MALEFIC CLIENT DETECTED!!!!");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, please select a color to put in the bag");
            }

        }
    }
    private void bannedIslandSelector(ServerClientHandler client, ExpertCard card) throws IOException,ClassNotFoundException{
        client.sendMessageToClient("Please select the island where you want to put your ban token");
        Message message;
        boolean idxIsland = false;
        while(!idxIsland){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage){
                if(((IntegerMessage) message).getMessage()>0 && ((IntegerMessage) message).getMessage()<=game.getArchipelago().size()){
                    card.changeIslandIndex(((IntegerMessage) message).getMessage()-1);
                    idxIsland=true;
                }
                else{
                    client.sendMessageToClient("This island does not exists");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, needed the number of island that you want to ban");
            }
        }
    }
    private void pseudoMotherIslandSelector(ServerClientHandler client, ExpertCard card) throws IOException,ClassNotFoundException{
        client.sendMessageToClient("Please select the island where you want to calculate your influence");
        Message message;
        boolean idxIsland = false;
        while(!idxIsland){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage){
                if(((IntegerMessage) message).getMessage()>0 && ((IntegerMessage) message).getMessage()<=game.getArchipelago().size()){
                    card.changeIslandIndex(((IntegerMessage) message).getMessage()-1);
                    idxIsland=true;
                }
                else{
                    client.sendMessageToClient("This island does not exists");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, needed the number of island where you want to calculate influence");
            }
        }
    }
    private void swapStudents(ServerClientHandler client, SwapStudentsCard card) throws IOException, ClassNotFoundException{
        Message message;
        Board board = game.getCurrentPlayer().getBoard();
            for(int count=0; count<2; count++){
                boolean entranceColor = false;
                while(!entranceColor){
                    client.sendMessageToClient("If you want to stop the effect type 'stop'");
                    client.sendMessageToClient("Please select the color of the entrance you want to swap");
                    message = client.readMessageFromClient();
                    if(message instanceof ColorChosen){
                        if(board.getEntrance().colorsAvailable().contains(((ColorChosen) message).getColor())){
                            card.setStudentInEntranceColor(((ColorChosen) message).getColor());
                            setSwapHall(client, card);
                            entranceColor = true;
                            drawBoard(client, game.getCurrentPlayer());
                            card.effect();
                        }
                        else{
                            client.sendMessageToClient("There is no such color in the entrance");
                        }
                    }
                    else if(message instanceof StopMessage){
                        client.sendMessageToClient("You finished swapping the cards");
                        return;
                    }
                    else{
                        client.sendMessageToClient("Wrong command, please select a color");
                    }

                }
            }
    }
    private void setSwapHall(ServerClientHandler client, SwapStudentsCard card) throws IOException, ClassNotFoundException{
        Message message;
        boolean hallColor = false;
        Board board = game.getCurrentPlayer().getBoard();
        while(!hallColor){
            client.sendMessageToClient("Please select a color for the hall to swap");
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(board.getHall().colorsAvailable().contains(((ColorChosen) message).getColor())){
                    card.setStudentInHallColor(((ColorChosen) message).getColor());
                    hallColor = true;
                }
                else{
                    client.sendMessageToClient("Color not available, select another color");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, select a color!");
            }
        }
    }
    private void choseColorInfluenceCalculator(ServerClientHandler client, InfluenceCardsCluster card) throws IOException, ClassNotFoundException{
        boolean choseColor = false;
        Message message;
        client.sendMessageToClient("Please select the color to ignore for the influence calculation");
        while(!choseColor){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(((ColorChosen) message).getColor()!=null) {
                    card.changeColor(((ColorChosen) message).getColor());
                    choseColor = true;
                }
                else{
                    client.sendMessageToClient("!!!!ALARM!!!!! MALEFIC CLIENT DETECTED!!!!");
                }
            }
            else{
                client.sendMessageToClient("Please select a valid color");
            }
        }
    }
    public int getNumPlayer() {
        return numPlayer;
    }

}
