package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
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
    private int nextId; //next id for register a player
    private GameHandler currentGame = null; //controller for a game

    private int requiredPlayer;
    private boolean expertMode;
    private final ArrayList<ServerClientHandler> connectionList; //list of client waiting in the lobby

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
            clientHandler.sendMessageToClient("Wait for " + (this.requiredPlayer - connectionList.size()) + " players to join.");
        } else if (connectionList.size() == requiredPlayer) {
            broadcastMessage("Number of players reached. Starting a new game.");

            currentGame = new GameHandler(requiredPlayer, expertMode, new ArrayList<>(connectionList));
            for(ServerClientHandler client: connectionList)
                client.setGameHandler(currentGame);

            currentGame.setup();


            connectionList.clear();
            requiredPlayer = 0;
            expertMode = false;
            currentGame = null;


        } else {
            clientHandler.sendMessageToClient("Wait for " + (this.requiredPlayer - connectionList.size()) + " players to join.");
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
     * This method set a number of player for a specific game
     * @param clientHandler client that communicates with server
     */
    private void selectNumPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        clientHandler.sendMessageToClient("You are the first player; Please choose a number of players.");
        boolean valid = false;

        while(!valid) {
            Message msg = clientHandler.readMessageFromClient();
            if(msg instanceof IntegerMessage) {

                int numPlayer = ((IntegerMessage) msg).getMessage();
                valid = ! (numPlayer <= 1 || numPlayer > 3);
                System.out.println("valid: " + valid);
                if (!valid) {
                    clientHandler.sendMessageToClient("Please choose a valid number of players.");

                } else {
                    this.requiredPlayer = numPlayer;
                    clientHandler.sendMessageToClient("Number of players inserted: " + this.requiredPlayer);
                }
            }else{
                clientHandler.sendMessageToClient("Please insert an integer.");
            }
        }
    }

    /**
     * This method sets a game mode for a specific game
     * @param clientHandler client that communicates with server
     */
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