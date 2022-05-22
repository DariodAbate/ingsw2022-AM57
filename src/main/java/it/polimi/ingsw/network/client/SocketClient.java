package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.client.messages.Message;
import it.polimi.ingsw.network.client.messages.Ping;
import it.polimi.ingsw.network.server.answers.Answer;
import it.polimi.ingsw.network.server.answers.Shutdown;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * This class creates a new socket that communicates with the server. It is also used for the communication with the server.
 *
 * @author Dario d'Abate
 */
public class SocketClient {
    private final Socket socket ;
    private final ObjectOutputStream out ;
    private final ObjectInputStream in ;
    private volatile boolean listenServer;

    private final AnswerHandler answerHandler;



    public SocketClient(String hostName, int portNumber, AnswerHandler answerHandler) throws IOException {
        socket = new Socket(hostName, portNumber);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.answerHandler = answerHandler;
        listenServer = true;
    }

    /**
     * Method used to run a thread in which a ping message is sent periodically
     */
    public void startPinging(){
        Thread outPing = new Thread(()-> {
            try {
                sendPing();
            } catch (IOException e) {
                System.err.println("IoException in outPing: "+ e.getMessage());
            }
        });
        outPing.start();
    }

    /**
     * This method is used to send a ping message periodically
     */
    public void sendPing() throws IOException {
        while(listenServer) {
            try{
            out.writeObject(new Ping());
            out.flush();
            Thread.sleep(3 * 1000);//ping every 3 second
            }catch(InterruptedException e){
                System.err.println("InterruptedException in sendPing: " + e.getMessage());
            }catch(SocketException e1){
                System.out.println("Connection closed");
            }
        }
    }

    /**
     * This method is used to send a message to the server
     * @param msg message to be sent
     */
    public void send(Message msg) throws SocketException {
        try{
            out.reset();
            out.writeObject(msg);
            out.flush();
        }catch(SocketException e1){
            throw new SocketException("Connection closed");
        } catch(IOException e ){
            System.err.println("Error in sending a message to the server");
        }
    }

    /**
     * This method is used to run a thread in which the client can read message
     */
    public void startListening() {
        Thread inServer = new Thread(this::read);
        inServer.start();

    }

    /**
     * This method is used to read messages from the server. It dispatches the message to the
     * answerHandler class
     */
   public void read(){
       Object msg;
       try{
           while(listenServer && (msg = in.readObject())!= null) {
               if (msg instanceof Shutdown){
                   System.err.println(((Shutdown) msg).getMessage());
                   listenServer = false;
                   answerHandler.handleMessage((Shutdown) msg);

               }
               if(msg instanceof Answer){
                   answerHandler.handleMessage((Answer) msg);
               }
               else
                   System.err.println("Unexpected message from server");
           }
           in.close();
           out.close();
           socket.close();

       }catch (ClassNotFoundException | IOException e){
           System.out.println("Connection closed");
       }
   }

}
