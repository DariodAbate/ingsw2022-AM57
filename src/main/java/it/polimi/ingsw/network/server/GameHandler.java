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
    }
    public synchronized void setup() throws IOException, ClassNotFoundException {

        for(int i=1; i<numPlayer; i++){
            game.addPlayer(playersConnections.get(i).getNickname());
        }

        for(int i=0; i<numPlayer; i++){
            clientToPlayer.put(playersConnections.get(i), game.getPlayers().get(i));
        }
        game.startGame();
        for(ServerClientHandler client : playersConnections){
            askCardsBackSetup(client);
            askColorsSetup(client);
        }
        game.setGameState(GameState.PLANNING_STATE);
    }

    private synchronized void askColorsSetup(ServerClientHandler client) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Select the preferred tower color");
        client.sendMessageToClient("The available tower colors are: ");
        for(int i=0; i<game.getAvailableTowerColor().size(); i++){
            client.sendMessageToClient(game.getAvailableTowerColor().get(i).name());
        }
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
        //client.sendMessage(new RemainingCardBackMessage())
        for(int i = 0; i<game.getAvailableCardsBack().size(); i++){
            client.sendMessageToClient(game.getAvailableCardsBack().get(i).name());
        }
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
            System.out.println(message instanceof ChooseCardBack);
            System.out.println(game.getGameState());
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
            client.sendMessageToClient("Please select wich assistant card do you wanna play");
            client.sendMessageToClient("The remaining assistant cards are:");
            for(AssistantCard card : game.getCurrentPlayer().getHand()){
                client.sendMessageToClient(Integer.toString(card.getPriority()));
            }
            message = client.readMessageFromClient();
            if(message instanceof PlayAssistantCard && game.getGameState() == GameState.PLANNING_STATE){
                if(game.getCurrentPlayer().isPriorityAvailable(((PlayAssistantCard) message).getMessage())) {
                    game.getCurrentPlayer().playCard(((PlayAssistantCard) message).getMessage());
                    client.sendMessageToClient("You have chosen your " + ((PlayAssistantCard) message).getMessage() + "card");
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
            client.sendMessageToClient("Select where you wanna move your students");
            while(!correctMove){
                message = client.readMessageFromClient();
                if(message instanceof EntranceToHall && game.getGameState() == GameState.MOVING_STUDENT_STATE){
                    client.sendMessageToClient("This are the avaiable colors: ");
                    for(Color color : game.getCurrentPlayer().getBoard().getEntrance().colorsAvailable()){
                        client.sendMessageToClient(color.name());
                    }
                }
                else
                {
                    client.sendMessageToClient("Wrong command, select Hall or Island");
                }


            }

        }
    }
    public int getNumPlayer() {
        return numPlayer;
    }

}
