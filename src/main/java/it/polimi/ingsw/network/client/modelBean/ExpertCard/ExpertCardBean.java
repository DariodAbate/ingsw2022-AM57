package it.polimi.ingsw.network.client.modelBean.ExpertCard;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent an expert card with a simple effect
 *
 * @author Dario d'Abate
 */
public class ExpertCardBean {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
