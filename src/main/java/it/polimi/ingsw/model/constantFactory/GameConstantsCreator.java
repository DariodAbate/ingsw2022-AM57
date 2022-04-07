package it.polimi.ingsw.model.constantFactory;

/**
 * This abstract class is a part of the factory pattern used for managing game's constants.
 * Here we declare the factory method that returns new GameConstants objects.
 *
 * @author Dario d'Abate
 */
public abstract class GameConstantsCreator {
    /**
     * @return a new GameConstants objects
     */
    public abstract GameConstants create();
}
