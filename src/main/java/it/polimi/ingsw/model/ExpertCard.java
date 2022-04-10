package it.polimi.ingsw.model;

/**
 * This class implements the expert card system
 * @author Lorenzo Corrado
 */
public abstract class ExpertCard {
    protected int price;
    protected boolean played;
    public void effect(){

    }

    /**
     *
     * @return if the card has been played or not
     */
    public boolean hasPlayed(){
        return played;
    }
}
