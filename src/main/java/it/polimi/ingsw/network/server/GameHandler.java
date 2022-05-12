package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.constantFactory.ThreePlayersConstants;
import it.polimi.ingsw.model.constantFactory.TwoPlayersConstants;
import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.network.client.messages.*;


import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//all method synchronized
public class GameHandler {
    private final int numPlayer;
    private ArrayList<ServerClientHandler> playersConnections;
    private Game game;
    private Map<ServerClientHandler, Player> clientToPlayer;
    private Map<Player, ServerClientHandler> playerToClient;

    private MultiServer server;

    public GameHandler(int numPlayer, boolean expertGame, ArrayList<ServerClientHandler> playersConnections, MultiServer server) {
        this.server = server;
        this.numPlayer = numPlayer;
        this.playersConnections = playersConnections;
        if(!expertGame) {
            game = new Game(playersConnections.get(0).getNickname(), numPlayer);
        }else
            game = new ExpertGame(playersConnections.get(0).getNickname(), numPlayer);
        clientToPlayer = new HashMap<>();
        playerToClient = new HashMap<>();
    }

    //used only for restarting a match
    //WARNING: playersConnections should have the same order as the arraylist of players saved in the game
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
    }


    /**
     * @return nickname of players in this game
     */
    public ArrayList<String> getNicknamePlayers(){
        ArrayList<String> nickNamePlayers = new ArrayList<>();
        for(ServerClientHandler client: playersConnections){
            nickNamePlayers.add(client.getNickname());
        }
        return nickNamePlayers;
    }

    // Se un giocatore si disconnette mentre sta scegliendo la torre o il retro della carta,
    // viene cancellata quella lobby di gioco e i player saranno costretti a fare il login nel server
    public synchronized void setup() throws IOException, ClassNotFoundException, SetupGameDisconnectionException {

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
        try {
            for (ServerClientHandler client : playersConnections) {
                askCardsBackSetup(client);
                askColorsSetup(client);
            }
        }catch (SocketTimeoutException e){
            broadcastMessage("A player has disconnected. Closing this game...");
            broadcastMessage("Please login another time on the server to play.");
            broadcastShutDown();
            for(String player: getNicknamePlayers()){
                server.unregisterPlayer(player);
            }
            throw new SetupGameDisconnectionException();
        }
        game.setGameState(GameState.PLANNING_STATE);
        gameTurns();
    }
    private void broadcastMessage(String message) throws IOException {
        for (ServerClientHandler client : playersConnections)
            client.sendMessageToClient(message);
    }

    private void broadcastShutDown() throws IOException {
        for (ServerClientHandler client : playersConnections)
            client.sendShutDownToClient();
    }

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

    public  synchronized void gameTurns() throws IOException, ClassNotFoundException{
        boolean endgame = false;
        while(!endgame){
            planningPhase();
            actionPhase();
        }
    }
    private synchronized void planningPhase() throws IOException, ClassNotFoundException{
        Message message;
        ServerClientHandler client;
        ArrayList<Integer> cardsPlayed = new ArrayList<>();
        while(game.getGameState() == GameState.PLANNING_STATE){
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
    private synchronized void actionPhase() throws IOException, ClassNotFoundException{
        while(game.getGameState() != GameState.PLANNING_STATE){
            ServerClientHandler client = playerToClient.get(game.getCurrentPlayer());
            client.sendMessageToClient("It's your turn!");
            moveStudents(client);
            motherMovement(client);
            takeCloud(client);
        }
    }

    private synchronized void moveStudents(ServerClientHandler client) throws IOException, ClassNotFoundException{
        int numberOfMoves = numPlayer == 3 ? new ThreePlayersConstants().getMaxNumStudMovements() : new TwoPlayersConstants().getMaxNumStudMovements();
        Message message;
        drawBoard(client, game.getCurrentPlayer());
        drawArchipelago(client);
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
                else
                {
                    client.sendMessageToClient("Wrong command, select Hall or Island");
                }


            }

        }
        drawArchipelago(client);
        game.setGameState(GameState.MOTHER_MOVEMENT_STATE);
    }
    private void availableEntranceColor(ServerClientHandler client) throws IOException{
        client.sendMessageToClient("These are the available colors: ");

        ArrayList<String> colors = new ArrayList<>();
        for(Color color : game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable()){
            colors.add(color.name());
        }
        client.sendMessageToClient(colors.toString());
        client.sendMessageToClient("Please select one of these colors.");
    }

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

        ArrayList<String> clouds = new ArrayList<>();
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
                else{
                    client.sendMessageToClient("Cloud not valid, please insert a new cloud.");
                }
            }
            else{
                client.sendMessageToClient("Wrong command, insert the number of the cloud you want to take.");
            }

        }
    }
    private void drawArchipelago(ServerClientHandler client) throws IOException, ClassNotFoundException{
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
                }
                for(int i=0; i<island.getNumTowers(); i++){
                    towerColor.append("T\u001B[0m");
                    towerCounter++;
                }
                if(game.getArchipelago().indexOf(island) == game.getMotherNature()){
                    mother.append("o");
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
        client.sendMessageToClient("Professors = " + playerBoard.getProfessors());
        client.sendMessageToClient("Tower Color = " + playerBoard.getTowerColor());
        client.sendMessageToClient("Number of Towers = " + playerBoard.getNumTower());

    }

    public int getNumPlayer() {
        return numPlayer;
    }

}
