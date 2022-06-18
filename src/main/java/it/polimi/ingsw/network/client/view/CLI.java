package it.polimi.ingsw.network.client.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.client.AnswerHandler;
import it.polimi.ingsw.network.client.SocketClient;
import it.polimi.ingsw.network.client.messages.*;
import it.polimi.ingsw.network.client.modelBean.*;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.BanExpertCardBean;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.StudBufferExpertCardBean;
import it.polimi.ingsw.network.server.answers.AssistantCardPlayedAnswer;
import it.polimi.ingsw.network.server.answers.update.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This is the main class  for the Command Line Interface.
 *
 * @author Dario d'Abate
 */
public class CLI  implements PropertyChangeListener, UI {
    private  SocketClient socketClient;
    private final Scanner stdIn ;
    private volatile boolean sending;

    private GameBean gameBean; //model view
    private String nickname;

    /**
     * Constructor of the class. It also initializes the socketClient
     */
    public CLI(){
        stdIn = new Scanner(new InputStreamReader(System.in));
        sending = true;
        try{
            initSocketClient();
        }catch (IOException e){
            System.out.println("Cannot initialize CLI");
            System.exit(1);
        }
    }

    /**
     * Helper method used to initialize a socket. It also set up the mechanism of event handling
     * through property change listener
     */
    private void initSocketClient() throws IOException {
        AnswerHandler answerHandler = new AnswerHandler();
        int portNumber = getPortNumber();
        String hostName = getHostName();
        socketClient = new SocketClient(hostName , portNumber,answerHandler);
        answerHandler.addPropertyChangeListener(this);
    }

    /**
     * Helper method used to get and validate the hostname
     * @return valid hostname inserted by the user
     */
    private String getHostName(){
        System.out.println("Please insert the server host name.");
        String temp = null;
        boolean valid = false;
        while(!valid) {
            temp = stdIn.nextLine();
            valid = checkIpAddress(temp);
            if(!valid)
                System.out.println("Please insert a valid host name");
        }
        return temp;
    }

