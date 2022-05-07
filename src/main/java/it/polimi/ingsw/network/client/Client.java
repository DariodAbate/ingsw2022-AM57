package it.polimi.ingsw.network.client;


import it.polimi.ingsw.network.client.messages.*;
import it.polimi.ingsw.network.server.answers.GenericAnswer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket echoSocket ;
    private ObjectOutputStream out ;
    private ObjectInputStream in ;
    private Scanner stdIn ;
    private volatile boolean listenServer;

    public Client(String hostName, int portNumber) throws IOException {
        echoSocket =  new Socket(hostName, portNumber);
        out = new ObjectOutputStream(echoSocket.getOutputStream());
        in = new ObjectInputStream(echoSocket.getInputStream());
        stdIn = new Scanner(new InputStreamReader(System.in));
        listenServer = true;
    }

    public void communicationWithServer() throws ClassNotFoundException, IOException {

        //THE CLIENT MUST SEND A MESSAGE AS FIRST THING
        System.out.println("Type \"?\" to show commands");
        Thread inServer = new Thread(this::printServerMessage);
        inServer.start();

        /*
            Thread outPing = new Thread(()-> {
                try {
                    sendPing();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outPing.start();

         */

        String userInput;
        while ((userInput = stdIn.nextLine()) != null) {

            if(userInput.equals("?")){
                out.writeObject(new Help());
            }
            else if(userInput.equalsIgnoreCase("login")){
                out.writeObject(new Login());
            }
            else if(userInput.equalsIgnoreCase("quit")){
                listenServer = false;
                out.writeObject(new Disconnect());
                out.flush();
                break;
            }
            else if(userInput.equalsIgnoreCase("king") || userInput.equalsIgnoreCase("witch")
            || userInput.equalsIgnoreCase("sage") || userInput.equalsIgnoreCase("druid")){
                //CardBack back = CardBack.valueOf(userInput.toUpperCase());
                out.writeObject(new ChooseCardBack(userInput));
            }
            else{
                out.writeObject(new GenericMessage(userInput));
            }
            out.flush();

        }
        in.close();
        stdIn.close();
        echoSocket.close();
    }

    public void printServerMessage(){
        Object msg;
        try{
            while(listenServer && (msg = in.readObject())!= null) {
                if (msg instanceof GenericAnswer)
                    System.out.println(((GenericAnswer) msg).getMessage());
                else
                    System.err.println("Unexpected message from server");
            }

        }catch (ClassNotFoundException | IOException e){
            System.out.println("Connection closed");
        }

    }

    /*
    public void sendPing() throws IOException {
        while(listenServer) {
            out.writeObject(new Ping());
            out.flush();
            try{
                Thread.sleep(8000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

     */

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        Client client;

        try{
            client = new Client(hostName, portNumber);
            client.communicationWithServer();
        }catch(IOException | ClassNotFoundException e){
            System.err.println("Error during client initialization");
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}
