package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RoundObserver;
import it.polimi.ingsw.model.statePattern.ExceptOneColorCalculator;
import it.polimi.ingsw.model.statePattern.InfluenceCalculator;
import it.polimi.ingsw.model.statePattern.NoTowerCalculator;
import it.polimi.ingsw.model.statePattern.TwoMoreCalculator;

import java.io.Serializable;

/**
 * This class implements three expert cards that change the way influence is calculated
 * @author Lorenzo Corrado
 */
public class InfluenceCardsCluster extends ExpertCard implements Serializable {
    private final int INDEX_TWO_MORE = 1;
    private final int INDEX_COLOR_EXCEPTION = 2;
    private InfluenceCalculator[] cards = new InfluenceCalculator[]{
            new NoTowerCalculator(),
            new TwoMoreCalculator(this),
            new ExceptOneColorCalculator(this)
    };
    private int index;
    private InfluenceCluster game;
    private RoundObserver round;
    private Color color; //this serves only for EXCEPT_ONE_COLOR_CALCULATOR
    private Player player;
    public InfluenceCardsCluster(int cardIndex, ExpertGame game){
        super(3);
        this.game = game;
        this.round = game;
        this.index = cardIndex;
        if (cardIndex != 0) {
            if(cardIndex == INDEX_TWO_MORE){
                this.price = 2;
            }
            else if (cardIndex== INDEX_COLOR_EXCEPTION){
                this.price = 3;
            }
            else{
                throw new IllegalArgumentException("L'indice della carta non è valido!");
            }
        }
    }

    @Override
    public void effect() {
        if(!isPlayed()){
            played = true;
            price += 1;
        }
        if(index== INDEX_COLOR_EXCEPTION && color == null){
            throw new IllegalStateException("Devi scegliere un colore prima di attivare questo effetto!");
        }
        game.changeCalculator(cards[index]);
    }
    public void changeColor(Color color){
        if(index== INDEX_COLOR_EXCEPTION){
            this.color = color;
        }
    }

    public Color getColor() {
        return color;
    }

    public RoundObserver getRound(){
        return round;
    }

    public int getIndex() {
        return index;
    }
}


