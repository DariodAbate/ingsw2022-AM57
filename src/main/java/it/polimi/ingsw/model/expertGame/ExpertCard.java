package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

/**
 * This class implements the expert card system
 * @author Lorenzo Corrado
 */
public abstract class ExpertCard {
    protected int price;
    protected boolean played = false;
    public ExpertCard(int price){
        this.price = price;
    }

    /**
     *
     * @return the current price of the card
     */
    public int getPrice() {
        return price;
    }

    /**
     * Play the effect of the card
     */
    public void effect() throws NotExistingStudentException {}

    /**
     *
     * @return true if the card has been played at least one time, false otherwise
     */
    public boolean isPlayed(){
        return played;
    }

    /**
     * Change the island where the player wants the effect to be activated
     * @param index of the island
     */
    public void changeIslandIndex(int index){}

    public void setStudentColorInEntrance(Color studentColorInEntrance){}

    public void setIdxChosenIsland(int idxChosenIsland){}

    public void setStudentColorToBeMoved(Color studentColorToBeMoved){}

    public void changeColor(Color color){}
}
