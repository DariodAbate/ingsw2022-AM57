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

        if (args.length != 1) {
            System.err.println("Java usage: <UI type>");
            System.exit(1);
        }

        String uiType = args[0];
        if(! (uiType.equalsIgnoreCase("CLI") || uiType.equalsIgnoreCase("GUI") )){
            System.err.println("Insert GUI or CLI as third parameter");
            System.exit(1);
        }



        CLI cli;
        if(uiType.equalsIgnoreCase("CLI")) {
            cli = new CLI();
            try {
                cli.communicationWithServer();
            }catch(IOException | ClassNotFoundException e) {
                System.err.println("Error during client initialization");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        if(uiType.equalsIgnoreCase("GUI")) {
            Application.launch(GUI.class);
        }
    }
}
