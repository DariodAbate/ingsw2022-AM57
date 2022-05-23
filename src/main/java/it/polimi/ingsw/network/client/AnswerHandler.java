package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.answers.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class is used to generate event based on the server's answer. These events will be captured by an instance of the UI
 *
 * @author Dario d'Abate
 */
public class AnswerHandler {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this); //with this object we will fire the property change event

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        support.addPropertyChangeListener(pcl);
    }

    public void handleMessage(Answer answer){ //FIRE DI EVENTI
        if(answer instanceof Shutdown)//Used to close the connection
            support.firePropertyChange(new PropertyChangeEvent(this,"stopSending", "", "notEmpty"));

        if(answer instanceof GenericAnswer)  //usato fino a questo momento
            support.firePropertyChange(new PropertyChangeEvent(this, "genericMessage", "", ((GenericAnswer) answer).getMessage()));

        if(answer instanceof TowerChoiceAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "towerChoice", null, ((TowerChoiceAnswer) answer).getMessage()));
        if(answer instanceof CardBackChoiceAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "cardBackChoice", null, ((CardBackChoiceAnswer)answer).getMessage()));
        if(answer instanceof NicknameAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "nickname", null ,((NicknameAnswer)answer).getMessage()));
        if(answer instanceof GameStateAnswer) {
            support.firePropertyChange(new PropertyChangeEvent(this, "gameState", null, ((GameStateAnswer) answer).getMessage()));
        }
        if(answer instanceof AssistantCardPlayedAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "cardPlayed", null , (AssistantCardPlayedAnswer)answer));
        }
        if(answer instanceof ToHallUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "toHall", null, (ToHallUpdateAnswer)answer));
        }
        if(answer instanceof ToIslandUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "toIsland", null, (ToIslandUpdateAnswer)answer));
        }
        if(answer instanceof MotherNatureUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this,  "motherMovement", null,(MotherNatureUpdateAnswer)answer));
        }
        if(answer instanceof CloudsUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "cloudChoice", null, (CloudsUpdateAnswer) answer));
        }
        if(answer instanceof ExpertCardUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "expertCard", null, (ExpertCardUpdateAnswer) answer));
        }
    }
}
