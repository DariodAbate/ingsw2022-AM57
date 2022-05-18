package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.model.Color;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent a cloud tile
 *
 * @author Dario d'Abate
 */
public class CloudBean {
    private Map<Color, Integer > students;

    public CloudBean(){
        students = new HashMap<>();
    }


    public Map<Color, Integer> getStudents() {
        return students;
    }

    public void setStudents(Map<Color, Integer> students) {
        this.students = students;
    }
}
