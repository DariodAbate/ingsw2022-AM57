package network.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.expertGame.ExpertGame;
import network.server.messages.Disconnect;
import network.server.messages.Message;

import java.util.ArrayList;

//tutto synchrionized
public class GameHandler {
    private final int numPlayer;
    private ArrayList<ServerClientHandler> playersConnections;
    private Game game;

    public GameHandler(int numPlayer, boolean expertGame, ArrayList<ServerClientHandler> playersConnections) {
        this.numPlayer = numPlayer;
        this.playersConnections = playersConnections;
        if(expertGame) {
            game = new Game(playersConnections.get(0).getNickname(), numPlayer);
        }else
            game = new ExpertGame(playersConnections.get(0).getNickname(), numPlayer);
    }
    public synchronized void setup() {
        for(int i=1; i<numPlayer; i++){
            game.addPlayer(playersConnections.get(i).getNickname());
        }
        game.startGame();
        for(ServerClientHandler client : playersConnections){

        }

    }
    private synchronized void askColorsSetup(ServerClientHandler client){
        client.sendMessageToClient("pocciopi");
        waitForColorAnswer();
    }
    private synchronized void waitForColorAnswer(){
        boolean temp = true;
        Message message;
        while(temp){
            //getMessage
            if(message instanceof(Disconnect) && game.getGameState() == GameState.JOIN_STATE){
                game.getPlayers().get(0).chooseBack(); //controlla che non siano già stati presi
                game.getPlayers().get(0).getBoard().chooseTower();
                temp = false;
            }
        }
    }
    public int getNumPlayer() {
        return numPlayer;
    }

}
