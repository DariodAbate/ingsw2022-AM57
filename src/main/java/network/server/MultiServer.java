package network.server;

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
    private int port;
    private SocketServer socketServer;

    //TODO gestione lobby

    private Map<Integer, String> loggedPlayersByNickname;
    private Map<Integer, ServerClientHandler> loggedPlayersByConnection;
    private int nextId;
    private GameHandler currentGame = null;

    private int requiredPlayer;
    /** List of clients waiting in the lobby. */
    private final ArrayList<ServerClientHandler> connectionList;




    /**
     * Constructor of the class that creates a socketServer Object and a thread that
     * allows you to close the server process
     * @param port port number on which the server will listen
     */
    public MultiServer(int port) {
        this.port = port;
        socketServer = new SocketServer(this, port);
        Thread thread = new Thread(() -> stopServer()); //thread that listen for quitting
        thread.start();
        connectionList = new ArrayList<>();

        loggedPlayersByNickname = new HashMap<>();
        loggedPlayersByConnection = new HashMap<>();
        nextId = -1;
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
    public void loginPlayer(ServerClientHandler clientHandler){
        if(!loggedPlayersByConnection.containsValue(clientHandler)) {
            registerPlayer(clientHandler);
            addToLobby(clientHandler);
        }
        else
            clientHandler.sendMessageToClient("Hai già eseguito il login");
    }

    /**
     * This method register a player in the server, associating to it an integer id. The player will choose
     * a unique nickname.
     * @param clientHandler client handler associated to a player.
     */
    public synchronized void registerPlayer(ServerClientHandler clientHandler){
        clientHandler.sendMessageToClient("Inserisci un nickname");

        boolean correctNick = false;
        while(!correctNick){
            String nickName = clientHandler.readMessageFromClient();

            if(!loggedPlayersByNickname.containsValue(nickName)){
                ++ nextId;
                loggedPlayersByNickname.put(nextId,nickName);
                loggedPlayersByConnection.put(nextId, clientHandler);
                correctNick = true;
                clientHandler.setNickname(nickName);
                clientHandler.sendMessageToClient("Benvenuto " + nickName);
            }else{
                clientHandler.sendMessageToClient("Il tuo nickname è già stato utilizzato, inseriscine un altro.");
            }
        }
    }

    /**
     * This method add a player to a lobby. If that player is the first, it will set a game parameters, otherwise it will
     * wait until all the players are connected. When the required number of player is reached, a new game starts.
     *
     * @param clientHandler client handler associated to a player.
     */
    public synchronized void addToLobby(ServerClientHandler clientHandler){
        connectionList.add(clientHandler);

        if (connectionList.size() == 1) {
            clientHandler.sendMessageToClient("Sei il primo giocatore; Inserisci il numero di players");//non posso mettere due send
            boolean isNumber = false;
            while(!isNumber) {
                String temp = clientHandler.readMessageFromClient();
                if(!(isNumber = setRequiredPlayer(temp))){
                    clientHandler.sendMessageToClient("Non hai inserito un numero di giocatori, riprova");

                }else
                    clientHandler.sendMessageToClient("Numero di giocatori inserito: " + temp);
                    //TODO new game
                    //TODO pulisci coda di connessione dopo averla passata a game
                    //TODO pulisci mappa che coneneve ale connessioni passate a game
            }

        } else if (connectionList.size() == requiredPlayer) {
            broadcastMessage("Numero di giocatori raggiunto, inizio partita.");
        } else {
            clientHandler.sendMessageToClient("Attendi che si arrivi a " + this.requiredPlayer + " giocatori");

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
    public boolean setRequiredPlayer(String numPlayer){
        try {
            int temp = Integer.parseInt(numPlayer);
            if(temp <= 1 || temp > 3) return false;

            this.requiredPlayer = temp;
            currentGame = new GameHandler(this.requiredPlayer);
            System.out.println(this.requiredPlayer);
            return true;
        }catch (NumberFormatException e){
            return false;
        }

    }

    /**
     *  This method sends a message to all the logged players that are waiting for a game
     * @param msg message sent.
     */
    public void broadcastMessage(String msg){
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