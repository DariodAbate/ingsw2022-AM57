package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Game;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO implementing mechanism to save a game after a disconnection
/**
 * This class is used to reconnect disconnected players to an existing game
 */
public class ReconnectionHandler {
    private final MultiServer server;

    private final Map<ArrayList<String>, Integer> gameIdByUserMap; //associate a players' session  with a progressive integer
    private final Map<Integer,Integer> numReconnectedPlayer; //associate the id of gameIdByUser to the number of player that has reconnected
    private final Map<Integer,ArrayList<ServerClientHandler>> reconnectedPlayer;//associate the id of gameIdByUser to the client handler
    private int nextId;

    public ReconnectionHandler(MultiServer server){
        this.server = server;
        gameIdByUserMap = new HashMap<>();
        numReconnectedPlayer = new HashMap<>();
        reconnectedPlayer = new HashMap<>();
        nextId = -1;
    }

    /**
     * Helper method used to get the id of a game corresponding to the given player
     * @param player nickname of a player
     * @return id corresponding to a started game
     */
    private int getIdByNickname(String player){
        return gameIdByUserMap.get(getKey(player));
    }


    /**
     * This method is used to handle the reconnection of a valid player in the game he was playing
     * @param clientHandler client handler associated to a player
     */
    public void reconnectPlayer(ServerClientHandler clientHandler) throws IOException {
        //Control over reconnection already done
        String nickname = clientHandler.getNickname();
        int idOfAGame = getIdByNickname(nickname);
        insertClientHandler(idOfAGame, clientHandler);
        manageRestarting(idOfAGame, clientHandler);
    }

    /**
     * This method check the condition for restarting a previous game. If the condition are
     * met, it set up the mechanism for restarting
     * @param idOfAGame id of the game to be restarted
     * @param clientHandler client that has just reconnected
     */
    private void manageRestarting(int idOfAGame, ServerClientHandler clientHandler) throws IOException {
        int numPlayerReconnected = reconnectedPlayer.get(idOfAGame).size();
        int numPlayerToReconnect = numReconnectedPlayer.get(idOfAGame);

        if(numPlayerReconnected < numPlayerToReconnect){
            clientHandler.sendMessageToClient("Wait for "+ (numPlayerToReconnect - numPlayerReconnected) + " players to join.");

        }else{
            broadcastMessage(idOfAGame, "All the player has reconnected, restarting previous game...");
            restartGame(idOfAGame);
        }
    }

    /**
     * This method sends a message to all the reconnected players that belongs to a
     * specified game
     * @param idOfAGame id of the game to which the players belong
     * @param msg message sent
     */
    public void broadcastMessage(int idOfAGame, String msg) throws IOException {
        for(ServerClientHandler clientHandler: reconnectedPlayer.get(idOfAGame)){
            clientHandler.sendMessageToClient(msg);
        }
    }

    /**
     * This method insert a player in a lobby of reconnection. Exist a unique lobby for each game
     * @param idOfAGame id of the game to which that player belongs
     * @param clientHandler client handler associated with a player
     */
    private void insertClientHandler(int idOfAGame, ServerClientHandler clientHandler) throws IOException {
        //Putting a value into a map with a key which is already present in that map will overwrite the previous value.
        //So you have to put the list only the first time you find a new Keyword
        ArrayList<ServerClientHandler> clientHandlers = reconnectedPlayer.get(idOfAGame);
        if(clientHandlers == null){//first player to reconnect
            clientHandlers = new ArrayList<>();
            clientHandlers.add(clientHandler);
            reconnectedPlayer.put(idOfAGame, clientHandlers);
        }else{//other player that reconnect
            clientHandlers.add(clientHandler);
        }

        numReconnectedPlayer.merge(idOfAGame, 1, Integer::sum);//if key do not exist, put 1 as value otherwise sum 1 to the value linked to key
        clientHandler.sendMessageToClient("Welcome back "+clientHandler.getNickname());

    }

    /**
     * This method check if used to check if a player has already reconnected
     * @param nickname nickname to check
     * @return false if the nickname belongs to a player that has not yet reconnected, true otherwise
     */
    public boolean alreadyLogged(String nickname){
        //containPlayer(nickName) control already done
        int idOfAGame = getIdByNickname(nickname);
        ArrayList<ServerClientHandler> clientHandlers = reconnectedPlayer.get(idOfAGame);
        if(clientHandlers == null)//that nickname belongs to the first player that reconnected
            return false;
        for(ServerClientHandler clientHandler: clientHandlers){
            if(clientHandler.getNickname().equals(nickname))//that user has already reconnected
                return true;
        }
        return false;//that nickname belongs to a user that has not yet reconnected
    }

    /**
     * This method is used to check if the nick of a player is between those of disconnected player
     * @param player nickname of the user to check
     * @return true if the nickname belongs to a user that was disconnected from a started game, false otherwise
     */
    public boolean containPlayer(String player){
        for(ArrayList<String> players : gameIdByUserMap.keySet()){
            if(players.contains(player))
                return true;
        }
        return false;
    }

    /**
     * Helper method used to find the list of player by a single nickname
     * @param elementToFind nickname of the player that belongs to a players' session
     * @return list of nickname of players that started to play, null if that player does not exist
     */
    private ArrayList<String> getKey(String elementToFind){
        for(ArrayList<String> players : gameIdByUserMap.keySet()){
            if(players.contains(elementToFind))
                return players;
        }
        return null; //no key existing
    }

    /**
     * This method saves a game session on disk, binding it to the list of related player
     * @param game game to be saved on disk
     * @param playersNick list of nickname of players that started that game
     */
    public void addGame(Game game, ArrayList<String> playersNick){
        ++nextId;
        gameIdByUserMap.put(playersNick, nextId);
        writeGame(game);
    }


    /**
     * This method writes on disk a game, it manages the stream associated with a file
     * @param game game object to be written on disk
     */
    private void writeGame(Game game){
        try{
            String path = "src/main/resources/SavedGames/testSerializationGame" + nextId +".ser";
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



    private void restartGame(int idOfAGame) {
        Game game = readGame(idOfAGame);
        ArrayList<ServerClientHandler> playersToRestart = reconnectedPlayer.get(idOfAGame);
        //TODO rearrange arraylist of client handler to the original order with a comparator
        if(game != null){
            server.restartGame(game, playersToRestart);
        }
    }


    /**
     * This method is used to retrieve a game back from disk
     * @param idOfAGame id of the game
     * @return game object corresponding to a started game
     */
    private Game readGame(int idOfAGame) {
        Game g = null;
        try {
            String path = "src/main/resources/SavedGames/testSerializationGame" + idOfAGame + ".ser";
            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            g = (Game) oi.readObject();

            oi.close();
            fi.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return g;
    }

/*
    public static void main(String[] args) {
        ReconnectionHandler reconnectionHandler = new ReconnectionHandler();
        Game game = new Game("Dario", 2);
        game.addPlayer("Lerry");
        game.startGame();

        ArrayList<String> nickPlayer = new ArrayList<>();
        for(Player player: game.getPlayers())
            nickPlayer.add(player.getNickname());

        reconnectionHandler.addGame(game, nickPlayer);

        Game game1 = new Game("Samu", 3);
        game1.addPlayer("Fede");
        game1.addPlayer("Giulia");
        game1.startGame();

        ArrayList<String> nickPlayer1 = new ArrayList<>();
        for(Player player: game1.getPlayers())
            nickPlayer1.add(player.getNickname());

        reconnectionHandler.addGame(game1, nickPlayer1);


        reconnectionHandler.readGame("Fede");
    }


 */


}
