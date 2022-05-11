package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

import java.io.Serializable;

/**
 * This class represents the card that allows the player to swap a maximum of two students
 * from the entrance to the hall and vice versa.
 * @author Luca Bresciani
 */
public class SwapStudentsCard extends ExpertCard implements Serializable {
    private final SwapStudents board;
    private  int numOfStudentsToMove;
    private Color student1InEntranceColor;
    private Color student2InEntranceColor;
    private Color student1InHallColor;
    private Color student2InHallColor;

    /**
     * This SwapStudentsCard constructor is used when the player
     * choose to swap two students. It creates a card with the default cost of one.
     * @param board is the reference to game used for accessing the current player board
     */
    public SwapStudentsCard(SwapStudents board ){
        super(1);
        this.board = board;
    }

    public void setNumOfStudentsToMove(int numOfStudentToMove){
        if (numOfStudentToMove > 2)
            throw new IllegalArgumentException("You can move maximum 2 students");
        this.numOfStudentsToMove = numOfStudentToMove;
    }

    public int getNumOfStudentsToMove() {
        return this.numOfStudentsToMove;
    }

    public void setStudent1InEntranceColor(Color studentColor){
        this.student1InEntranceColor = studentColor;
    }

    public Color getStudent1InEntranceColor() {
        return this.student1InEntranceColor;
    }

    public void setStudent2InEntranceColor(Color studentColor) {
        this.student2InEntranceColor = studentColor;
    }

    public Color getStudent2InEntranceColor() {
        return student2InEntranceColor;
    }

    public void setStudent1InHallColor(Color studentColor) {
        this.student1InHallColor = studentColor;
    }

    public Color getStudent1InHallColor() {
        return student1InHallColor;
    }

    public void setStudent2InHallColor(Color student2InHallColor) {
        this.student2InHallColor = student2InHallColor;
    }

    public Color getStudent2InHallColor() {
        return student2InHallColor;
    }

    /**
     * This effect swap one or two students depending on the choice of the player.
     * It also increments the price of the card the first time this one is used.
     */
    @Override
    public void effect(){
        if (!isPlayed()) {
            played = true;
            price += 1;
        }
        if (numOfStudentsToMove == 1)
            board.swapStudents(student1InEntranceColor, student1InHallColor);
        else {
            board.swapStudents(student1InEntranceColor, student1InHallColor);
            board.swapStudents(student2InEntranceColor, student2InHallColor);
        }
    }
}
