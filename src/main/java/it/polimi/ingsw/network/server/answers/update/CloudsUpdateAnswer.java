package it.polimi.ingsw.network.server.answers.update;

import it.polimi.ingsw.network.client.modelBean.BoardBean;
import it.polimi.ingsw.network.client.modelBean.CloudBean;
import it.polimi.ingsw.network.server.answers.Answer;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player choose a cloud.
 * This class provides you with the following attributes:
 * -All the boards  updated
 * -All the clouds updated
 *
 * @author Dario d'Abate
 */
public class CloudsUpdateAnswer implements Answer {
    ArrayList<BoardBean> updatedBoards;
    ArrayList<CloudBean> updateClouds;

    public CloudsUpdateAnswer(ArrayList<BoardBean> updatedBoards, ArrayList<CloudBean> updateClouds){
        this.updatedBoards = updatedBoards;
        this.updateClouds = updateClouds;
    }

    public ArrayList<BoardBean> getUpdatedBoards() {
        return updatedBoards;
    }

    public ArrayList<CloudBean> getUpdateClouds() {
        return updateClouds;
    }

    @Override
    public Object getMessage() {
        return null;
    }
}
