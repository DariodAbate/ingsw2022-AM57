package network.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    private Socket echoSocket ;
    private PrintWriter out ;
    private Scanner in ;
    private Scanner stdIn ;
    private volatile boolean listenServer;

    public Client(String hostName, int portNumber) throws IOException {
        echoSocket =  new Socket(hostName, portNumber);
        out = new PrintWriter(echoSocket.getOutputStream(), true);
        in = new Scanner(new InputStreamReader(echoSocket.getInputStream()));
        stdIn = new Scanner(new InputStreamReader(System.in));
        listenServer = true;
    }

    public void communicationWithServer()throws IOException{

        //THE CLIENT MUST SEND A MESSAGE AS FIRST THING
        System.out.println("Type \"?\" to show commands");
        Thread inServer = new Thread(() -> printServerMessage());
        inServer.start();

        String userInput;
        while ((userInput = stdIn.nextLine()) != null) {
            out.println(userInput);

            if (userInput.equals("quit")) {
                //System.out.println("Connection closed");
                listenServer = false;
                break;
            }
        }
        in.close();
        stdIn.close();
        echoSocket.close();
    }

    public void printServerMessage(){
        while(listenServer)
            try{
                System.out.println(in.nextLine());
            }catch (NoSuchElementException e ){
                System.out.println("Connection closed");
                System.exit(0);
            }
    }

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
        }catch(IOException e){
            System.err.println("Error during client initialization");
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}
