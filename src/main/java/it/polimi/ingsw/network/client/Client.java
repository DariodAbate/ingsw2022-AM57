package it.polimi.ingsw.network.client;


import it.polimi.ingsw.network.client.view.CLI;
import it.polimi.ingsw.network.client.view.GUI;
import javafx.application.Application;

import java.io.IOException;

/**
 * Main class of the client. It takes parameter from the shell and instantiates the CLI or the GUI
 *
 * @author Dario d'Abate
 */
public class Client {

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Usage: java -jar AM57.jar <host name> <port number> <UI type>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        String uiType = args[2];
        if(! (uiType.equalsIgnoreCase("CLI") || uiType.equalsIgnoreCase("GUI") )){
            System.err.println("Insert GUI or CLI as third parameter");
            System.exit(1);
        }

        AnswerHandler answerHandler = new AnswerHandler();
        SocketClient socketClient = new SocketClient(hostName, portNumber, answerHandler);

        //TODO case for GUI
        CLI cli;
        if(uiType.equalsIgnoreCase("CLI")) {
            cli = new CLI(socketClient);
            answerHandler.addPropertyChangeListener(cli);
            try {
                cli.communicationWithServer();
            }catch(IOException | ClassNotFoundException e) {
                System.err.println("Error during client initialization");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        /*GUI gui;
        if(uiType.equalsIgnoreCase("GUI")) {
            gui = new GUI(socketClient);
            answerHandler.addPropertyChangeListener(gui);
            gui.startGUIApplication();
        }*/
        if(uiType.equalsIgnoreCase("GUI")) {
            Application.launch(GUI.class);
        }
    }
}
