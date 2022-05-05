package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.messages.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private SocketServer socketServer;

    private Map<Integer, String> loggedPlayersByNickname;
    private Map<Integer, ServerClientHandler> loggedPlayersByConnection;
    private int nextId;
    private GameHandler currentGame = null;

    private int requiredPlayer;
    private boolean expertMode;
    /** List of clients waiting in the lobby. */
    private final ArrayList<ServerClientHandler> connectionList;




    /**
     * Constructor of the class that creates a socketServer Object and a thread that
     * allows you to close the server process
     * @param port port number on which the server will listen
     */
    public MultiServer(int port) {
        socketServer = new SocketServer(this, port);
        Thread thread = new Thread(this::stopServer); //thread that listen for quitting
        thread.start();
        connectionList = new ArrayList<>();

        loggedPlayersByNickname = new HashMap<>();
        loggedPlayersByConnection = new HashMap<>();
        nextId = -1;
        expertMode = false;
    }

    /**
     * This method stop the server and close all active connections.
     */
    public void stopServer(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            if(scanner.next().equalsIgnoreCase("close")){
                socketServer.setOperating(false);
                System.exit(0);
                break;
            }
        }
    }

    /**
     * This method logs a player in the server the first time it connects.
     *
     * @param clientHandler client handler associated to a player.
     */
    public void loginPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        if(!loggedPlayersByConnection.containsValue(clientHandler)) {
            boolean hasRegistered = registerPlayer(clientHandler);
            if(hasRegistered)
                addToLobby(clientHandler);
        }
        else
            clientHandler.sendMessageToClient("User already logged.");
    }

    /**
     * This method register a player in the server, associating to it an integer id. The player will choose
     * a unique nickname.
     * @param clientHandler client handler associated to a player.
     */
    public synchronized boolean registerPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        clientHandler.sendMessageToClient("Set a nickname.");

        boolean correctNick = false;
        while(!correctNick) {
            Message nick = clientHandler.readMessageFromClient();
            if (nick instanceof GenericMessage) {
                String nickName = ((GenericMessage) nick).getMessage();
                if (!loggedPlayersByNickname.containsValue(nickName)) {
                    ++nextId;
                    loggedPlayersByNickname.put(nextId, nickName);
                    loggedPlayersByConnection.put(nextId, clientHandler);
                    correctNick = true;
                    clientHandler.setNickname(nickName);
                    clientHandler.sendMessageToClient("Welcome " + nickName);
                } else {
                    clientHandler.sendMessageToClient("Username not available, please try again.");
                }
            }else{//malicious client
                return false;
            }
        }
        return true;
    }

    /**
     * This method add a player to a lobby. If that player is the first, it will set a game parameters, otherwise it will
     * wait until all the players are connected. When the required number of player is reached, a new game starts.
     *
     * @param clientHandler client handler associated to a player.
     */
    public synchronized void addToLobby(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        connectionList.add(clientHandler);

        if (connectionList.size() == 1) {
            selectNumPlayer(clientHandler);
            selectGameMode(clientHandler);
        } else if (connectionList.size() == requiredPlayer) {
            broadcastMessage("Number of players reached. Starting a new game.");

            currentGame = new GameHandler(requiredPlayer, expertMode, new ArrayList<>(connectionList));
            currentGame.setup();
            //passare la reference di gamehandler ai socket

            connectionList.clear();
            requiredPlayer = 0;
            expertMode = false;
            currentGame = null;
        } else {
            clientHandler.sendMessageToClient("Wait for " + (this.requiredPlayer - connectionList.size()) + "players to join.");
        }
    }



    /**
     * This method removes a client from a lobby before the maximum number is reached.
     * @param clientHandler client handler associated to a player.
     */
    public synchronized void removeFromLobby(ServerClientHandler clientHandler){
        connectionList.remove(clientHandler);
    }


    /**
     * This method set the number of player for a game
     * @param numPlayer number of players that will connect to a game
     * @return true if a player insert a valid number of player, false otherwise
     */
    private boolean setRequiredPlayer(String numPlayer){
        try {
            int temp = Integer.parseInt(numPlayer);
            if(temp <= 1 || temp > 3) return false;

            this.requiredPlayer = temp;
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void selectNumPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        clientHandler.sendMessageToClient("You are the first player; Please choose a number of players.");
        boolean isNumber = false;
        while(!isNumber) {
            Message msg = clientHandler.readMessageFromClient();
            if(msg instanceof GenericMessage) {

                String temp = ((GenericMessage) msg).getMessage();
                if (!(isNumber = setRequiredPlayer(temp))) {
                    clientHandler.sendMessageToClient("Please choose a valid number of players.");

                } else
                    clientHandler.sendMessageToClient("Number of players inserted: " + temp);
            }
        }
    }

    private void selectGameMode(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        clientHandler.sendMessageToClient("Do you want to play expert mode? [y/n]");
        boolean isCorrect = false;
        while(!isCorrect) {
            Message msg = clientHandler.readMessageFromClient();
            if(msg instanceof GenericMessage) {

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
     *  This method sends a message to all the logged players that are waiting for a game
     * @param msg message sent.
     */
    public void broadcastMessage(String msg) throws IOException {
        for(ServerClientHandler clientHandler: connectionList){
            clientHandler.sendMessageToClient(msg);
        }
    }


    /**
     * Main class of the server. It creates a MultiEchoServer class that will run on an executor
     * @param args args[0] contain the port number
     */
    public static void main(String[] args) {
        System.out.println("Server");
        if (args.length != 1) {
            System.err.println("Missing port number");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);

        MultiServer server = new MultiServer(portNumber);
        ExecutorService executor = Executors.newCachedThreadPool();
        System.out.println("Creating server class...");
        executor.submit(server.socketServer);
    }
}