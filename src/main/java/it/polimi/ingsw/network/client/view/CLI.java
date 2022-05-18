package it.polimi.ingsw.network.client.view;

import it.polimi.ingsw.model.CardBack;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.client.messages.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * This is the main class  for the Command Line Interface
 *
 * @author Dario d'Abate
 */
public class CLI implements UI, PropertyChangeListener {
    private final SocketClient socketClient;
    private final Scanner stdIn ;
    private volatile boolean sending;


    public CLI(SocketClient socketClient){
        this.socketClient = socketClient;
        stdIn = new Scanner(new InputStreamReader(System.in));
        sending = true;
    }

    /**
     * This method starts the mechanism of pinging and the mechanism to receive data packets from server. It also sends
     * commands to the server, based on the user's input
     */
    public void communicationWithServer() throws ClassNotFoundException, IOException {
        cliWelcome();
        socketClient.startListening();
        socketClient.startPinging();

        String userInput;
        while (sending ) {
            userInput = stdIn.nextLine();
            System.out.println();
            if(isNumeric(userInput)){
                socketClient.send(new IntegerMessage(Integer.parseInt(userInput)));
            }
            else if(userInput.equalsIgnoreCase("king") || userInput.equalsIgnoreCase("witch")
                    || userInput.equalsIgnoreCase("sage") || userInput.equalsIgnoreCase("druid")){
                CardBack back = CardBack.valueOf(userInput.toUpperCase());
                socketClient.send(new ChooseCardBack(back));
            }
            else if(userInput.equalsIgnoreCase("black") || userInput.equalsIgnoreCase("white")
                    || userInput.equalsIgnoreCase("gray")){
                Tower tower = Tower.valueOf(userInput.toUpperCase());
                socketClient.send(new ChooseTowerColor(tower));
            }
            else if(userInput.equalsIgnoreCase("hall") || userInput.equalsIgnoreCase("island")){
                socketClient.send(new MoveStudentMessage(userInput));
            }
            else if(userInput.equalsIgnoreCase("blue") || userInput.equalsIgnoreCase("pink")
                    ||userInput.equalsIgnoreCase("red")||userInput.equalsIgnoreCase("yellow")||
                    userInput.equalsIgnoreCase("green")){
                Color color = Color.valueOf(userInput.toUpperCase());
                socketClient.send(new ColorChosen(color));
            }
            else if(userInput.equalsIgnoreCase("play")){
                socketClient.send(new PlayExpertCard());
            }
            else{
                socketClient.send(new GenericMessage(userInput));
            }

        }
        stdIn.close();
    }

    /**
     * This method is used to print some initial information
     */
    private void cliWelcome() {
        System.out.println("\n" +
                "  ______                        _   _     \n" +
                " |  ____|                      | | (_)    \n" +
                " | |__   _ __ _   _  __ _ _ __ | |_ _ ___ \n" +
                " |  __| | '__| | | |/ _` | '_ \\| __| / __|\n" +
                " | |____| |  | |_| | (_| | | | | |_| \\__ \\\n" +
                " |______|_|   \\__, |\\__,_|_| |_|\\__|_|___/\n" +
                "               __/ |                      \n" +
                "              |___/                       \n");

        System.out.println("Authors: Dario d'Abate - Lorenzo Corrado - Luca Bresciani");
        System.out.println();
    }

    /**
     * This method is used to check if the argument passed is a string representation of an integer
     * @param string string to check
     * @return true if the argument passed represent an integer, false otherwise
     */
    public static boolean isNumeric(String string){
        if (string == null || string.equals(""))
            return false;

        try{
            int intValue = Integer.parseInt(string);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * This method is used to flush the screen. It works only for shell, not
     * for intellij's command line
     */
    private  void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * This method will print content based on the event that the client receives
     * @param evt event occurred due to server
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("stopSending")){
            sending = false;

        }
        if(evt.getPropertyName().equals("genericMessage")){
            System.out.println(evt.getNewValue());
        }
    }


}
