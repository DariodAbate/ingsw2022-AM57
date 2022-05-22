package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.network.client.modelBean.ExpertCard.ExpertCardBean;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class contains all the element of a game
 *
 * @author Dario d'Abate
 */
public class GameBean implements Serializable {
    private int motherNature;
    private ArrayList<IslandBean> archipelago;
    private ArrayList<CloudBean> cloudTiles;
    private ArrayList<PlayerBean> players;
    private ArrayList<ExpertCardBean> expertCards;
    private int bank;
    private boolean expertGame;

    public void setMotherNature(int motherNature) {
        this.motherNature = motherNature;
    }

    public int getMotherNature() {
        return motherNature;
    }

    public void setArchipelago(ArrayList<IslandBean> archipelago) {
        this.archipelago = archipelago;
    }

    public ArrayList<IslandBean> getArchipelago() {
        return archipelago;
    }

    public void setPlayers(ArrayList<PlayerBean> players) {
        this.players = players;
    }

    public ArrayList<PlayerBean> getPlayers() {
        return players;
    }

    public void setCloudTiles(ArrayList<CloudBean> cloudTiles) {
        this.cloudTiles = cloudTiles;
    }

    public ArrayList<CloudBean> getCloudTiles() {
        return cloudTiles;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public int getBank() {
        return bank;
    }

    public void setExpertCards(ArrayList<ExpertCardBean> expertCards) {
        this.expertCards = expertCards;
    }

    public ArrayList<ExpertCardBean> getExpertCards() {
        return expertCards;
    }

    public boolean isExpertGame() {
        return expertGame;
    }

    public void setExpertGame(boolean expertGame) {
        this.expertGame = expertGame;
    }
}
