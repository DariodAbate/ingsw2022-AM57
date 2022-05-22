package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.network.client.modelBean.GameBean;

/**
 * This class represent the answer given to the clients used to initialize the view.

 * @author Dario d'Abate
 */
public class GameStateAnswer implements Answer{
    GameBean game;
    public GameStateAnswer(GameBean game){
        this.game = game;
    }

    @Override
    public Object getMessage() {
        return game;
    }
}
