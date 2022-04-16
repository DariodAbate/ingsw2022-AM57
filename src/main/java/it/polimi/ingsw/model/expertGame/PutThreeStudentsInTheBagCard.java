package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

public class PutThreeStudentsInTheBagCard extends ExpertCard {
    private final PutThreeStudentsInTheBag game;
    private final Color studentColor;

    public PutThreeStudentsInTheBagCard(Color studentColor, PutThreeStudentsInTheBag game) {
        super(3);
        this.game = game;
        this.studentColor = studentColor;
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
