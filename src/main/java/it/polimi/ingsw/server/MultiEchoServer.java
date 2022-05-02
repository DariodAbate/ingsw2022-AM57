package it.polimi.ingsw.server;

import java.util.ArrayList;
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
public class MultiEchoServer {
    private int port;
    private SocketServer socketServer;

    //TODO gestione lobby

    private Map<Integer, EchoServerClientHandler> connectionQueue;
    private int nextId;
    private GameHandler currentGame = null;
    private int requiredPlayer;
    /** List of clients waiting in the lobby. */
    private final ArrayList<EchoServerClientHandler> waiting = new ArrayList<>();




    /**
     * Constructor of the class that creates a socketServer Object and a thread that
     * allows you to close the server process
     * @param port port number on which the server will listen
     */
    public MultiEchoServer(int port) {
        this.port = port;
        socketServer = new SocketServer(this, port);
        Thread thread = new Thread(() -> stopServer()); //thread that listen for quitting
        thread.start();


        /*
        nextId = 0;
        connectionQueue = new HashMap<>();

         */
    }

    /**
     * This method stop the server and close all active connections.
     */
    public void stopServer(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            if(scanner.next().equalsIgnoreCase("quit")){
                socketServer.setOperating(false);
                System.exit(0);
                break;
            }
        }
    }

    /*
    public synchronized GameHandler getCurrentGame() {
        return currentGame;
    }

     */

    /*
    public void startServer() {

        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }
        System.out.println("Server ready");




        while (true) {
            try {
                //Socket socket = serverSocket.accept();
                //EchoServerClientHandler clientHandler = new EchoServerClientHandler(this, socket);


                //executor.submit(clientHandler);
                connectionQueue.put(nextId,clientHandler);
                ++nextId;



                System.out.println("Accepted connection by" + socket.getLocalAddress());
            } catch(IOException e) {
                break; // Entrerei qui se serverSocket venisse chiuso
            }
        }
        executor.shutdown();
    }

*/
    public void setRequiredPlayer(int numPlayer){ //TODO OUTOFBOUNDEXCEPTION
        this.requiredPlayer = numPlayer;
        currentGame = new GameHandler(numPlayer);
    }

    public synchronized void lobby(EchoServerClientHandler c) throws InterruptedException {
        waiting.add(c);
        if (waiting.size() == 1) {
            c.sendMessage("Sei il primo giocatore");
            setRequiredPlayer(3);
        } else if (waiting.size() == requiredPlayer) {
            c.sendMessage("Giocatori pieni, inizio partita pirupiru");
        } else {
            c.sendMessage("NON sei il primo giocatore");

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

        MultiEchoServer server = new MultiEchoServer(portNumber);
        ExecutorService executor = Executors.newCachedThreadPool();
        System.out.println("Creating server class...");
        executor.submit(server.socketServer);
    }
}