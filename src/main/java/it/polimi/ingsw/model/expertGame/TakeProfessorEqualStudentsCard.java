package it.polimi.ingsw.model.expertGame;

/**
 * This class implements a single expert card
 * @author Dario d'Abate
 */
public class TakeProfessorEqualStudentsCard  extends ExpertCard{
   private final TakeProfessorEqualStudents game;

    /**
     * Standard constructor for a card
     * @param game interface of ExpertGame class that exposes only certain method
     */
    public TakeProfessorEqualStudentsCard(ExpertGame game) {
        super(2);
        this.game = game;
    }

    /**
     * This method simulate the effect of a card, by setting a flag in Game class as true.
     * The current player can take control of the professors even if it has the same number of students in his hall as the
     * player currently controlling them
     */
    @Override
    public void effect() throws NotExistingStudentException {
        if(!isPlayed()){
            played = true;
            price += 1;
        }
        game.setTakeProfessorEqualStudents();
    }
}
