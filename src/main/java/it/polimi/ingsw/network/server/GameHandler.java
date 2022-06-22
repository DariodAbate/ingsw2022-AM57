package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.constantFactory.ThreePlayersConstants;
import it.polimi.ingsw.model.constantFactory.TwoPlayersConstants;
import it.polimi.ingsw.model.expertGame.*;
import it.polimi.ingsw.network.client.view.ExpertCard_ID;
import it.polimi.ingsw.network.client.messages.*;
import it.polimi.ingsw.network.client.modelBean.*;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.BanExpertCardBean;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.StudBufferExpertCardBean;
import it.polimi.ingsw.network.server.answers.*;
import it.polimi.ingsw.network.server.answers.update.*;
import it.polimi.ingsw.network.server.exception.GameDisconnectionException;
import it.polimi.ingsw.network.server.exception.SetupGameDisconnectionException;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private volatile boolean emptyBag;//false if the bag is empty
    private int moveStudentsSteps;//register how many swap are taken in Move Students state


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
        moveStudentsSteps = 0;
        continueGame = true;
        endGameInRound = false;
        emptyBag = false;
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

        expertGame = game instanceof ExpertGame;


        continueGame = true;
        endGameInRound = false;
        emptyBag =  false;
        //WARNING: playersConnections should have the same order as the arraylist of players saved in the game
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
        }else if(evt.getPropertyName().equals("emptyBagWinning")){
            emptyBag = true;
        }
    }

    /**
     * @return the reference to this game object
     */
    public Game getGame() {
        return game;
    }



    /**
     * This method is used to unregister all the player of this game in the server, so a new player can choose his nickname
     */
    private void unregisterPlayersFromServer(){
        for(String player: getNicknamePlayers()){
            server.unregisterPlayer(player);
        }
        server.unregisterPlayerFromReconnection(getNicknamePlayers().get(0));//only one player is needed
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
        }catch (SocketTimeoutException | SocketException e){
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
            sendNickname();// used for the view
            sendGameView();//used for the view
            gameTurns();
        }catch (GameDisconnectionException e){//save the game
            broadcastMessage("A player has disconnected. Closing this game...");
            broadcastMessage("Please reconnect to restart this game!");
            broadcastShutDown();
            throw new GameDisconnectionException();
        }
    }

    /**
     * Helper method used to associate a client with the nickname chosen in the UI class
     */
    private void sendNickname() throws IOException {
        for (ServerClientHandler client : playersConnections) {
                client.sendMessageToClient(new NicknameAnswer(client.getNickname()));
        }
    }

    /**
     * This method is used to send a  string message in broadcast to all the players connected to this game handler
     * @param message message to be sent
     */
    private void broadcastMessage(String message) throws IOException {
        for (ServerClientHandler client : playersConnections) {
            try {
                client.sendMessageToClient(message);
            }catch(SocketException e){
                System.out.println("Client already disconnected, do not need to send message");
            }
        }
    }

    /**
     * This method is used to send an answer message in broadcast to all the players connected to this game handler
     * @param answer answer object that will be sent to all the clients
     */
    private void broadcastMessage(Answer answer) throws IOException{
        for (ServerClientHandler client : playersConnections) {
            try {
                client.sendMessageToClient(answer);
            }catch(SocketException e){
                System.out.println("Client already disconnected, do not need to send message");
            }
        }
    }

    /**
     * This method is used to send a message of shutdown to the client, so that it will terminate
     */
    private void broadcastShutDown() throws IOException {
        for (ServerClientHandler client : playersConnections)
            try {
                client.sendShutDownToClient();
            }catch(SocketException e){
                System.out.println("Client already disconnected, do not need to disconnect manually");
            }
    }

    /**
     * Method used to send the entire game state to the Client, so it can be displayed.
     * This method sends only bean object, that are extracted from the model at the server side
     */
    void sendGameView() throws IOException {
        //parsing and sending
        GameBean gameBean = new GameBean();
        gameBean.setExpertGame(expertGame);

        ArrayList<PlayerBean> playerBeans = new ArrayList<>();
        for (Player player : game.getPlayers()){
            PlayerBean tempPlayer = new PlayerBean();
            tempPlayer.setNickname(player.getNickname());
            tempPlayer.setHand(player.getHand());
            tempPlayer.setPlayedCard(player.viewLastCard());

            tempPlayer.setBoard(copyBoard(player.getBoard()));

            playerBeans.add(tempPlayer);
        }
        gameBean.setPlayers(playerBeans);

        gameBean.setArchipelago(copyArchipelago(game.getArchipelago()));

        gameBean.setMotherNature(game.getMotherNature());

        gameBean.setCloudTiles(copyClouds(game.getCloudTiles()));

        gameBean.setExpertCards(copyExpertCards(game.getExpertCards()));

        gameBean.setBank(game.getCoinBank());

        broadcastMessage(new GameStateAnswer(gameBean));
    }

    /**
     * Helper method used to extract data from the board class
     * @param board board that will be parsed
     * @return bean object with a board's data
     */
    private BoardBean copyBoard(Board board){
        BoardBean tempBoard = new BoardBean();
        tempBoard.setNumCoins(board.getNumCoin());
        tempBoard.setTowerColor(board.getTowerColor());
        tempBoard.setNumTowers(board.getNumTower());
        tempBoard.setProfessors(board.getProfessors());

        HashMap<Color, Integer> tempEntrance = new HashMap<>();
        HashMap<Color, Integer> tempHall = new HashMap<>();
        for(Color color: Color.values()){
            tempEntrance.put(color, board.entranceSize(color));
            tempHall.put(color, board.hallSize(color));
        }
        tempBoard.setEntranceStudent(tempEntrance);
        tempBoard.setHallStudent(tempHall);

        return tempBoard;
    }

    /**
     * Helper method used to extract data from the Archipelago
     * @param archipelago list of Island tiles that will be parsed
     * @return bean object with the archipelago's data
     */
    private ArrayList<IslandBean> copyArchipelago(ArrayList<IslandTile> archipelago){
        ArrayList<IslandBean> islandBeans = new ArrayList<>();
        for(IslandTile islandTile: archipelago){
            IslandBean tempIsland = new IslandBean();
            tempIsland.setBanToken(islandTile.getBanTile());
            tempIsland.setTowerColor(islandTile.getTowerColor());
            tempIsland.setNumTowers(islandTile.getNumTowers());

            HashMap<Color, Integer> tempMap = new HashMap<>();
            for(Color color: Color.values()){
                tempMap.put(color, islandTile.getIslandStudents().numStudents(color));
            }
            tempIsland.setStudents(tempMap);
            islandBeans.add(tempIsland);

        }
        return islandBeans;
    }

    /**
     * Helper method used to extract data from the clouds
     * @param cloudTiles list of cloud tiles that will be parsed
     * @return bean object with the clouds data
     */
    private ArrayList<CloudBean> copyClouds(ArrayList<CloudTile> cloudTiles){
        ArrayList<CloudBean> cloudBeans = new ArrayList<>();
        for(CloudTile cloudTile: cloudTiles){
            CloudBean tempCloud = new CloudBean();
            HashMap<Color, Integer> tempMap = new HashMap<>();
            for(Color color: Color.values()){
                tempMap.put(color, cloudTile.numStudOn(color));
            }
            tempCloud.setStudents(tempMap);
            cloudBeans.add(tempCloud);
        }
        return cloudBeans;
    }

    /**
     * Helper method used to extract data from the Expert Card used in a game
     * @param expertCards list of expert cards  that will be parsed
     * @return bean object with the expert card data
     */
    private ArrayList<ExpertCardBean> copyExpertCards(ArrayList<ExpertCard> expertCards){
        ArrayList<ExpertCardBean> expertCardBeans = new ArrayList<>();
        for(ExpertCard expertCard : expertCards){
            ExpertCardBean tempExpertCard  = associateCard(expertCard);
            tempExpertCard.setPlayed(expertCard.isPlayed());
            expertCardBeans.add(tempExpertCard);
        }
        return expertCardBeans;
    }

    /**
     * Helper method used to associate a card bean object to an expert card object
     * @param expertCard expert card that will be parsed
     * @return bean object with a specific expert card data
     */
    private ExpertCardBean associateCard(ExpertCard expertCard) {
        ExpertCardBean tempExpertCard;
        if(expertCard instanceof BannedIslandCard){
            tempExpertCard = new BanExpertCardBean();
            tempExpertCard.setActivationCost(expertCard.getPrice());
            tempExpertCard.setName(ExpertCard_ID.HEALER);
            ((BanExpertCardBean)tempExpertCard).setNumBanToken(((ExpertGame)game).getBanTile());
            return  tempExpertCard;
        }

        if(expertCard instanceof StudentsBufferCardsCluster) {
            tempExpertCard = new StudBufferExpertCardBean();
            tempExpertCard.setActivationCost(expertCard.getPrice());
            switch (((StudentsBufferCardsCluster) expertCard).getIndex()) {
                case 0 -> tempExpertCard.setName(ExpertCard_ID.MONK);
                case 1 -> tempExpertCard.setName(ExpertCard_ID.JOKER);
                case 2 -> tempExpertCard.setName(ExpertCard_ID.PRINCESS);
            }

            HashMap<Color, Integer> tempStudOnCard = new HashMap<>();
            for (Color color : Color.values()) {
                tempStudOnCard.put(color, ((StudentsBufferCardsCluster) expertCard).getStudBuffer().numStudents(color));
            }
            ((StudBufferExpertCardBean) tempExpertCard).setStudentBuffer(tempStudOnCard);
            return  tempExpertCard;
        }

        tempExpertCard = new ExpertCardBean();
        tempExpertCard.setActivationCost(expertCard.getPrice());

        if(expertCard instanceof IncrementMaxMovementCard){
            tempExpertCard.setName(ExpertCard_ID.DELIVERYMAN);

        }
        else if(expertCard instanceof PseudoMotherNatureCard){
            tempExpertCard.setName(ExpertCard_ID.HERALD);

        }
        else if(expertCard instanceof PutThreeStudentsInTheBagCard){
            tempExpertCard.setName(ExpertCard_ID.MONEYLENDER);

        }
        else if(expertCard instanceof SwapStudentsCard){
            tempExpertCard.setName(ExpertCard_ID.BARD);

        }
        else if(expertCard instanceof TakeProfessorEqualStudentsCard){
            tempExpertCard.setName(ExpertCard_ID.HOST);

        }
        else if(expertCard instanceof InfluenceCardsCluster){
            switch(((InfluenceCardsCluster)expertCard).getIndex()){
                case 0 -> tempExpertCard.setName(ExpertCard_ID.CENTAUR);
                case 1-> tempExpertCard.setName(ExpertCard_ID.KNIGHT);
                case 2-> tempExpertCard.setName(ExpertCard_ID.POISONER);
            }

        }
        return tempExpertCard;
    }

    /**
     * Helper method that helps with the choice of the Tower Color
     * It will ask the player to send a message until a correct message with correct parameters is sent
     * Needs a ColorChosen type of message
     * @param client the current player
     * @see ServerClientHandler for exceptions
     */
    private synchronized void askColorsSetup(ServerClientHandler client) throws IOException, ClassNotFoundException{
        client.sendMessageToClient(new TowerChoiceAnswer(game.getAvailableTowerColor()));
        waitForColorsSetup(client);
    }

    /**
     * This method waits for the player to choose a tower color
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
        client.sendMessageToClient(new CardBackChoiceAnswer(game.getAvailableCardsBack()));
        waitForCardBackAnswer(client);
    }

    /**
     * This method ask the player for a Card Back during the setup phase
     * You can't choose a card back selected from another player
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
                if(game.getAvailableCardsBack().contains(card)) {
                    game.associatePlayerToCardsToBack(card, clientToPlayer.get(client));
                    backChosen = true;
                } else{
                    client.sendMessageToClient("Card already selected, please select another card");
                }
            } else
            {
                client.sendMessageToClient("Command not inserted, please insert a valid command");
            }
        }
    }

    /**This method handles all the phases of the game, switching turns and rounds until the game ends (see instant winning)
     * or until the variable endgame is switched! (it waits for the end of the turn
     * @see ServerClientHandler for exceptions
     */
    public  synchronized void gameTurns() throws IOException, ClassNotFoundException, GameDisconnectionException {
        server.saveGame(this);//saveGame

        while(!endGameInRound && continueGame){
            try{
                if(emptyBag)
                    endGameInRound = true;
            planningPhase();
            actionPhase();
            }catch(SocketTimeoutException | SocketException e){//start the mechanism to save the game
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

        for (ServerClientHandler client : playersConnections) {
            try {
                client.sendMessageToClient(new WinningAnswer(winner));
            }catch(SocketException e){
                System.out.println("Client already disconnected, do not need to send message");
            }
        }

        broadcastShutDown();
    }

    /**
     * This method is used to notify the players about the winner
     * @param winner nickname of the winner
     */
    private void notifyWinner(String winner) throws IOException {

        for (ServerClientHandler client : playersConnections) {
            try {
                client.sendMessageToClient(new WinningAnswer(winner));
            }catch(SocketException e){
                System.out.println("Client already disconnected, do not need to send message");
            }
        }

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
            client.sendMessageToClient("Please select the priority of the card you wanna play");

            ArrayList<Integer> hand = new ArrayList<>();
            for(AssistantCard card : game.getCurrentPlayer().getHand()){
                hand.add(card.getPriority());
            }

            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState() == GameState.PLANNING_STATE){
                Player currentPlayer = game.getCurrentPlayer();

                if(currentPlayer.isPriorityAvailable(((IntegerMessage) message).getMessage()) &&
                        (!cardsPlayed.contains(((IntegerMessage) message).getMessage()) || cardsPlayed.containsAll(hand))){

                    int index = currentPlayer.priorityToIndex(((IntegerMessage) message).getMessage());
                    game.playCard(index);
                    server.saveGame(this);// save game

                    broadcastMessage(new AssistantCardPlayedAnswer(currentPlayer.getNickname(),
                            currentPlayer.getHand(), currentPlayer.viewLastCard()));

                    cardsPlayed.add(((IntegerMessage) message).getMessage());
                } else if(!currentPlayer.isPriorityAvailable(((IntegerMessage) message).getMessage())){
                    client.sendMessageToClient("Not valid priority!");
                } else{
                    client.sendMessageToClient("This card has already been played by another player!");
                }
            } else{
                client.sendMessageToClient("Wrong command, please insert a valid command");
            }
        }

    }

    /**
     * This method handles the action phase, using 3 methods to handle all the turn changing
     * @see ServerClientHandler for exceptions
     */
    private synchronized void actionPhase() throws IOException, ClassNotFoundException{
        boolean areCloudsEmpty = false;

        server.saveGame(this);// save game

        while(game.getGameState() != GameState.PLANNING_STATE && continueGame){
            ServerClientHandler client = playerToClient.get(game.getCurrentPlayer());
            client.sendMessageToClient("It's your turn!");
            moveStudents(client);
            motherMovement(client);
            if(!endGameInRound && continueGame) { //to avoid problem caused by end game
                takeCloud(client);
            } else{
                game.nextTurn();
            }
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

        while(game.getActualNumStudMoves()<numberOfMoves){
            boolean correctMove = false;
            client.sendMessageToClient("Select where you want to move your students[\"hall/island\"]");
            while(!correctMove){
                message = client.readMessageFromClient();
                if(message instanceof MoveStudentMessage && game.getGameState() == GameState.MOVING_STUDENT_STATE) {
                    String command = ((MoveStudentMessage) message).getMsg().toUpperCase();
                    if (( command.equals("HALL"))){
                        client.sendMessageToClient("Please select the color of the student you want to move");
                        toHall(client);
                    } else{
                        client.sendMessageToClient("Please select the color of the student you want to move");
                        toIsland(client);
                    }
                    correctMove = true;
                } else if(message instanceof PlayExpertCard && expertGame){
                    if(!((ExpertGame) game).isCardHasBeenPlayed()) {
                        correctMove = playCard(client);
                        if(correctMove) {
                            game.removeActualNumStudMoves();
                        } else{
                            client.sendMessageToClient("Select where you want to move your students[\"hall/island\"]");
                        }
                    } else{
                        client.sendMessageToClient("You have already played a card this turn!");
                        client.sendMessageToClient("Select where you want to move your students[\"hall/island\"]");
                    }
                } else if(message instanceof PlayExpertCard){
                    client.sendMessageToClient("Not in an expert game");
                    client.sendMessageToClient("Select where you want to move your students[\"hall/island\"]");
                } else {
                    client.sendMessageToClient("Wrong command, select Hall or Island");
                }
            }
            game.addActualNumStudMoves();
        }
        game.setActualNumStudMoves(0);
        game.setGameState(GameState.MOTHER_MOVEMENT_STATE);
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

                    server.saveGame(this);// save game

                    broadcastMessage(new ToHallUpdateAnswer(client.getNickname(), getBoardBeans()));
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
    public void toIsland(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean isColorChosen = false;
        Message message;
        while(!isColorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen && game.getGameState()==GameState.MOVING_STUDENT_STATE){
                if(game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable().contains(((ColorChosen) message).getColor())){
                    client.sendMessageToClient("Select the island where you want to place your student.");
                    islandSelection(client, ((ColorChosen) message).getColor());
                    isColorChosen = true;
                    broadcastMessage(new ToIslandUpdateAnswer(client.getNickname(), copyBoard(game.getCurrentPlayer().getBoard()) , copyArchipelago(game.getArchipelago())));
                } else{
                    client.sendMessageToClient("Color not available, please select another color.");
                }
            } else{
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

                    server.saveGame(this);// save game

                    isIdxChosen = true;
                } else{
                    client.sendMessageToClient("This island doesn't exists, please select another island.");
                }
            } else{
                client.sendMessageToClient("Wrong command, select the idx of the island");
            }
        }
    }

    /**
     * @return all the board beans object parsed from the board objects
     */
    private ArrayList<BoardBean> getBoardBeans(){
        ArrayList<BoardBean> boardBeans = new ArrayList<>();
        for(Player player: game.getPlayers()){
            boardBeans.add(copyBoard(player.getBoard()));
        }
        return  boardBeans;
    }

    private void motherMovement(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean isIdxChosen = false;
        Message message;


        while(!isIdxChosen){
            client.sendMessageToClient("Move mother nature. You can travel " + game.getMaxMovement() + " islands.");
            client.sendMessageToClient("Choose the number of islands you want to travel.");
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState()==GameState.MOTHER_MOVEMENT_STATE){
                int step = ((IntegerMessage)message).getMessage();
                if(step <= game.getArchipelago().size() && step > 0 && step <= game.getMaxMovement()){
                    game.motherMovement(step);

                    server.saveGame(this);// save game

                    //copy of boards
                    ArrayList<BoardBean> boardBeans = getBoardBeans();

                    broadcastMessage(new MotherNatureUpdateAnswer(game.getMotherNature(), boardBeans, copyArchipelago(game.getArchipelago())));

                    //HEALER CARD: refresh the cards
                    ExpertCardUpdateAnswer expertCardUpdateAnswer = new ExpertCardUpdateAnswer();
                    expertCardUpdateAnswer.setUpdatedExpertCards(copyExpertCards(game.getExpertCards()));
                    broadcastMessage(expertCardUpdateAnswer);
                    isIdxChosen = true;
                } else{
                    client.sendMessageToClient("Please select a valid number of steps.");
                }
            } else if(message instanceof PlayExpertCard && expertGame){
                if(!((ExpertGame) game).isCardHasBeenPlayed()) {
                    playCard(client);
                } else{
                    client.sendMessageToClient("You have already played a card this turn!");
                }
            } else{
                client.sendMessageToClient("Wrong command, please insert the number of islands you want to travel");
            }

        }
    }

    private void takeCloud(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean cloudTaken = false;
        Message message;
        client.sendMessageToClient("Select one of the clouds");

        while(!cloudTaken){
            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState()==GameState.CLOUD_TO_ENTRANCE_STATE){
                int temp = ((IntegerMessage) message).getMessage();
                if(temp > 0 && temp<= numPlayer &&  !game.getCloudTiles().get(temp-1).isEmpty()){
                    game.cloudToBoard(temp - 1);

                    server.saveGame(this);// save game

                    ArrayList<BoardBean> boardBeans = getBoardBeans();
                    broadcastMessage(new CloudsUpdateAnswer(boardBeans, copyClouds(game.getCloudTiles())));
                    cloudTaken = true;
                } else{
                    client.sendMessageToClient("Cloud not valid, please insert a new cloud.");
                }
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
                client.sendMessageToClient("Wrong command, insert the number of the cloud you want to take.");
            }

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

                    //this update message is filled according to the effect of a card
                    ExpertCardUpdateAnswer expertCardUpdateAnswer = new ExpertCardUpdateAnswer();

                    if(card instanceof IncrementMaxMovementCard || card instanceof TakeProfessorEqualStudentsCard){ //do not refresh the ui
                        game.playEffect(((IntegerMessage) message).getMessage()-1);
                    }
                    else if(card instanceof SwapStudentsCard card1){//refresh the boards
                        if(game.getCurrentPlayer().getBoard().getHall().numStudents() == 0){
                            client.sendMessageToClient("Your Hall is empty!");
                            return false;
                        }
                        game.playVoidEffects(card1);// refresh te boards in each movement
                        swapStudents(client, card1);

                    }
                    else if(card instanceof  StudentsBufferCardsCluster card1){
                        int idx = ((StudentsBufferCardsCluster) card).getIndex();
                        if(idx == 0){// refresh the archipelago
                            manStudentCluster(client, card1);
                            game.playEffect(((IntegerMessage) message).getMessage()-1);

                            expertCardUpdateAnswer.setUpdatedArchipelago(copyArchipelago(game.getArchipelago()));
                        }
                        else if(idx == 1){// refresh the boards
                            game.playVoidEffects(card1);
                            swapCardCluster(client, card1);//refresh the boards in each movement

                        }
                        else if(idx == 2){// refresh the boards
                            if(!askColorStudentsCluster(client, card1))
                                return false;
                            game.playEffect(((IntegerMessage) message).getMessage()-1);
                            //copy of boards
                            ArrayList<BoardBean> boardBeans = getBoardBeans();
                            expertCardUpdateAnswer.setUpdatedBoards(boardBeans);
                        }
                    }
                    else if (card instanceof PutThreeStudentsInTheBagCard){//refresh the boards
                        putThreeStudentsInBagColor(client, card);
                        game.playEffect(((IntegerMessage) message).getMessage() - 1);

                        //copy of boards
                        ArrayList<BoardBean> boardBeans = getBoardBeans();
                        expertCardUpdateAnswer.setUpdatedBoards(boardBeans);
                    }
                    else if (card instanceof  PseudoMotherNatureCard){//refresh the archipelago and the boards
                        pseudoMotherIslandSelector(client, card);
                        game.playEffect(((IntegerMessage) message).getMessage()-1);

                        //copy of boards
                        ArrayList<BoardBean> boardBeans = getBoardBeans();
                        expertCardUpdateAnswer.setUpdatedBoards(boardBeans);
                        expertCardUpdateAnswer.setUpdatedArchipelago(copyArchipelago(game.getArchipelago()));

                    }
                    else if (card instanceof  InfluenceCardsCluster card1){//do not refresh the ui
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
                    else if (card instanceof BannedIslandCard){//refresh the archipelago
                        if(((ExpertGame)game).getBanTile()<=0){
                            client.sendMessageToClient("There are no ban token remaining");
                            return false;
                        }
                        bannedIslandSelector(client, card);
                        game.playEffect(((IntegerMessage) message).getMessage()-1);

                        expertCardUpdateAnswer.setUpdatedArchipelago(copyArchipelago(game.getArchipelago()));

                    }
                    server.saveGame(this);// save game

                    //refresh the cards
                    expertCardUpdateAnswer.setUpdatedExpertCards(copyExpertCards(game.getExpertCards()));
                    broadcastMessage(expertCardUpdateAnswer);
                    broadcastMessage("A card was activated!");
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
                        setSwapCardStudentsBuffer(client, card);
                        card.setStudentColorInEntrance(((ColorChosen) message).getColor());
                        card.effect();
                        entranceColor=true;

                        //refresh the UI at each swap
                        ExpertCardUpdateAnswer expertCardUpdateAnswer = new ExpertCardUpdateAnswer();

                        //copy of boards
                        ArrayList<BoardBean> boardBeans = getBoardBeans();
                        expertCardUpdateAnswer.setUpdatedBoards(boardBeans);
                        expertCardUpdateAnswer.setUpdatedExpertCards(copyExpertCards(game.getExpertCards()));
                        broadcastMessage(expertCardUpdateAnswer);
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
                else{
                    client.sendMessageToClient("Please select a valid color!");
                }
            }else{
                client.sendMessageToClient("Wrong command, please insert a new command");
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
    private boolean askColorStudentsCluster(ServerClientHandler client, ExpertCard card) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Please select the color to move to your hall");
        Message message;
        boolean colorChosen = false;
        while(!colorChosen){
            message = client.readMessageFromClient();
            if(message instanceof ColorChosen){
                if(!game.getCurrentPlayer().getBoard().getHall().isAddable(((ColorChosen) message).getColor())){
                    client.sendMessageToClient("You can't add more "+ ((ColorChosen) message).getColor() + " students in your hall");
                    return false;
                }
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
        return true;
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
                    ((BannedIslandCard)card).setIslandIndex(((IntegerMessage) message).getMessage()-1);
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
                            card.effect();

                            //refresh the UI at each swap
                            ExpertCardUpdateAnswer expertCardUpdateAnswer = new ExpertCardUpdateAnswer();

                            //copy of boards
                            ArrayList<BoardBean> boardBeans = getBoardBeans();
                            expertCardUpdateAnswer.setUpdatedBoards(boardBeans);
                            expertCardUpdateAnswer.setUpdatedExpertCards(copyExpertCards(game.getExpertCards()));
                            broadcastMessage(expertCardUpdateAnswer);
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
}
