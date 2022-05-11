package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Game;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReconnectionHandler {
    private String path = "src/main/resources/SavedGames/testSerializationGame.ser";
    //private final Map<ArrayList<String>, Game> gameByUserMap;
    private final Map<ArrayList<String>, Integer> gameIdByUserMap;
    private int nextId;

    public ReconnectionHandler(){
       // gameByUserMap = new HashMap<>();
        gameIdByUserMap = new HashMap<>();
        nextId = -1;
    }

    public boolean containPlayer(String player){
        for(ArrayList<String> players : gameIdByUserMap.keySet()){
            if(players.contains(player))
                return true;
        }
        return false;
    }

    private ArrayList<String> getKey(String elementToFind){
        for(ArrayList<String> players : gameIdByUserMap.keySet()){
            if(players.contains(elementToFind))
                return players;
        }
        return null; //no key existing
    }

    public void addGame(Game game, ArrayList<String> playersNick){
        ++nextId;
        gameIdByUserMap.put(playersNick, nextId);
        //todo write game
    }


    public void writeGame(Game game){
        try{
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(game);

            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readGame(){
        try{
            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            Game g  = (Game) oi.readObject();

            System.out.println(g.getPlayers().get(0).getNickname());
            System.out.println(g.getPlayers().get(1).getNickname());



            oi.close();
            fi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReconnectionHandler reconnectionHandler = new ReconnectionHandler();
        Game game = new Game("Dario", 2);
        game.addPlayer("Lerry");
        game.startGame();
        reconnectionHandler.writeGame(game);
        reconnectionHandler.readGame();
    }

}