    /**
     * helper method used to validate an ip address
     * @param ipAddress string representation of an ip address
     * @return true if the ipAddress is well-formed, false otherwise
     */
    private static boolean checkIpAddress(String ipAddress) {
        boolean b1 = false;
        StringTokenizer t = new StringTokenizer(ipAddress, ".");
        try {
            int a = Integer.parseInt(t.nextToken());
            int b = Integer.parseInt(t.nextToken());
            int c = Integer.parseInt(t.nextToken());
            int d = Integer.parseInt(t.nextToken());
            if ((a >= 0 && a <= 255) && (b >= 0 && b <= 255)
                    && (c >= 0 && c <= 255) && (d >= 0 && d <= 255))
                b1 = true;
            return b1;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Helper method used to get a valid port number
     * @return valid port number inserted by the user
     */
    private int getPortNumber(){
        System.out.println("Please insert the server port.");
        int temp = -1;
        while(temp < 0) {
            String tempString = stdIn.nextLine();
            try{
                temp = Integer.parseInt(tempString);
            }catch (NumberFormatException e){
                System.out.println("Please insert a valid server port.");
                temp = -1;
            }
        }
        return temp;
    }

    /**
     * This method will show content based on the event that the client receives, thus it updates the view
     * @param evt event occurred due to server
     */
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "stopSending" -> closeUserInterface();
            case "genericMessage", "requestNickname", "requestNumPlayer", "requestExpertMode", "startMessage" -> displayGenericMessage((String)evt.getNewValue());
            case "towerChoice" -> displaySelectableTower((ArrayList<Tower>) evt.getNewValue());
            case "cardBackChoice" -> displaySelectableCardBack((ArrayList<CardBack>) evt.getNewValue());
            case "nickname" -> this.nickname = (String) evt.getNewValue();
            case "win" -> displayWinner((String)evt.getNewValue());
            case "gameState" -> {
                this.gameBean = (GameBean)evt.getNewValue();
                displayAllGame() ;
            }
            case "cardPlayed" -> {
                String nickname = ((AssistantCardPlayedAnswer)evt.getNewValue()).getNickname();
                ArrayList<AssistantCard> hand = ((AssistantCardPlayedAnswer)evt.getNewValue()).getHand();
                AssistantCard playedCard = ((AssistantCardPlayedAnswer)evt.getNewValue()).getCard();

                for(PlayerBean player :gameBean.getPlayers()){
                    if(player.getNickname().equals(nickname)){
                        player.setPlayedCard(playedCard);
                        player.setHand(hand); //new hand
                    }
                }
                displayAllGame() ;
            }
            case "toHall" -> {
                ArrayList<BoardBean> updatedBoardList = ((ToHallUpdateAnswer)evt.getNewValue()).getUpdatedBoardList();


                for(int i = 0; i < gameBean.getPlayers().size(); i++){
                    PlayerBean player = gameBean.getPlayers().get(i);
                    player.setBoard(updatedBoardList.get(i));
                }
                displayAllGame() ;
            }
            case "toIsland" -> {
                String nickname = ((ToIslandUpdateAnswer)evt.getNewValue()).getNickname();
                BoardBean updatedBoard = ((ToIslandUpdateAnswer)evt.getNewValue()).getUpdatedBoard();
                ArrayList<IslandBean> updatedArchipelago = ((ToIslandUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();

                for(PlayerBean player :gameBean.getPlayers()){
                    if(player.getNickname().equals(nickname))
                        player.setBoard(updatedBoard);
                }
                gameBean.setArchipelago(updatedArchipelago);
                displayAllGame() ;
            }

            case "motherMovement" -> {
                int motherNature =  ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedMotherNature();
                ArrayList<BoardBean> updatedBoards = ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                ArrayList<IslandBean> updatedArchipelago = ((MotherNatureUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();

                for(int i = 0; i < gameBean.getPlayers().size(); i++){
                    gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                }
                gameBean.setMotherNature(motherNature);
                gameBean.setArchipelago(updatedArchipelago);
                displayAllGame() ;
            }
            case "cloudChoice" -> {
                ArrayList<BoardBean> updatedBoards = ((CloudsUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                ArrayList<CloudBean> updateClouds = ((CloudsUpdateAnswer)evt.getNewValue()).getUpdateClouds();

                for(int i = 0; i < gameBean.getPlayers().size(); i++){
                    gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                }
                gameBean.setCloudTiles(updateClouds);
                displayAllGame();
            }
            case "expertCard" -> {
                ArrayList<BoardBean> updatedBoards = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedBoards();
                ArrayList<IslandBean> updatedArchipelago = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedArchipelago();
                ArrayList<ExpertCardBean> updatedExpertCards = ((ExpertCardUpdateAnswer)evt.getNewValue()).getUpdatedExpertCards();

                gameBean.setExpertCards(updatedExpertCards);
                if(updatedBoards != null){
                    for(int i = 0; i < gameBean.getPlayers().size(); i++){
                        gameBean.getPlayers().get(i).setBoard(updatedBoards.get(i));
                    }
                }
                if(updatedArchipelago != null){
                    gameBean.setArchipelago(updatedArchipelago);
                }
                displayAllGame();
            }
        }
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

        try {
            while (sending) {
                userInput = stdIn.nextLine();
                System.out.println();
                if (isNumeric(userInput)) {
                    socketClient.send(new IntegerMessage(Integer.parseInt(userInput)));
                } else if (userInput.equalsIgnoreCase("king") || userInput.equalsIgnoreCase("witch")
                        || userInput.equalsIgnoreCase("sage") || userInput.equalsIgnoreCase("druid")) {
                    CardBack back = CardBack.valueOf(userInput.toUpperCase());
                    socketClient.send(new ChooseCardBack(back));
                } else if (userInput.equalsIgnoreCase("black") || userInput.equalsIgnoreCase("white")
                        || userInput.equalsIgnoreCase("gray")) {
                    Tower tower = Tower.valueOf(userInput.toUpperCase());
                    socketClient.send(new ChooseTowerColor(tower));
                } else if (userInput.equalsIgnoreCase("hall") || userInput.equalsIgnoreCase("island")) {
                    socketClient.send(new MoveStudentMessage(userInput));
                } else if (userInput.equalsIgnoreCase("blue") || userInput.equalsIgnoreCase("pink")
                        || userInput.equalsIgnoreCase("red") || userInput.equalsIgnoreCase("yellow") ||
                        userInput.equalsIgnoreCase("green")) {
                    Color color = Color.valueOf(userInput.toUpperCase());
                    socketClient.send(new ColorChosen(color));
                } else if (userInput.equalsIgnoreCase("play")) {
                    socketClient.send(new PlayExpertCard());
                } else if(userInput.equalsIgnoreCase("stop")){
                    socketClient.send(new StopMessage());
                } else {
                    socketClient.send(new GenericMessage(userInput));
                }

            }
        } catch(SocketException e){//server crashed
            System.out.println(e.getMessage());
        }
        stdIn.close();
    }

    /**
     * This method is used to print some initial information
     */
    private void cliWelcome() {
        System.out.println("\n" +
                "  ______      _             _             \n" +
                " |  ____|    (_)           | |            \n" +
                " | |__   _ __ _  __ _ _ __ | |_ _   _ ___ \n" +
                " |  __| | '__| |/ _` | '_ \\| __| | | / __|\n" +
                " | |____| |  | | (_| | | | | |_| |_| \\__ \\\n" +
                " |______|_|  |_|\\__,_|_| |_|\\__|\\__, |___/\n" +
                "                                 __/ |    \n" +
                "                                |___/     \n");

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
        System.out.println(ANSIConstants.CLEAR);
        System.out.flush();
    }

    /**
     * This method is used to close the user interface
     */
    @Override
    public void closeUserInterface() {
        sending = false;
    }

    /**
     * This method is used to print a generic message received from the server
     * @param message message to be print
     */
    @Override
    public void displayGenericMessage(String message) {
        System.out.println(message);
    }


    /**
     * This method is used to print the entire Game view
     */
    @Override
    public void displayAllGame(){
        clearScreen();

        ArrayList<PlayerBean> playerBeans = gameBean.getPlayers();
        //display expert card
        if(gameBean.isExpertGame()){
            System.out.println(ANSIConstants.UNDERLINE + "EXPERT CARDS");
            System.out.print(ANSIConstants.TEXT_RESET);
            System.out.flush();
            System.out.println();
            displayExpertCard();
        }

        System.out.println(ANSIConstants.UNDERLINE + "BOARDS");
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        System.out.println();
        for(PlayerBean playerBean : playerBeans){
            //display  boards
            System.out.print("NICKNAME: " + playerBean.getNickname() + " ");
            if(playerBean.getNickname().equals(nickname))
                System.out.println("(YOU)");
            else
                System.out.println();
            displayBoard(playerBean.getBoard(), gameBean.isExpertGame());
            //display assistant cards
            displayCard(playerBean);
            System.out.println("------------------------------------------------------------------------");

        }

        System.out.println(ANSIConstants.UNDERLINE + "ARCHIPELAGO");
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        displayArchipelago();

        System.out.println(ANSIConstants.UNDERLINE + "CLOUDS");
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        displayClouds();
    }


    /**
     * This method is used to print the card backs sent by the server in the setup phase
     * @param selectableCardBack list of card backs that can be chosen
     */
    @Override
    public void displaySelectableCardBack(ArrayList<CardBack> selectableCardBack) {
        clearScreen();
        System.out.println("Select the preferred card back");
        System.out.print("The available card backs are: ");
        for (CardBack cardBack : selectableCardBack){
            if(cardBack == CardBack.KING)
                System.out.print(ANSIConstants.YELLOW + cardBack.name() + " ");
            if(cardBack == CardBack.WITCH)
                System.out.print(ANSIConstants.PURPLE + cardBack.name() + " ");
            if(cardBack == CardBack.DRUID)
                System.out.print(ANSIConstants.GREEN + cardBack.name() + " ");
            if(cardBack == CardBack.SAGE)
                System.out.print(ANSIConstants.BLUE + cardBack.name() + " ");
        }
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        System.out.println();
    }

    /**
     * This method is used to print the tower color sent by the server in the setup phase
     * @param selectableTowers list of tower color that can be chosen
     */
    @Override
    public void displaySelectableTower(ArrayList<Tower> selectableTowers) {
        clearScreen();
        System.out.println("Insert the preferred tower color");
        System.out.print("The available tower colors are: ");
        for (Tower towerColor: selectableTowers){
            if(towerColor == Tower.WHITE) {
                System.out.print(ANSIConstants.WHITE + towerColor.name() + " ");
            }
            if(towerColor == Tower.BLACK) {
                System.out.print(ANSIConstants.BLUE+ towerColor.name());
                System.out.print(" ");

            }
            if(towerColor == Tower.GRAY) {
                System.out.print(ANSIConstants.CYAN + towerColor.name() + " ");
            }
        }
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        System.out.println();
    }

    /**
     * This method is used to print a single board
     * @param board board to be printed
     * @param expertGame true if the game is in expert mode, false otherwise
     */
    @Override
    public void displayBoard(BoardBean board, boolean expertGame) {
        if(expertGame)
            System.out.print("REMAINING COINS: " + board.getNumCoins() + "\n");

        System.out.print("TOWER COLOR: " + board.getTowerColor() + "\n");
        System.out.print("REMAINING TOWERS: ");
        for(int i = 0; i < board.getNumTowers(); i++)
            System.out.print(ANSIConstants.FILLED_RECTANGLE + " ");

        System.out.println();
        System.out.print("PROFESSORS: ");
        for (Color color: board.getProfessors()){
            System.out.print(getAnsi(color) + ANSIConstants.FILLED_RECTANGLE + " ");
        }
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();

        System.out.println();
        System.out.println(ANSIConstants.UNDERLINE + "ENTRANCE");
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        for (Color color: Color.values()){
            for (int j = 0; j < board.getEntranceStudent().get(color); j++) {
                System.out.print(getAnsi(color) + ANSIConstants.FILLED_RECTANGLE + " ");
            }
            System.out.print(ANSIConstants.TEXT_RESET);
            System.out.flush();
            if(board.getEntranceStudent().get(color) > 0)
                System.out.println();

        }
        
        System.out.println(ANSIConstants.UNDERLINE + "HALL");
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        for (Color color: Color.values()){
            int num = board.getHallStudent().get(color);
            for (int j = 0; j < num; j++){
                System.out.print(getAnsi(color) + ANSIConstants.FILLED_RECTANGLE + " ");
            }
            for (int j = 0; j < 10 - num; j++){
                System.out.print(getAnsi(color) + ANSIConstants.LIGHT_FILLED_RECTANGLE + " ");
            }
            System.out.print(ANSIConstants.TEXT_RESET);
            System.out.flush();
            System.out.println();
        }
        
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        System.out.println();
    }

    /**
     * This method is used to print the assistant cards
     * @param playerBean model view object that contains all the data for a single player
     */
    @Override
    public void displayCard(PlayerBean playerBean) {
        System.out.println(ANSIConstants.UNDERLINE + "ASSISTANT CARDS");
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
        System.out.println("Card back: " + playerBean.getHand().get(0).getCardBack());


        if(playerBean.getNickname().equals(nickname)){
            System.out.print("PRIORITY: ");
            for(AssistantCard assistantCard: playerBean.getHand()){
                System.out.print(assistantCard.getPriority() + "\t");
            }
            System.out.println();
            System.out.print("MOVEMENT: ");
            for(AssistantCard assistantCard: playerBean.getHand()){
                System.out.print(assistantCard.getMovement() + "\t");
            }
            System.out.println();
            System.out.println();
        }
            System.out.println("LAST PLAYED CARD");
            if( playerBean.getPlayedCard() != null) {
                System.out.println("PRIORITY: " + playerBean.getPlayedCard().getPriority() + "\t");
                System.out.println("MOVEMENT: " + playerBean.getPlayedCard().getMovement() + "\t");
            }else
                System.out.println("A card has not yet been played");
    }

    /**
     * This method is used to print all the clouds in the game
     */
    @Override
    public void displayClouds() {
        for(CloudBean cloudBean: gameBean.getCloudTiles()){
            System.out.println("CLOUD " + (gameBean.getCloudTiles().indexOf(cloudBean) + 1));
            for (Color color: Color.values()){
                for (int j = 0; j < cloudBean.getStudents().get(color); j++)
                    System.out.print(getAnsi(color) + ANSIConstants.FILLED_RECTANGLE + " ");
                System.out.print(ANSIConstants.TEXT_RESET);
                System.out.flush();
                if(cloudBean.getStudents().get(color) > 0)
                    System.out.println();
            }
        }
    }

    /**
     * This method is used to print all the information of an expert Cards
     */
    @Override
    public void displayExpertCard() {
        for(ExpertCardBean expertCardBean : gameBean.getExpertCards()){
            System.out.println(expertCardBean.getName() + "     Activation cost: " + expertCardBean.getActivationCost());
            if(expertCardBean instanceof StudBufferExpertCardBean){
                for (Color color: Color.values()){
                    for (int j = 0; j < ((StudBufferExpertCardBean) expertCardBean).getStudentBuffer().get(color); j++)
                        System.out.print(getAnsi(color) + ANSIConstants.FILLED_RECTANGLE + " ");
                    System.out.print(ANSIConstants.TEXT_RESET);
                    System.out.flush();
                    if(((StudBufferExpertCardBean) expertCardBean).getStudentBuffer().get(color) > 0)
                        System.out.println();
                }
            }
            if(expertCardBean instanceof BanExpertCardBean){
                for(int j = 0; j < ((BanExpertCardBean) expertCardBean).getNumBanToken(); j++)
                    System.out.print(ANSIConstants.FILLED_RECTANGLE + " ");
                System.out.print(ANSIConstants.TEXT_RESET);
                System.out.flush();
                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * This method is used to print the archipelago
     */
    @Override
    public void displayArchipelago() {
        StringBuilder stringStudents = new StringBuilder(100);
        StringBuilder towerColor = new StringBuilder(100);
        StringBuilder string = new StringBuilder(100);
        StringBuilder mother = new StringBuilder(100);
        int stringCounter = 0;
        int motherCounter = 0;
        int towerCounter = 0;
        int studentsCounter = 0;
        for(IslandBean islandBean : gameBean.getArchipelago()) {

            for(int i=0; i< Math.max(numStud(islandBean.getStudents()) + 3, 6); i++){
                string.append("-");
                stringCounter++;
            }


            stringStudents.append(ANSIConstants.TEXT_RESET + "|");
            studentsCounter++;

            for (int i = 0; i < islandBean.getStudents().get(Color.BLUE); i++) {
                stringStudents.append(ANSIConstants.BLUE + ANSIConstants.FILLED_RECTANGLE + ANSIConstants.TEXT_RESET);
                studentsCounter++;
            }
            for (int i = 0; i < islandBean.getStudents().get(Color.GREEN); i++) {
                stringStudents.append(ANSIConstants.GREEN + ANSIConstants.FILLED_RECTANGLE + ANSIConstants.TEXT_RESET);
                studentsCounter++;
            }
            for (int i = 0; i < islandBean.getStudents().get(Color.RED); i++) {
                stringStudents.append(ANSIConstants.RED + ANSIConstants.FILLED_RECTANGLE + ANSIConstants.TEXT_RESET);
                studentsCounter++;
            }
            for (int i = 0; i < islandBean.getStudents().get(Color.YELLOW); i++) {
                stringStudents.append(ANSIConstants.YELLOW + ANSIConstants.FILLED_RECTANGLE + ANSIConstants.TEXT_RESET);
                studentsCounter++;
            }
            for (int i = 0; i < islandBean.getStudents().get(Color.PINK); i++) {
                stringStudents.append(ANSIConstants.PURPLE + ANSIConstants.FILLED_RECTANGLE + ANSIConstants.TEXT_RESET);
                studentsCounter++;
            }
            while(stringCounter > studentsCounter){
                stringStudents.append(" ");
                studentsCounter++;
            }
            towerColor.append(ANSIConstants.TEXT_RESET + "|");
            towerCounter++;
            mother.append(ANSIConstants.TEXT_RESET+ "|");
            motherCounter++;
            if(islandBean.getNumTowers() > 0) {
                switch (islandBean.getTowerColor()) {
                    case WHITE -> towerColor.append(ANSIConstants.WHITE);
                    case BLACK -> towerColor.append(ANSIConstants.BLUE);
                    case GRAY -> towerColor.append(ANSIConstants.CYAN);
                }
                for(int i=0; i < islandBean.getNumTowers(); i++){
                    towerColor.append("T");
                    towerCounter++;
                }
            }
            if(gameBean.getMotherNature() == gameBean.getArchipelago().indexOf(islandBean)){
                mother.append("M");
                motherCounter++;
            }
            for(int i = 0; i < islandBean.getBanToken(); i++){
                mother.append("!");
                motherCounter++;
            }
            while(studentsCounter>towerCounter){
                towerColor.append(" ");
                towerCounter++;
            }
            while(towerCounter>motherCounter){
                mother.append(" ");
                motherCounter++;
            }
            towerColor.append( ANSIConstants.TEXT_RESET +"|/");
            towerCounter++;
            towerCounter++;
            stringStudents.append(ANSIConstants.TEXT_RESET + "|/");
            studentsCounter++;
            studentsCounter++;
            mother.append(ANSIConstants.TEXT_RESET + "|/");
            motherCounter++;
            motherCounter++;

        }
        System.out.println(string);
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();

        System.out.println(stringStudents);
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();

        System.out.println(towerColor);
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();

        System.out.println(mother);
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();

        System.out.println(string);
        System.out.print(ANSIConstants.TEXT_RESET);
        System.out.flush();
    }

    @Override
    public void displayWinner(String winner) {
        if(winner.equals(nickname)){
            System.out.println("Congratulations, you have just won!!");
        }else{
            System.out.println(winner + " has won!");
        }

    }

    /**
     * Helper method used to determine the number of student  in the map
     * @param map with students on it
     * @return total number of student in the map
     */
    private int numStud(Map<Color, Integer> map){
        int sum = 0;
        for (Color color : Color.values()) {
            sum += map.get(color);
        }
        return sum;
    }

    /**
     * Helper method used to get the ANSI escape code  associated to the Color ENUM
     * @param color color to convert in ANSI escape code
     * @return  the ANSI escape code  corresponding to the specified color
     */
    private String getAnsi(Color color) {
        String result = null;
        switch(color){
            case GREEN -> result = ANSIConstants.GREEN;
            case RED -> result = ANSIConstants.RED;
            case BLUE -> result = ANSIConstants.BLUE;
            case YELLOW -> result = ANSIConstants.YELLOW;
            case PINK -> result = ANSIConstants.PURPLE;
        }
        return result;
    }

}
