package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.network.client.messages.ChooseCardBack;
import it.polimi.ingsw.network.client.messages.ChooseTowerColor;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//tutto synchrionized
public class GameHandler {
    private final int numPlayer;
    private ArrayList<ServerClientHandler> playersConnections;
    private Game game;
    private Map<ServerClientHandler, Player> clientToPlayer;

    public GameHandler(int numPlayer, boolean expertGame, ArrayList<ServerClientHandler> playersConnections) {
        this.numPlayer = numPlayer;
        this.playersConnections = playersConnections;
        if(expertGame) {
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
        }

    }
    private synchronized void askColorsSetup(ServerClientHandler client) throws IOException, ClassNotFoundException{
        client.sendMessageToClient("Seleziona il colore di torre desiderato");
        client.sendMessageToClient("I colori disponibili sono rimasti: ");
        for(int i=0; i<game.getAvailableTowerColor().size(); i++){
            client.sendMessageToClient(game.getAvailableTowerColor().get(i).name());
        }
        waitForCardBackAnswer(client);
    }
    private synchronized void waitForColorsSetup(ServerClientHandler client) throws IOException, ClassNotFoundException{
        boolean towerChosen = false;
        Object message;
        Tower color;
        while(!towerChosen){
            message = client.getIn().readObject();
            if(message instanceof ChooseTowerColor && game.getGameState()==GameState.JOIN_STATE){
                color = ((ChooseTowerColor) message).getColor();
                if(game.getAvailableTowerColor().contains(color)){
                    game.associatePlayerToTower(color, clientToPlayer.get(client));
                    towerChosen = true;
                }
                else{
                    client.sendMessageToClient("La carta selezionata non è disponibile");
                }

            }
            else{
                client.sendMessageToClient("Il comando non è corretto, selezionare il colore di torre desiderato");
            }
        }
    }

    private synchronized void askCardsBackSetup(ServerClientHandler client) throws IOException , ClassNotFoundException {
        client.sendMessageToClient("Inserisci il Card Back desiderato");
        client.sendMessageToClient("I Card Back disponibili sono:");
        //client.sendMessage(new RemainingCardBackMessage())
        for(int i=0; i<game.getRemainingCardsBack().size(); i++){
            client.sendMessageToClient(game.getRemainingCardsBack().get(i).name());
        }
        waitForCardBackAnswer(client);
    }
    private synchronized void waitForCardBackAnswer(ServerClientHandler client) throws IOException , ClassNotFoundException{
        boolean backChosen = false;
        Object message;
        CardBack card;
        while(!backChosen){
            message = client.getIn().readObject();
            if(message instanceof ChooseCardBack && game.getGameState() == GameState.JOIN_STATE){
                card = ((ChooseCardBack)message).getMessage();
                if(game.getRemainingCardsBack().contains(card)) {
                    game.associatePlayerToCardsToBack(card, clientToPlayer.get(client));
                    backChosen = true;
                }
                else{
                    client.sendMessageToClient("Carta già selezionata, inviane una nuova");
                }
            }
            else
            {
                client.sendMessageToClient("Comando non corretto, devi inviare il retro della carta desiderato");
            }
        }
    }
    public int getNumPlayer() {
        return numPlayer;
    }

}
