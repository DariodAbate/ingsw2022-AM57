package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

/**
 * This class represents the card that allows the player to swap a maximum of two students
 * from the entrance to the hall and vice versa.
 * @author Luca Bresciani
 */
public class SwapStudentsCard extends ExpertCard {
    private final SwapStudents board;
    private final int numOfStudentsToMove;
    private final Color student1InEntranceColor;
    private Color student2InEntranceColor;
    private final Color student1InHallColor;
    private Color student2InHallColor;

    /**
     * This SwapStudentsCard constructor is used when the player
     * choose to swap two students. It creates a card with the default cost of one.
     * @param numOfStudentsToMove is the number of students to swap chosen by the player
     * @param entrance1Color is the color of the first student in the entrance that the player
     *                       want to move
     * @param entrance2Color is the color of the second student in the entrance that the player
     *                       want to move
     * @param hall1Color is the color of the first student in the hall that the player want to move
     * @param hall2Color is the color of the second student in the hall that the player want to move
     * @param board is the reference to game used for accessing the current player board
     */
    public SwapStudentsCard(int numOfStudentsToMove, Color entrance1Color,
                            Color entrance2Color, Color hall1Color, Color hall2Color,
                            SwapStudents board ){
        super(1);
        if (numOfStudentsToMove > 2)
            throw new IllegalArgumentException("You can move maximum 2 students");
        this.board = board;
        this.numOfStudentsToMove = numOfStudentsToMove;
        this.student1InEntranceColor = entrance1Color;
        this.student2InEntranceColor = entrance2Color;
        this.student1InHallColor = hall1Color;
        this .student2InHallColor = hall2Color;
    }

    /**
     * This SwapStudentsCard constructor is used when the player
     * chose to swap just one student
     * @param numOfStudentsToMove is the number of students to swap chosen by the player
     * @param entrance1Color is the color of the student in the entrance that the player want to move
     * @param hall1Color is the color of the student in the hall that the player want to move
     * @param board is the reference to game used for accessing the current player board
     */
    public SwapStudentsCard(int numOfStudentsToMove, Color entrance1Color,
                            Color hall1Color, SwapStudents board) {
        super(1);
        if (numOfStudentsToMove > 2)
            throw new IllegalArgumentException("You can move maximum 2 students");
        this.board = board;
        this.numOfStudentsToMove = numOfStudentsToMove;
        this.student1InEntranceColor = entrance1Color;
        this.student1InHallColor = hall1Color;
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
