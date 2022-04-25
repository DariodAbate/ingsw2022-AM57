package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.StudentsHandler;

/**
 * This class implements the expert card system by clustering three cards
 * @author Dario d'Abate
 */
public class StudentsBufferCardsCluster extends ExpertCard {
    private static final int MAN_CARD_INDEX = 0;
    private static final int MAN_CARD_COST = 1;
    private static final int MAN_CARD_BUFFER_SIZE = 4;
    private static final int CLOWN_CARD_INDEX = 1;
    private static final int CLOWN_CARD_COST = 1;
    private static final int CLOWN_CARD_BUFFER_SIZE = 6;
    private static final int WOMAN_CARD_INDEX = 2;
    private static final int WOMAN_CARD_COST = 2;
    private static final int WOMAN_CARD_BUFFER_SIZE = 4;

    private StudentsHandler studBuffer;
    private StudentsBufferCluster game;
    private final int index ;
    private final int cardBufferSize;

    private Color studentColorToBeMoved;
    private int idxChosenIsland;
    private Color studentColorInEntrance;

    /**
     * Constructor of the class, it populates the card with student tokens, according to its size
     * @param index of the card to be instantiated inside the cluster
     * @param game interface of ExpertGame class that exposes only certain method
     */
    public StudentsBufferCardsCluster(int index, ExpertGame game) {
        super(0); //temporary value
        this.game = game;
        this.index = index;

        if(index == MAN_CARD_INDEX){
            this.price = MAN_CARD_COST;
            studBuffer = new StudentsHandler(MAN_CARD_BUFFER_SIZE);
            cardBufferSize = MAN_CARD_BUFFER_SIZE;
        }else if(index == CLOWN_CARD_INDEX){
            this.price = CLOWN_CARD_COST;
            studBuffer = new StudentsHandler(CLOWN_CARD_BUFFER_SIZE);
            cardBufferSize = CLOWN_CARD_BUFFER_SIZE;
        }else if(index == WOMAN_CARD_INDEX){
            this.price = WOMAN_CARD_COST;
            studBuffer = new StudentsHandler(WOMAN_CARD_BUFFER_SIZE);
            cardBufferSize = WOMAN_CARD_BUFFER_SIZE;
        }else
            throw new IllegalArgumentException("L'indice della carta non è valido!");

        refillStudBuffer();
    }

    //helper method to refill student tokens on a card
    private void refillStudBuffer(){
        while(studBuffer.numStudents() < cardBufferSize) {
            Color drawColor = game.draw();
            if (drawColor == null) {
                //TODO endgame condition
            }
            studBuffer.add(drawColor);
        }
    }

    /**
     * This method is used to set the color of a student that has to  be moved from a card. If is required to
     * move multiple student, this method will be invoked as many times as the number of student.
     * The control over the existence of that student on the card is done by the controller
     * @param studentColorToBeMoved color of the student to be moved from a card
     */
    public void setStudentColorToBeMoved(Color studentColorToBeMoved) {
        this.studentColorToBeMoved = studentColorToBeMoved;
    }

    /**
     * This method gets the color of the student that will be moved with the next call of effect()
     * @return color of the student that will be moved
     */
    public Color getStudentColorToBeMoved() {
        return studentColorToBeMoved;
    }

    /**
     * This method is invoked to set the index of the island to which the student will be moved
     * from the man's expert card.
     * The control over the existence of that index is done by the controller
     * @param idxChosenIsland index of the island to which move the student
     */
    public void setIdxChosenIsland(int idxChosenIsland) {
        this.idxChosenIsland = idxChosenIsland;
    }

    /**
     * This method gets the index of the island to which the student will be moved
     * from the man's expert card with the next call of effect()
     * @return index of the island to which move the student
     */
    public int getIdxChosenIsland() {
        return idxChosenIsland;
    }

    /**
     * This method is used to set the color of a student that has to  be moved from the board's entrance of the current player.
     * If is required to move multiple student, this method will be invoked as many times as the number of student.
     * The control over the existence of that student in the entrance is done by the controller
     * @param studentColorInEntrance color of the student to be moved from a card
     */
    public void setStudentColorInEntrance(Color studentColorInEntrance) {
        this.studentColorInEntrance = studentColorInEntrance;
    }

    /**
     * This method gets the color of the student that will be moved from the board's entrance of the current player
     * with the next call of effect()
     * @return color of the student that will be moved from entrance
     */
    public Color getStudentColorInEntrance() {
        return studentColorInEntrance;
    }

    /**
     * This method simulate the effect of a card. Having gathered 3 cards in a cluster there are 3 effects.<br/>
     * - Man's card: removes a student of the specified color on the card and moves it on a specified island tile,
     *  then refills the card with a single student.<br/>
     * - Clown's card : swap a student of the specified color on the card with one in the board's hall of the current player<br/>
     * - Woman's card: removes a student of the specified color on the card and moves it in the board's hall of the current player,
     * then refills the card with a single student. If the hall is full, all is unchanged
     * @throws IllegalArgumentException when there is no student of the specified color to move from the card
     * @throws  IndexOutOfBoundsException when is chosen an index which does not have a
     * corresponding island tile in the archipelago arrayList, used by the woman card
     */
    @Override
    public void effect(){
        //Do not increase the cost and do not play the card if the parameters are incorrect
        if(studBuffer.numStudents(studentColorToBeMoved) > 0) {
            if (index == MAN_CARD_INDEX) {
                try {
                    game.fromManCardToIsland(idxChosenIsland, studentColorToBeMoved);
                    studBuffer.remove(studentColorToBeMoved);
                    usingCard();
                    refillStudBuffer();
                }catch (IndexOutOfBoundsException e){throw new IndexOutOfBoundsException("The specified island tile does not exist!");}
            } else if (index == CLOWN_CARD_INDEX) {
                try {
                    game.fromClownCardToEntrance(studentColorToBeMoved, studentColorInEntrance);
                    studBuffer.remove(studentColorToBeMoved);
                    studBuffer.add(studentColorInEntrance);
                    usingCard();
                }catch(IllegalArgumentException e){throw new IllegalArgumentException(e.getMessage());}
            } else {
                if(game.fromWomanCardToHall(studentColorToBeMoved)) {
                    studBuffer.remove(studentColorToBeMoved);
                    usingCard();
                    refillStudBuffer();
                }
            }
        }else {
            throw new IllegalArgumentException("Lo studente da muovere non è presente sulla carta!");
        }
    }
    //helper method used for playing a card
    private void usingCard(){
        if(!isPlayed()){
            played = true;
            price += 1;
        }
    }

    /**
     * Method used only for testing
     * @return the reference to the studentsHandler contained in the class
     */
    public StudentsHandler getStudBuffer() {
        return studBuffer;
    }
}
