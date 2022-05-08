package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.messages.*;
import it.polimi.ingsw.network.server.answers.GenericAnswer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * This class contains the streams through witch the server communicates with a single client
 *
 * @author Dario d'Abate
 */
public class ServerClientHandler implements Runnable {
    private MultiServer server;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickname;
    private volatile boolean active;

    private GameHandler gameHandler;

    /**
     * @param server Server to which the client is connected
     * @param socket Socket of the client
     */
    public ServerClientHandler(MultiServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        active = true;
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    /**
     * In this method the streams are instantiated and closed, Thus it handles the login of a player
     */
    public void run() {
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            sendMessageToClient("Welcome to the magical world of Eryantis!!");
            initPlayer();


            while(active){ //FIXME delete this loop
               int i = 0;
            }
            sendMessageToClient("Disconnected");
           // messageDispatcher();


            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        }catch (IOException e) {
            System.err.println("IOException in  run: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * This method pass the login of a player to the server class
     */
    private synchronized void initPlayer(){
        try {
            server.loginPlayer(this);
        } catch (IOException e) {
            System.err.println("IOException in  initPlayer: " + e.getMessage());
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException in  initPlayer: " + e.getMessage());
            System.exit(1);
        }

    }

    /**
     *  This method sends a message to a client
     * @param message message to be sent
     */
    public void sendMessageToClient(String message) throws IOException {
        out.reset();
        out.writeObject(new GenericAnswer(message));
        out.flush();
    }

    /**
     * This method is used to receive a message from a client.
     * @return returns a message read from the client. Returns null it receives an
     * unexpected message.
     */
    public Message readMessageFromClient() throws IOException, ClassNotFoundException {
        Object msg = null;
        while(msg == null){
                msg =  in.readObject();
        }
        if(msg instanceof Message) {
            if(msg instanceof Disconnect) {
                active = false;
            }
            return (Message) msg;
        }
        else{
            System.err.println("Unexpected message from client");
            return null;
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}