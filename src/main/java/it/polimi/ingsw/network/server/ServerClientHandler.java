package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.messages.Disconnect;
import it.polimi.ingsw.network.client.messages.Message;
import it.polimi.ingsw.network.client.messages.Ping;
import it.polimi.ingsw.network.server.answers.Answer;
import it.polimi.ingsw.network.server.answers.GenericAnswer;
import it.polimi.ingsw.network.server.answers.Pong;
import it.polimi.ingsw.network.server.answers.Shutdown;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * This class contains the streams through witch the server communicates with a single client
 *
 * @author Dario d'Abate
 */
public class ServerClientHandler implements Runnable {
    private final MultiServer server;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickname;
    private volatile boolean start;
    private final int PONG_CLOCK = 2;


    /**
     * @param server Server to which the client is connected
     * @param socket Socket of the client
     */
    public ServerClientHandler(MultiServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        start = false;
    }

    /**
     * Method used to exit from the waiting room
     */
    public void setStart(){
        start = true;
    }

    /**
     * In this method the streams are instantiated and closed, Thus it handles the login of a player
     */
    public void run() {
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            sendMessageToClient("Welcome to the magical world of Eriantys!!");
            try{

                initPlayer();

                /*
                 * If a player disconnects while waiting then it will be removed
                 * from the server
                 */
                while(!start){
                    Thread.sleep(PONG_CLOCK * 1000);
                    sendMessageToClient(new Pong());
                }

            }catch(SocketTimeoutException | SocketException e){
                System.out.println("Disconnecting: " + socket.getLocalAddress());
                System.out.println("Removing from the server...");
                server.removeFromLobby(this);

                if(e instanceof SocketTimeoutException)
                    sendShutDownToClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }catch(SocketException e){
            System.out.println("Disconnection completed!");
        }
        catch (IOException e) {
            System.err.println("IOException in : " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * This method pass the login of a player to the server class
     */
    private synchronized void initPlayer() throws SocketTimeoutException, SocketException {
        try {
            server.loginPlayer(this);
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException(e.getMessage());
        }catch (SocketException e){
            throw new SocketException(e.getMessage());
        }catch (IOException e) {
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


    public void sendMessageToClient(Answer answer) throws IOException {
        out.reset();
        out.writeObject(answer);
        out.flush();
    }

    public void sendShutDownToClient() throws IOException{
        out.reset();
        out.writeObject(new Shutdown("You are disconnected from the server"));
        out.flush();
        closeClientHandler();
    }

    /**
     * This method is used to receive a message from a client.
     * @return returns a message read from the client. Returns null it receives an
     * unexpected message.
     */
    public Message readMessageFromClient() throws IOException, ClassNotFoundException {
        Object msg = null;
        while(msg == null){
            try {

                if( (msg = in.readObject()) instanceof Ping ){
                    msg = null;
                }
            }catch(SocketTimeoutException e){
                throw new SocketTimeoutException("Client disconnected");
            }catch (SocketException e1){
                throw new SocketException("Client disconnected");
            }
        }
        if(msg instanceof Message) {
            if(msg instanceof Disconnect) {//TODO close the game
                start = false;
            }
            return (Message) msg;
        }
        else{
            System.err.println("Unexpected message from client");
            return null;
        }
    }

    public void closeClientHandler(){
        try {
            in.close();
            out.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
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