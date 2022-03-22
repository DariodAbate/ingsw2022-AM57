package it.polimi.ingsw;

import java.util.HashSet;
import java.util.Set;

public class Student {
    private Set<Color> yellowStudents;
    private Set<Color> blueStudents;
    private Set<Color> redStudents;
    private Set<Color> greenStudents;
    private Set<Color> pinkStudents;

    public Student() {
        this.yellowStudents = new HashSet<Color>();
        this.blueStudents = new HashSet<Color>();
        this.redStudents = new HashSet<Color>();
        this.greenStudents = new HashSet<Color>();
        this.pinkStudents = new HashSet<Color>();
    }

    public void add(Color color) {
        if(color.equals(Color.yellow)){
            yellowStudents.add(color);
        }
        if (color.equals(Color.red)){
            redStudents.add(color);
        }
        if(color.equals(Color.blue)){
            blueStudents.add(color);
        }
        if(color.equals(Color.green)){
            greenStudents.add(color);
        }
        if(color.equals(Color.pink)){
            pinkStudents.add(color);
        }
    }
    public void add(Color color, int num){
        for (int i=0; i<num; i++) {
            if (color.equals(Color.yellow)) {
                yellowStudents.add(color);
            }
            if (color.equals(Color.red)) {
                redStudents.add(color);
            }
            if (color.equals(Color.blue)) {
                blueStudents.add(color);
            }
            if (color.equals(Color.green)) {
                greenStudents.add(color);
            }
            if (color.equals(Color.pink)) {
                pinkStudents.add(color);
            }
        }
    }
    public void remove(Color color){
        if(color.equals(Color.yellow)){
            yellowStudents.remove(color);
        }
        if (color.equals(Color.red)){
            redStudents.remove(color);
        }
        if(color.equals(Color.blue)){
            blueStudents.remove(color);
        }
        if(color.equals(Color.green)){
            greenStudents.remove(color);
        }
        if(color.equals(Color.pink)){
            pinkStudents.remove(color);
        }

    }
    public void remove(Color color, int num){
        for(int i=0; i<num; i++) {
            if (color.equals(Color.yellow)) {
                yellowStudents.remove(color);
            }
            if (color.equals(Color.red)) {
                redStudents.remove(color);
            }
            if (color.equals(Color.blue)) {
                blueStudents.remove(color);
            }
            if (color.equals(Color.green)) {
                greenStudents.remove(color);
            }
            if (color.equals(Color.pink)) {
                pinkStudents.remove(color);
            }
        }


    }
    public int numStudents(){
        return yellowStudents.size()+blueStudents.size()+ pinkStudents.size()+ redStudents.size()+ blueStudents.size();
    }
    public int numStudents(Color color){
        if (color.equals(Color.yellow)) {
            return yellowStudents.size();
        }
        if (color.equals(Color.red)) {
            return redStudents.size();
        }
        if (color.equals(Color.blue)) {
            return blueStudents.size();
        }
        if (color.equals(Color.green)) {
            return greenStudents.size();
        }
        if (color.equals(Color.pink)) {
            return pinkStudents.size();
        }
        //fare exception per colori non validi!
        return -1;
    }
}

