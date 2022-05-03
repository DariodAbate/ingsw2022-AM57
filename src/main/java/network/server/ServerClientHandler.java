package network.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class contains the streams through witch the server communicates with a single client
 *
 * @author Dario d'Abate
 */
public class ServerClientHandler implements Runnable {
    private MultiServer server;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    /**
     * Constructor of the class
     * @param server
     * @param socket
     */
    public ServerClientHandler(MultiServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }


    /**
     * In this method the streams are instantiated and closed
     */
    public void run() {
        try{
            out = new PrintWriter(socket.getOutputStream());
            in = new Scanner(socket.getInputStream());

            messageDispatcher();

            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     * @throws IOException
     */
    public synchronized void messageDispatcher() throws IOException {
    String[] messagesType = {"quit", "login"};

        while (true) {
            String line = in.nextLine();
            if (line.equalsIgnoreCase("quit")) {
                server.removeFromLobby(this);
                break;
            }
            else if(line.equalsIgnoreCase("?")){
                sendMessageToClient(Arrays.toString(messagesType));
            }
            else if(line.equalsIgnoreCase("login")){
                server.loginPlayer(this);
            }
            else {
                sendMessageToClient("Received: " + line);
            }
        }
    }

    /**
     *  This method sends a message to a client
     * @param message message to be sent
     */
    public void sendMessageToClient(String message){
        out.println(message);
        out.flush();
    }

    /**
     * This method is used to receive a message from a client
     * @return a message read from the client
     */
    public String readMessageFromClient(){
        String msg = null;
        while(msg == null){
            msg = in.nextLine();
        }
        return msg;
    }

}