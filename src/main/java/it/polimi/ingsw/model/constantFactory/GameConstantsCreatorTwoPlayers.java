package it.polimi.ingsw.model.constantFactory;

import java.io.Serializable;

/**
 * This class is a part of the factory pattern used for managing game's constants.
 * Here we override the base factory method create() so it returns a TwoPlayersConstants object.
 *
 * @author Dario d'Abate
 */
public class GameConstantsCreatorTwoPlayers  extends GameConstantsCreator implements Serializable {

    /**
     * @return a TwoPlayersConstants object
     */
    @Override
    public GameConstants create() {
        return new TwoPlayersConstants();
    }
}
