package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.statePattern.ExceptOneColorCalculator;
import it.polimi.ingsw.model.statePattern.InfluenceCalculator;
import it.polimi.ingsw.model.statePattern.NoTowerCalculator;
import it.polimi.ingsw.model.statePattern.TwoMoreCalculator;

/**
 * This class implements three expert cards that change the way influence is calculated
 * @author Lorenzo Corrado
 */
public class InfluenceCardsCluster extends ExpertCard{
    private int INDEX_COLOREXCEPTION = 2;
    private InfluenceCalculator[] cards = new InfluenceCalculator[]{
            new NoTowerCalculator(),
            new TwoMoreCalculator(),
            new ExceptOneColorCalculator(this)
    };
    private int index;
    private InfluenceCluster game;
    private Color color; //this serves only for EXCEPT_ONE_COLOR_CALCULATOR
    public InfluenceCardsCluster(int cardIndex, ExpertGame game){
        super(3);
        this.game = game;
        this.index = cardIndex;
        if (cardIndex != 0) {
            if(cardIndex == 1){
                this.price = 2;
            }
            else if (cardIndex== INDEX_COLOREXCEPTION){
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
        if(index==INDEX_COLOREXCEPTION && color == null){
            throw new IllegalStateException("Devi scegliere un colore prima di attivare questo effetto!");//FIXME
        }
        game.changeCalculator(cards[index]);
    }
    public void changeColor(Color color){
        if(index==INDEX_COLOREXCEPTION){
            this.color = color;
        }
    }

    public Color getColor() {
        return color;
    }
}


