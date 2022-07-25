package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Game;

import java.io.*;
import java.util.*;

/**
 * This class is used to manage the persistence mechanism
 *
 * @author Dario d'Abate
 */
public class ReconnectionHandler {
    private final MultiServer server;

    private final Map<ArrayList<String>, Integer> gameIdByUserMap; //associate a players' session  with a progressive integer
    private final Map<Integer,ArrayList<ServerClientHandler>> reconnectedPlayerMap;// associate reconnected players to a gameId
    private int nextId;

    /**
     * Default constructor used to create a new Reconnection handler object
     * @param server server object that instantiates ReconnectionHandler object
     */
    public ReconnectionHandler(MultiServer server){
        this.server = server;
        gameIdByUserMap = new HashMap<>();
        reconnectedPlayerMap = new HashMap<>();
        nextId = -1;
    }


    /**
     * Constructor used to reload data from a previous ReconnectionHandler object
     * @param server server object that instantiates ReconnectionHandler object
     * @param nextId previous nextId
     * @param gameIdByUserMap previous gameIdUserMap
     */
    public ReconnectionHandler(MultiServer server, int nextId, Map<ArrayList<String>, Integer> gameIdByUserMap){
        this.server = server;
        this.nextId = nextId;
        this.gameIdByUserMap = gameIdByUserMap;
        reconnectedPlayerMap = new HashMap<>();
    }

    /**
     * Helper method used to get the id of a game corresponding to the given player
     * @param player nickname of a player
     * @return id corresponding to a started game, null if that player does not exist
     */
    private Integer getIdByNickname(String player){
        if(getKey(player) == null)
            return null;

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
        int numPlayerReconnected = reconnectedPlayerMap.get(idOfAGame).size();
        int numPlayerToReconnect = getInitialOrder(clientHandler.getNickname()).size();

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
        for(ServerClientHandler clientHandler: reconnectedPlayerMap.get(idOfAGame)){
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
        ArrayList<ServerClientHandler> clientHandlers = reconnectedPlayerMap.get(idOfAGame);
        if(clientHandlers == null){//first player to reconnect
            clientHandlers = new ArrayList<>();
            clientHandlers.add(clientHandler);
            reconnectedPlayerMap.put(idOfAGame, clientHandlers);
        }else{//other player that reconnect
            clientHandlers.add(clientHandler);
        }
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
        ArrayList<ServerClientHandler> clientHandlers = reconnectedPlayerMap.get(idOfAGame);
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
     * This method is used to retrieve the list of players that was originally disconnected
     * @param nickname nickname of one of the players
     * @return list of players that was originally disconnected if the nick has correspondence, null otherwise
     */
    public ArrayList<String> getInitialOrder(String nickname){
        for(ArrayList<String> players : gameIdByUserMap.keySet()){
            if(players.contains(nickname))
                return new ArrayList<>(players);
        }
        return null;
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
     * This method saves a game session on disk, binding it to the list of related player.
     * Thus, it saves the attributes of this class on disk, because they are modified
     * @param game game to be saved on disk
     * @param playersNick list of nickname of players that started that game
     */
    public void addGame(Game game, ArrayList<String> playersNick){
        gameIdByUserMap.put(playersNick, getNextId(playersNick.get(0)));
        writeGame(game);
        saveParameters();// saving nextId and map on disk

    }

    /**
     * This method return the id associated to the corresponding game.
     * If the key does not already exist, it returns the next available id
     * @param nickname nickname of one player associated with a game
     * @return id associated to a game, specifying one player
     */
    private int getNextId(String nickname){
        Integer tempId = getIdByNickname(nickname);
        if(tempId == null){//game not saved
            ++nextId;
            return nextId;
        }
        return tempId;
    }

    /**
     * This method is used to remove all the participant of a game from the map because they won a game.
     * If the map does not contain that nickname it does nothing
     * @param nickPlayer nickname of one of the player to remove
     */
    public void remove(String nickPlayer){
        //control over existence of mapping already done
        try {
            int idToRemove = getIdByNickname(nickPlayer);
            ArrayList<String> playersToRemove = getKey(nickPlayer);

            this.reconnectedPlayerMap.remove(idToRemove);
            this.gameIdByUserMap.remove(playersToRemove);
            saveParameters();//update gameIDByUserMap on disk
        }catch(NullPointerException ignored){

        }
    }


    /**
     * This method writes on disk a game, it manages the stream associated with a file
     * @param game game object to be written on disk
     */
    private void writeGame(Game game){
        try{
            File directory = new File("SavedGames");
            directory.mkdir();
            String path = "SavedGames/SerializationGame" + nextId +".ser";
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(game);

            o.close();
            f.close();
        } catch (IOException e) {
            System.out.println("Message: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method restarts a game session by a callback to the server
     * @param idOfAGame id of the game that will be restarted
     */
    private void restartGame(int idOfAGame) {
        Game game = readGame(idOfAGame);
        ArrayList<ServerClientHandler> playersToRestart = reconnectedPlayerMap.get(idOfAGame);
        orderPlayer(playersToRestart);
        if(game != null){
            server.restartGame(game, playersToRestart);
        }
    }


    /**
     * This method is used to sort the list of clientHandler with the same order of the initial login
     * of players
     * @param playersToRestart list of clientHandler that reconnected
     */
    private void orderPlayer(ArrayList<ServerClientHandler> playersToRestart) {
        //to find the list of player you need only a player
        ArrayList<String> initialOrder = getInitialOrder(playersToRestart.get(0).getNickname());

        playersToRestart.sort(Comparator.comparing(
                item -> initialOrder.indexOf(item.getNickname())
        ));
    }


    /**
     * This method is used to retrieve a game back from disk
     * @param idOfAGame id of the game
     * @return game object corresponding to a started game
     */
    private Game readGame(int idOfAGame) {
        Game g = null;
        try {
            String path = "SavedGames/SerializationGame" + idOfAGame +".ser";

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

    /**
     * This method is used to write on disk the attributes of this class that has to be reloaded
     * because a server crash has occurred
     */
    private void saveParameters(){
        try{
            String path = "SavedServerParameters/gameIdByUserMap.ser";
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(this.gameIdByUserMap);
            o.close();
            f.close();


            path = "SavedServerParameters/nextId.ser";
            f = new FileOutputStream(path);
            o = new ObjectOutputStream(f);
            o.writeObject(this.nextId);

            o.close();
            f.close();
        } catch (IOException e) {
            System.out.println("Message: " +  e.getMessage());
            e.printStackTrace();
        }
    }

}
