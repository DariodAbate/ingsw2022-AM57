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
    private Color studentInEntranceColor;
    private Color studentInHallColor;

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

    public void setStudentInEntranceColor(Color studentColor){
        this.studentInEntranceColor = studentColor;
    }

    public Color getStudentInEntranceColor() {
        return this.studentInEntranceColor;
    }

    public void setStudentInHallColor(Color studentColor) {
        this.studentInHallColor = studentColor;
    }

    public Color getStudentInHallColor() {
        return studentInHallColor;
    }

    /**
     * This effect swap one or two students depending on the choice of the player.
     * It also increments the price of the card the first time this one is used.
     */
    @Override
    public void effect(){
        board.swapStudents(studentInEntranceColor, studentInHallColor);
        if (!isPlayed()) {
            played = true;
            price += 1;
        }
    }
}
