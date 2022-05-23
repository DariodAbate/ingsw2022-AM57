package it.polimi.ingsw.network.client.view;

/**
 * This class contains al the ANSI escape code used for the CLI
 *
 * @author Dario d'Abate
 */
public class ANSIConstants {
    public static final String TEXT_RESET = "\033[0m";      // Text Reset
    public static final String CLEAR = "\033[H\033[2J";     // Clear screen
    public static final String UNDERLINE = "\033[4m";


    public static final String BACKGROUND_BLACK = "\033[40m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String PURPLE = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";

    public static final String FILLED_RECTANGLE = "█";
    public static final String LIGHT_FILLED_RECTANGLE = "░";
}


