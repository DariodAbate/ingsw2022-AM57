package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.answers.Answer;
import it.polimi.ingsw.network.server.answers.GenericAnswer;
import it.polimi.ingsw.network.server.answers.Shutdown;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class is used to generate event based on the server's answer
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

    }
}
