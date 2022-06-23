package it.polimi.ingsw.network.server.answers.update;

import it.polimi.ingsw.network.client.modelBean.BoardBean;
import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;
import it.polimi.ingsw.network.client.modelBean.IslandBean;
import it.polimi.ingsw.network.server.answers.Answer;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player moves mother nature.
 * This class provides you with the following attributes:
 * -All the Expert Cards updated
 * -All the boards  updated
 * -The archipelago updated
 *
 * @author Dario d'Abate
 */
public class ExpertCardUpdateAnswer implements Answer {
    ArrayList<ExpertCardBean> updatedExpertCards = null;
    ArrayList<IslandBean> updatedArchipelago = null;
    ArrayList<BoardBean> updatedBoards = null;

    public void setUpdatedArchipelago(ArrayList<IslandBean> updatedArchipelago) {
        this.updatedArchipelago = updatedArchipelago;
    }

    public void setUpdatedBoards(ArrayList<BoardBean> updatedBoards) {
        this.updatedBoards = updatedBoards;
    }

    public void setUpdatedExpertCards(ArrayList<ExpertCardBean> updatedExpertCards) {
        this.updatedExpertCards = updatedExpertCards;
    }

    public ArrayList<BoardBean> getUpdatedBoards() {
        return updatedBoards;
    }

    public ArrayList<IslandBean> getUpdatedArchipelago() {
        return updatedArchipelago;
    }

    public ArrayList<ExpertCardBean> getUpdatedExpertCards() {
        return updatedExpertCards;
    }

    @Override
    public Object getMessage() {
        return null;
    }
}
