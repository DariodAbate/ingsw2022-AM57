package it.polimi.ingsw.network.client.modelBean.ExpertCard;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class is a more specific version of the ExpertCardBean because it contains the logic to manage the ban token
 *
 * @author Dario d'Abate
 */
public class BanExpertCardBean {
    int numBanToken;

    public int getNumBanToken() {
        return numBanToken;
    }

    public void setNumBanToken(int numBanToken) {
        this.numBanToken = numBanToken;
    }
}
