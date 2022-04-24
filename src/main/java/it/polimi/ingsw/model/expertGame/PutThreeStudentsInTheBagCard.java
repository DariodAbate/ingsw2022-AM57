package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

public class PutThreeStudentsInTheBagCard extends ExpertCard {
    private final PutThreeStudentsInTheBag game;
    private Color studentColor;

    public PutThreeStudentsInTheBagCard(PutThreeStudentsInTheBag game) {
        super(3);
        this.game = game;
    }

    public void setStudentColor(Color studentColor) {
        this.studentColor = studentColor;
    }

    public Color getStudentColor() {
        return studentColor;
    }

    @Override
    public void effect() {
        if (!isPlayed()) {
            played = true;
            price += 1;
        }
        game.putThreeStudentsInTheBag(studentColor);
    }

}
