package it.polimi.ingsw.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoServerClientHandler implements Runnable {
    private MultiEchoServer server;
    private Socket socket;

    public EchoServerClientHandler(MultiEchoServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try{
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());


           // playerSetup(in, out);

            // Leggo e scrivo nella connessione finche' non ricevo "quit"
            while (true) {
                String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                } else {
                    out.println("Received: " + line);
                    out.flush();
                }
            }
            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
    private synchronized void playerSetup(Scanner input, PrintWriter output){
        output.println("Sono in playerSetup");

        if(server.getCurrentGame() == null) {
            output.println("You are the first player, choose the number of players!");
            String line = input.nextLine();
            setPlayer(line);
        }
        else
            output.println("Joining the game!");


    }


    private void setPlayer(String numPlayer){
        try {
            int num = Integer.parseInt(numPlayer);
            server.setRequiredPlayer(num);
        }catch(NumberFormatException e){
            System.out.println("Bad format of message");
            System.exit(1);
        }
    }

     */

}