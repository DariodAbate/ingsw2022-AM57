package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class creates a new socket that accepts connections with clients and creates
 * a thread associated with them.
 *
 * @author Dario d'Abate
 */
public class SocketServer implements Runnable{
    private MultiServer server;
    private final int port;
    private final ExecutorService executor;
    private volatile boolean operating; // becomes visible to all readers when written

    /**
     * Constructor of the class
     * @param server is the type of the Server object
     * @param port port on which the server will accept connections
     */
    public SocketServer(MultiServer server, int port){
        this.server = server;
        this.port = port;
        executor =  Executors.newCachedThreadPool();
        operating = true;
    }

    /**
     * Sets a variable  which allows the server to continue accepting connections with clients
     * @param state true for accept connections, false otherwise
     */
    public void setOperating(boolean state){operating = state;}

    /**
     * This method accepts connections from clients, and for each connection creates a new thread
     * @param serverSocket socket of the server associated with a port
     */
    public void acceptConnections(ServerSocket serverSocket){
        while(operating){
            try{
                Socket socket = serverSocket.accept();
                ServerClientHandler clientHandler = new ServerClientHandler(server, socket);
                executor.submit(clientHandler);
            }catch(IOException e){
                System.out.println("Error." + e.getMessage());
            }
        }
    }

    /**
     * This method instantiates a new socket for the server
     */
    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server socket started. Listening on port " + port);
            System.out.println("Type \"close\" to exit");
            acceptConnections(serverSocket);
        }catch(IOException e){
            System.out.println("Error in initialization");
            System.exit(0);
        }
    }
}
