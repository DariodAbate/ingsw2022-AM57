package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.answers.*;
import it.polimi.ingsw.network.server.answers.request.RequestExpertModeAnswer;
import it.polimi.ingsw.network.server.answers.request.RequestNicknameAnswer;
import it.polimi.ingsw.network.server.answers.request.RequestNumPlayerAnswer;
import it.polimi.ingsw.network.server.answers.request.StartAnswer;
import it.polimi.ingsw.network.server.answers.update.*;

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

    public void handleMessage(Answer answer){
        if(answer instanceof Shutdown)//Used to close the connection
            support.firePropertyChange(new PropertyChangeEvent(this,"stopSending", "", "notEmpty"));

        if(answer instanceof GenericAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "genericMessage", "", ((GenericAnswer) answer).getMessage()));
        if(answer instanceof RequestNicknameAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "requestNickname", "", answer.getMessage()));
        if(answer instanceof RequestNumPlayerAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "requestNumPlayer", "", answer.getMessage()));
        if(answer instanceof RequestExpertModeAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "requestExpertMode", "", answer.getMessage()));
        if(answer instanceof StartAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "startMessage", "", answer.getMessage()));
        if(answer instanceof TowerChoiceAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "towerChoice", null, answer.getMessage()));
        if(answer instanceof CardBackChoiceAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "cardBackChoice", null, answer.getMessage()));
        if(answer instanceof NicknameAnswer)
            support.firePropertyChange(new PropertyChangeEvent(this, "nickname", null , answer.getMessage()));
        if(answer instanceof GameStateAnswer) {
            support.firePropertyChange(new PropertyChangeEvent(this, "gameState", null, answer.getMessage()));
        }
        if(answer instanceof WinningAnswer) {
            support.firePropertyChange(new PropertyChangeEvent(this, "win", null, answer.getMessage()));
        }
        if(answer instanceof AssistantCardPlayedAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "cardPlayed", null , answer));
        }
        if(answer instanceof ToHallUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "toHall", null, answer));
        }
        if(answer instanceof ToIslandUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "toIsland", null, answer));
        }
        if(answer instanceof MotherNatureUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this,  "motherMovement", null, answer));
        }
        if(answer instanceof CloudsUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "cloudChoice", null, answer));
        }
        if(answer instanceof ExpertCardUpdateAnswer){
            support.firePropertyChange(new PropertyChangeEvent(this, "expertCard", null, answer));
        }
    }
}
