package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.network.client.modelBean.BoardBean;
import it.polimi.ingsw.network.client.modelBean.IslandBean;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player moves mother nature.
 * This class provides you with the following attributes:
 * -Mother nature updated
 * -All the boards  updated
 * -The archipelago updated
 *
 * @author Dario d'Abate
 */
public class MotherNatureUpdateAnswer implements Answer{
    private final int updatedMotherNature ;
    ArrayList<BoardBean> updatedBoards;
    ArrayList<IslandBean> updatedArchipelago;

    public MotherNatureUpdateAnswer(int updatedMotherNature, ArrayList<BoardBean> updatedBoards, ArrayList<IslandBean> updatedArchipelago){
        this.updatedMotherNature = updatedMotherNature;
        this.updatedBoards = updatedBoards;
        this.updatedArchipelago = updatedArchipelago;

    }

    public ArrayList<IslandBean> getUpdatedArchipelago() {
        return updatedArchipelago;
    }

    public ArrayList<BoardBean> getUpdatedBoards() {
        return updatedBoards;
    }

    public int getUpdatedMotherNature() {
        return updatedMotherNature;
    }

    @Override
    public Object getMessage() {
        return null;
    }


}
