package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.messages.Message;
import it.polimi.ingsw.network.server.answers.request.RequestExpertModeAnswer;
import it.polimi.ingsw.network.server.answers.request.RequestNicknameAnswer;
import it.polimi.ingsw.network.server.answers.request.RequestNumPlayerAnswer;
import it.polimi.ingsw.network.server.answers.request.StartAnswer;
import it.polimi.ingsw.network.server.exception.GameDisconnectionException;
import it.polimi.ingsw.network.server.exception.SetupGameDisconnectionException;

import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the main class of the server. It takes care of managing the various roles for connecting with clients
 * and creating the appropriate threads.
 *
 * @author Dario d'Abate
 */
public class MultiServer {
    private final SocketServer socketServer;
    private ReconnectionHandler reconnectionHandler;
    
    private ArrayList<String> loggedPlayers;//list of all the nicknames used in the server
    private ArrayList<ServerClientHandler> connectionList; //list of client waiting in the lobby

    private int requiredPlayer;
    private boolean expertMode;

    /*
     * The management of multiple games is as follows. The first player connects to the server and decides
     * the number of players for a specific game.
     * When that number is reached, an instance of the controller is created and those players are connected
     * to the GameHandler who will take care of the actual game. Once this is done, a new first player will be
     * chosen and the same process follows.
     */


    /**
     * Constructor of the class that creates a socketServer Object and a thread that
     * allows you to close the server process
     * @param port port number on which the server will listen
     */
    public MultiServer(int port) {
        socketServer = new SocketServer(this, port);
        Thread thread = new Thread(this::stopServer); //thread that listen for quitting
        thread.start();
        reloadPreviousServer();
    }

