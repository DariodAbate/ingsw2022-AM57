package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.client.messages.GenericMessage;
import it.polimi.ingsw.network.client.messages.IntegerMessage;
import it.polimi.ingsw.network.client.messages.Message;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
    private final ReconnectionHandler reconnectionHandler;
    
    private final ArrayList<String> loggedPlayers;//list of all the nicknames used in the server
    private final ArrayList<ServerClientHandler> connectionList; //list of client waiting in the lobby

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
        connectionList = new ArrayList<>();
        loggedPlayers = new ArrayList<>();//contains all the nickname

        expertMode = false;
        requiredPlayer = -1;

        reconnectionHandler = new ReconnectionHandler(this);
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
     * This method unregister a player from the serve, deleting his nickname in the database.
     * If that nickname is not registered in the server, nothing happen
     * @param nickname nickname of the player to be deleted from the server
     */
    public void unregisterPlayer(String nickname){
        loggedPlayers.remove(nickname);
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
        clientHandler.sendMessageToClient("Set a nickname.");

        boolean correctNick = false;
        while(!correctNick) {
            Message nick;
            try {
                nick = clientHandler.readMessageFromClient();
            } catch (SocketTimeoutException e) {
                //a disconnection at this stage means that the player is not registered on the server
                clientHandler.sendShutDownToClient();
                return false;
            }
            if (nick instanceof GenericMessage) {
                String nickName = ((GenericMessage) nick).getMessage();
                if(reconnectionHandler.containPlayer(nickName) ){//user logged after a disconnection
                    if(!reconnectionHandler.alreadyLogged(nickName)){//user not yet reconnected
                        clientHandler.setNickname(nickName);
                        reconnectionHandler.reconnectPlayer(clientHandler);
                        correctNick = true;
                    } else{//inserted user of player already reconnected
                        clientHandler.sendMessageToClient("That user has already reconnected. Please insert a valid nickname");
                    }
                }
                if(!loggedPlayers.contains(nickName)){
                    loggedPlayers.add(nickName);
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

            }catch(SocketTimeoutException e){
                clientHandler.sendShutDownToClient();
                expertMode = false;
                requiredPlayer = -1;
                connectionList.remove(clientHandler);
                loggedPlayers.remove(clientHandler.getNickname());
            }
        } else if (connectionList.size() == requiredPlayer) {
            broadcastMessage("Number of players reached. Starting a new game.");

            startGame(requiredPlayer, expertMode, this);

            connectionList.clear();
            requiredPlayer = 0;
            expertMode = false;

        } else {
            clientHandler.sendMessageToClient("Wait for " + (this.requiredPlayer - connectionList.size()) + " players to join.");
        }
    }

    /**
     * This method is used to instantiate a gameHandler that run oh his own thread
     * @param requiredPlayer required number of player for a match
     * @param expertMode true for expert mode, false otherwise
     * @param server reference to the server
     */
    private synchronized void startGame(int requiredPlayer, boolean expertMode, MultiServer server){
        GameHandler gameHandler = new GameHandler(requiredPlayer, expertMode, new ArrayList<>(connectionList), server);

        //capire se servirà
        for(ServerClientHandler client: connectionList)
            client.setGameHandler(gameHandler);

        Thread t = new Thread(() -> {
            try {
                gameHandler.setup();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }catch (SetupGameDisconnectionException e1){
                System.err.println("Players disconnected during setup of a game!");
            }
        });
        t.start();
    }

    public synchronized void restartGame(Game game, ArrayList<ServerClientHandler> playersConnections){
        GameHandler gameHandler = new GameHandler(game, playersConnections,this);
        //capire se servirà
        for(ServerClientHandler client: playersConnections)
            client.setGameHandler(gameHandler);

        Thread t = new Thread(() -> {
            try {
                gameHandler.gameTurns(); //restart a game at the point where a player has disconnected
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }


    /**
     * This method set a number of player for a specific game
     * @param clientHandler client that communicates with server
     */
    private void selectNumPlayer(ServerClientHandler clientHandler) throws IOException, ClassNotFoundException {
        clientHandler.sendMessageToClient("You are the first player; Please choose a number of players.");
        boolean valid = false;

        while(!valid) {
            Message msg;
            try {
                msg = clientHandler.readMessageFromClient();
            } catch (SocketTimeoutException e) {
                throw new SocketTimeoutException(e.getMessage());
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
        clientHandler.sendMessageToClient("Do you want to play expert mode? [y/n]");
        boolean isCorrect = false;
        while(!isCorrect) {
            Message msg;
            try {
                msg = clientHandler.readMessageFromClient();
            } catch (SocketTimeoutException e) {
                throw new SocketTimeoutException(e.getMessage());
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