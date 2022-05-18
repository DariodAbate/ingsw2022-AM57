package it.polimi.ingsw.network.client.modelBean.ExpertCard;

import it.polimi.ingsw.model.Color;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class is a more specific version of the ExpertCardBean because it contains the logic to manages the students on this card
 *
 * @author Dario d'Abate
 */
public class StudBufferExpertCardBean extends ExpertCardBean {
    private Map<Color, Integer> studentBuffer;

    public StudBufferExpertCardBean(){
        studentBuffer = new HashMap<>();
    }

    public Map<Color, Integer> getStudentBuffer() {
        return studentBuffer;
    }

    public void setStudentBuffer(Map<Color, Integer> studentBuffer) {
        this.studentBuffer = studentBuffer;
    }
}
