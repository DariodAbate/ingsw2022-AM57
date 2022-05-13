package it.polimi.ingsw.model.constantFactory;

import java.io.Serializable;

/**
 * This class is a part of the factory pattern used for managing game's constants.
 * Here we override the base factory method create() so it returns a ThreePlayersConstants object.
 *
 * @author Dario d'Abate
 */
public class GameConstantsCreatorThreePlayers extends GameConstantsCreator implements Serializable {

    /**
     * @return a ThreePlayersConstants object
     */
    @Override
    public GameConstants create() {
        return new ThreePlayersConstants();
    }
}