    /**
     * This method stop the server and close all active connections.
     */
    public void stopServer(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            if(scanner.next().equalsIgnoreCase("close")){
                socketServer.setOperating(false);

                /*
                 * Delete the stored files in disk.
                 * WARNING: check the folder to be sure that the deletion occurred, otherwise the server cannot restart
                 */
                File savedParametersDirectory = new File("SavedServerParameters");
                savedParametersDirectory.mkdir();
                fileDeletion(savedParametersDirectory);

                File savedGamesDirectory = new File("SavedGames");
                fileDeletion(savedGamesDirectory);
                System.out.println("Cleaned old files");
                System.exit(0);
                break;
            }
        }
    }

    /**
     * Helper method used to delete the files that managed the persistence mechanism
     * @param directory directory in which the files will be deleted
     */
    private void fileDeletion(File directory) {
        if (directory.isDirectory() && (Objects.requireNonNull(directory.list())).length > 0) {

            //delete old content
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                boolean control = file.delete();
                System.out.println("Deleting: " + file.getName());
                System.out.println(control ? ("File deleted"):"Cannot delete this file, please remove manually from the folder!");
            }
        }
    }

    /**
     * This method unregister a player from the server, deleting his nickname in the "database".
     * If that nickname is not registered in the server, nothing happen
     * @param nickname nickname of the player to be deleted from the server
     */
    public void unregisterPlayer(String nickname){
        loggedPlayers.remove(nickname);

        saveParameters(); //update loggedPlayers on disk
    }

    /**
     * This method unregister ALL the players of a game from the reconnection handler, due to a victory.
     * @param nickname nickname of one of the players that belongs to a game
     */
    public void unregisterPlayerFromReconnection(String nickname){
        reconnectionHandler.remove(nickname);
    }

    /**
     * This method logs a player in the server the first time it connects.
     *
     * @param clientHandler client handler associated to a player.
     */
    public void loginPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        boolean hasRegistered = registerPlayer(clientHandler);
        if(hasRegistered)
            addToLobby(clientHandler);
    }

    /**
     * This method register a player in the server, saving his nickname. The player will choose
     * a unique nickname.
     * If a player disconnects as soon as it connects to the server, it is disconnected and not registered on the server.
     * @param clientHandler client handler associated to a player.
     */
    private synchronized boolean registerPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        //clientHandler.sendMessageToClient("Set a nickname.");
        clientHandler.sendMessageToClient(new RequestNicknameAnswer("Set a nickname"));

        boolean correctNick = false;
        while(!correctNick) {
            Message nick;
            try {
                nick = clientHandler.readMessageFromClient();
            } catch (SocketTimeoutException | SocketException e) {
                //a disconnection at this stage means that the player is not registered on the server
                if(e instanceof SocketTimeoutException )
                    clientHandler.sendShutDownToClient();
                return false;
            }
            if (nick instanceof GenericMessage) {
                String nickName = ((GenericMessage) nick).getMessage();
                if(reconnectionHandler.containPlayer(nickName) ){//user logged after a disconnection
                    if(!reconnectionHandler.alreadyLogged(nickName)){//user not yet reconnected
                        clientHandler.setNickname(nickName);
                        reconnectionHandler.reconnectPlayer(clientHandler);
                        return false;//user already logged

                    } else{//inserted user of player already reconnected
                        clientHandler.sendMessageToClient("That user has already reconnected. Please insert a valid nickname");
                    }
                }
                if(!loggedPlayers.contains(nickName)){
                    loggedPlayers.add(nickName);
                    saveParameters(); //saving loggedPlayers on disk

                    correctNick = true;
                    clientHandler.setNickname(nickName);
                    clientHandler.sendMessageToClient("Welcome " + nickName);
                } else {
                    clientHandler.sendMessageToClient("Username not available, please try again.");
                }
            } else {//nickname not valid
                clientHandler.sendMessageToClient("Set a valid nickname");
            }
        }
        return true;
    }

    /**
     * This method add a player to a lobby. If that player is the first, it will set a game parameters, otherwise it will
     * wait until all the players are connected. When the required number of player is reached, a new game starts.
     * If the first player enters the nickname and then disconnects, he is removed from the server and any parameters he has
     * set for a game are reset, furthermore his track on the server is deleted
     * @param clientHandler client handler associated to a player.
     */
    private synchronized void addToLobby(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        connectionList.add(clientHandler);

        if (connectionList.size() == 1) {
            try {
                selectNumPlayer(clientHandler);
                selectGameMode(clientHandler);
                clientHandler.sendMessageToClient("Wait for " + (this.requiredPlayer - connectionList.size()) + " players to join.");

            }catch(SocketTimeoutException | SocketException e) {
                if (e instanceof SocketTimeoutException)//disconnection
                    clientHandler.sendShutDownToClient();

                System.out.println("Removing from lobby...");
                removeFromLobby(clientHandler);
            }
        } else if (connectionList.size() == requiredPlayer) {
            broadcastStart("Number of players reached. Starting a new game.");
            startGame(requiredPlayer, expertMode, this);

            connectionList.clear();
            requiredPlayer = 0;
            expertMode = false;

        } else {
            clientHandler.sendMessageToClient("Wait for " + (this.requiredPlayer - connectionList.size()) + " players to join.");
        }
    }

    /**
     * This method is used to reset the parameters set up by the first player and remove him from the server
     * @param clientHandler client handler associated with the player
     */
    public void removeFromLobby(ServerClientHandler clientHandler){
        expertMode = false;
        requiredPlayer = -1;
        connectionList.remove(clientHandler);
        loggedPlayers.remove(clientHandler.getNickname());

        saveParameters(); //update loggedPlayers on disk

    }

    /**
     * This method is used to instantiate a gameHandler that run oh his own thread. It also calls the method save game when
     * a player disconnects during a game.
     * @param requiredPlayer required number of player for a match
     * @param expertMode true for expert mode, false otherwise
     * @param server reference to the server
     */
    private synchronized void startGame(int requiredPlayer, boolean expertMode, MultiServer server){
        GameHandler gameHandler = new GameHandler(requiredPlayer, expertMode, new ArrayList<>(connectionList), server);

        Thread t = new Thread(() -> {
            try {
                gameHandler.setup();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }catch (SetupGameDisconnectionException e1){
                System.err.println("Players disconnected during setup of a game!");
            }catch(GameDisconnectionException e2){
                System.err.println("Players disconnected during a game!");
                saveGame(gameHandler);
            }
        });
        t.start();
    }

    /**
     * This method is used to instantiate a gameHandler that run oh his own thread. This method is used by
     * the reconnection mechanism
     * @param game game object that was created before
     * @param playersConnections list of client handler that was originally disconnected
     */
    public synchronized void restartGame(Game game, ArrayList<ServerClientHandler> playersConnections){
        GameHandler gameHandler = new GameHandler(game, playersConnections,this);

        Thread t = new Thread(() -> {
            try {
                gameHandler.sendGameView();//resend the view
                gameHandler.gameTurns(); //restart a game at the point where a player has disconnected
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch(GameDisconnectionException e1){
                System.err.println("Players disconnected during a game!");
                saveGame(gameHandler);
            }
        });
        t.start();
    }

    /**
     * This method is used to save a game into the disk
     * @param gameHandler game handler object associated to the game that will be saved
     */
    public synchronized void saveGame(GameHandler gameHandler) {
        ArrayList<String> playersNick = gameHandler.getNicknamePlayers();
        Game game = gameHandler.getGame();
        reconnectionHandler.addGame(game, playersNick);
    }


    /**
     * This method set a number of player for a specific game
     * @param clientHandler client that communicates with server
     */
    private synchronized void selectNumPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        //clientHandler.sendMessageToClient("You are the first player; Please choose a number of players.");
        clientHandler.sendMessageToClient(new RequestNumPlayerAnswer("You are the first player; Please choose a number of players."));


        boolean valid = false;

        while(!valid) {
            Message msg;
            try {
                msg = clientHandler.readMessageFromClient();
            } catch (SocketTimeoutException e) {
                throw new SocketTimeoutException(e.getMessage());
            }catch (SocketException e) {
                throw new SocketException(e.getMessage());
            }


            if (msg instanceof IntegerMessage) {

                int numPlayer = ((IntegerMessage) msg).getMessage();
                valid = !(numPlayer <= 1 || numPlayer > 3);
                if (!valid) {
                    clientHandler.sendMessageToClient("Please choose a valid number of players.");

                } else {
                    this.requiredPlayer = numPlayer;
                    clientHandler.sendMessageToClient("Number of players inserted: " + this.requiredPlayer);
                }
            } else {
                clientHandler.sendMessageToClient("Please insert an integer.");
            }
        }
    }

    /**
     * This method sets a game mode for a specific game
     * @param clientHandler client that communicates with server
     */
    private void selectGameMode(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        //clientHandler.sendMessageToClient("Do you want to play expert mode? [y/n]");
        clientHandler.sendMessageToClient(new RequestExpertModeAnswer("Do you want to play expert mode? [y/n]"));
        boolean isCorrect = false;
        while(!isCorrect) {
            Message msg;
            try {
                msg = clientHandler.readMessageFromClient();
            } catch (SocketTimeoutException e) {
                throw new SocketTimeoutException(e.getMessage());
            }catch (SocketException e) {
                throw new SocketException(e.getMessage());
            }

            if (msg instanceof GenericMessage) {

                String temp = ((GenericMessage) msg).getMessage();
                if (!(isCorrect = temp.equalsIgnoreCase("y") || temp.equalsIgnoreCase("n"))) {
                    clientHandler.sendMessageToClient("Please choose a valid option.");

                } else {
                    expertMode = temp.equalsIgnoreCase("y");
                    clientHandler.sendMessageToClient((expertMode ? ("Expert ") : ("Normal ")) + "mode selected.");
                }
            }
        }
    }

    /**
     * This method is used to notify all the client in the connection list about the start of a game
     * @param msg msg that will be sent
     */
    public void broadcastStart(String msg) throws IOException {
        for(ServerClientHandler clientHandler: connectionList){
            clientHandler.setStart();//exit from the waiting room
            clientHandler.sendMessageToClient(new StartAnswer(msg));
        }
    }

    /**
     * This method is used to save on disk the list of logged players.
     * This method has to be invoked whenever that list is modified
     */
    private void saveParameters(){
        try{
            String path =  "SavedServerParameters/loggedPlayers.ser";
           // String path = getClass().getResource("/SavedServerParameters/loggedPlayers.ser").toExternalForm();
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(this.loggedPlayers);

            o.close();
            f.close();

        } catch (IOException e) {
            System.out.println("Message: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get previous server related parameters from disk.
     * If present, they are reloaded and removed from the disk, otherwise new ones are created
     */
    private void reloadPreviousServer() {
        try {
            //File directory = new File("src/main/resources/SavedServerParameters");
            File directory = new File("SavedServerParameters");
            directory.mkdir();

            //File directory = new File(getClass().getResource("/SavedServerParameters").toExternalForm());
            if (directory.isDirectory()) {
                if ((Objects.requireNonNull(directory.list())).length > 0) {
                    System.out.println("Reloading previous server parameters...");
                    this.loggedPlayers = (ArrayList<String>) readFromResources("loggedPlayers");
                    int nextId = (int) readFromResources("nextId");
                    Map<ArrayList<String>, Integer> gameIdByUserMap = (Map<ArrayList<String>, Integer>) readFromResources("gameIdByUserMap");
                    reconnectionHandler = new ReconnectionHandler(this, nextId, gameIdByUserMap);
                } else {
                    System.out.println("Previous server parameters does not exist. Creating new ones...");
                    loggedPlayers = new ArrayList<>();//contains all the nickname

                    reconnectionHandler = new ReconnectionHandler(this);
                }

                connectionList = new ArrayList<>();
                expertMode = false;
                requiredPlayer = -1;
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Helper method used to reload a file from the resource folder
     * @param nameOfResource name of the file to reload
     * @return object associated with the one that has been reloaded
     */
    private Object readFromResources(String nameOfResource) {
        Object o = null;
        try {
            String path = "SavedServerParameters/"+ nameOfResource +".ser";
            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            o = oi.readObject();

            oi.close();
            fi.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return o;
    }

    /**
     * Main class of the server. It creates a MultiEchoServer class that will run on an executor
     * @param args args[0] contain the port number
     */
    public static void main(String[] args) {
        System.out.println("Server\n");
        if (args.length != 1) {
            System.err.println("Missing port number");
            System.exit(1);
        }
        int portNumber = -1;
        try {
             portNumber = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){
            System.err.println("Not a valid port number");
            System.exit(1);
        }
        MultiServer server = new MultiServer(portNumber);
        ExecutorService executor = Executors.newCachedThreadPool();
        System.out.println("Creating server class...");
        executor.submit(server.socketServer);
    }
}