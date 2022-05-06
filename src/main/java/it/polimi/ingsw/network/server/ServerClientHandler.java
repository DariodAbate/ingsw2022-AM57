package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.messages.*;
import it.polimi.ingsw.network.server.answers.GenericAnswer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

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

    private GameHandler gameHandler;

    /**
     * Constructor of the class
     * @param server
     * @param socket
     */
    public ServerClientHandler(MultiServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    /**
     * In this method the streams are instantiated and closed
     */
    public void run() {
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            messageDispatcher();

            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        }catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * @throws IOException
     */
    public synchronized void messageDispatcher() throws IOException, ClassNotFoundException {
    String[] messagesType = {"quit", "login"};
        while (true) {
            Object msg = readMessageFromClient();
            if (msg instanceof Disconnect) {
                server.removeFromLobby(this);
                break;
            }
            else if(msg instanceof Help){
                sendMessageToClient(Arrays.toString(messagesType));
            }
            else if(msg instanceof Login){
                server.loginPlayer(this);
            }
            else if(msg instanceof GenericMessage){
                sendMessageToClient("Received: " + ((GenericMessage) msg).getMessage());
            }
            else{
                System.err.println("Unexpected message from client");
            }
        }
    }

    /**
     *  This method sends a message to a client
     * @param message message to be sent
     */
    public void sendMessageToClient(String message) throws IOException {
        out.writeObject(new GenericAnswer(message));
        out.flush();
    }

    /**
     * This method is used to receive a message from a client
     * @return a message read from the client
     */
    public Message readMessageFromClient() throws IOException, ClassNotFoundException {
        Object msg = null;
        while(msg == null){
            msg =  in.readObject();
        }
        if(msg instanceof Message)
            return (Message) msg;
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