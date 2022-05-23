package it.polimi.ingsw.network.client.modelBean.ExpertCard;


import it.polimi.ingsw.network.client.view.ExpertCard_ID;

import java.io.Serializable;

/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent an expert card with a simple effect
 *
 * @author Dario d'Abate
 */
public class ExpertCardBean  implements Serializable {
    private ExpertCard_ID name;
    private int activationCost;

    public ExpertCard_ID getName() {
        return name;
    }

    public void setName(ExpertCard_ID name) {
        this.name = name;
    }

    public int getActivationCost() {
        return activationCost;
    }

    public void setActivationCost(int activationCost) {
        this.activationCost = activationCost;
    }
}
