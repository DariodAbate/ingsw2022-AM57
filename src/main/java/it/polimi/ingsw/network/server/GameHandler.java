package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.constantFactory.ThreePlayersConstants;
import it.polimi.ingsw.model.constantFactory.TwoPlayersConstants;
import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.network.client.messages.*;


import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//all method synchronized
public class GameHandler {
    private final int numPlayer;
    private ArrayList<ServerClientHandler> playersConnections;
    private Game game;
    private Map<ServerClientHandler, Player> clientToPlayer;
    private Map<Player, ServerClientHandler> playerToClient;

    public GameHandler(int numPlayer, boolean expertGame, ArrayList<ServerClientHandler> playersConnections) {
        this.numPlayer = numPlayer;
        this.playersConnections = playersConnections;
        if(!expertGame) {
            game = new Game(playersConnections.get(0).getNickname(), numPlayer);
        }else
            game = new ExpertGame(playersConnections.get(0).getNickname(), numPlayer);
        clientToPlayer = new HashMap<>();
        playerToClient = new HashMap<>();
    }

    public synchronized void setup() throws IOException, ClassNotFoundException {

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
        for(ServerClientHandler client : playersConnections){
            askCardsBackSetup(client);
            askColorsSetup(client);
        }
        game.setGameState(GameState.PLANNING_STATE);
        gameTurns();
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

    private synchronized void gameTurns() throws IOException, ClassNotFoundException{
        boolean endgame = false;
        while(!endgame){
            planningPhase();
        }
    }
    private synchronized void planningPhase() throws IOException, ClassNotFoundException{
        Message message;
        while(game.getGameState() == GameState.PLANNING_STATE){
            ServerClientHandler client = playerToClient.get(game.getCurrentPlayer());
            client.sendMessageToClient("Please select which assistant card do you wanna play");
            client.sendMessageToClient("The remaining assistant cards are:");

            ArrayList<String> hand = new ArrayList<>();
            for(AssistantCard card : game.getCurrentPlayer().getHand()){
                hand.add(String.valueOf(card.getPriority()));
            }
            client.sendMessageToClient(hand.toString());


            message = client.readMessageFromClient();
            if(message instanceof IntegerMessage && game.getGameState() == GameState.PLANNING_STATE){
                if(game.getCurrentPlayer().isPriorityAvailable(((IntegerMessage) message).getMessage())) {
                    game.playCard(((IntegerMessage) message).getMessage() - 1);//FIXME
                    client.sendMessageToClient("You have chosen your " + ((IntegerMessage) message).getMessage() + "card");
                }
                else{
                    client.sendMessageToClient("You've already played this card! Play another one!");
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
            //TODO MotherMovement
            //TODO Choose Cloud
        }
    }

    private synchronized void moveStudents(ServerClientHandler client) throws IOException, ClassNotFoundException{
        int numberOfMoves = numPlayer == 3 ? new ThreePlayersConstants().getMaxNumStudMovements() : new TwoPlayersConstants().getMaxNumStudMovements();
        Message message;
        for(int i=0; i<numberOfMoves; i++){
            boolean correctMove = false;
            client.sendMessageToClient("Select where you want to move your students");
            while(!correctMove){
                message = client.readMessageFromClient();
                if(message instanceof EntranceToHall && game.getGameState() == GameState.MOVING_STUDENT_STATE){
                   availableEntranceColor(client);
                   toHall(client);
                   correctMove= true;
                }
                else if(message instanceof EntranceToIsland && game.getGameState() == GameState.MOVING_STUDENT_STATE){
                    availableEntranceColor(client);
                    toIsland(client);
                    correctMove = true;

                }
                else
                {
                    client.sendMessageToClient("Wrong command, select Hall or Island");
                }


            }

        }
        game.setGameState(GameState.MOTHER_MOVEMENT_STATE);
    }
    private void availableEntranceColor(ServerClientHandler client) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("These are the available colors: ");
        for(Color color : game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable()){
            client.sendMessageToClient(color.name());
        }
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
                    client.sendMessageToClient("You have placed a" + ((ColorChosen) message).getColor().name().toLowerCase()
                            + "student in the hall");
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
                    client.sendMessageToClient("There are " + game.getArchipelago().size() + "islands.");
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
            if(message instanceof IndexIsland && game.getGameState()==GameState.MOVING_STUDENT_STATE){
                if(((IndexIsland) message).getIdxIsland() <= game.getArchipelago().size() && ((IndexIsland) message).getIdxIsland() >0){
                    game.entranceToIsland(((IndexIsland) message).getIdxIsland() -1, color);
                    client.sendMessageToClient("You have placed a " + color.name()
                            + "student on the island number" + ((IndexIsland) message).getIdxIsland());
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
    public int getNumPlayer() {
        return numPlayer;
    }

}
