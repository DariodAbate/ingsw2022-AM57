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
        this.yellowStudents = new HashSet<>();
        this.blueStudents = new HashSet<>();
        this.redStudents = new HashSet<>();
        this.greenStudents = new HashSet<>();
        this.pinkStudents = new HashSet<>();
    }

    public void add(Color color) {
        if(color.equals(Color.YELLOW)){
            yellowStudents.add(color);
        }
        if (color.equals(Color.RED)){
            redStudents.add(color);
        }
        if(color.equals(Color.BLUE)){
            blueStudents.add(color);
        }
        if(color.equals(Color.GREEN)){
            greenStudents.add(color);
        }
        if(color.equals(Color.PINK)){
            pinkStudents.add(color);
        }
    }
    public void add(Color color, int num){
        for (int i=0; i<num; i++) {
            if (color.equals(Color.YELLOW)) {
                yellowStudents.add(color);
            }
            if (color.equals(Color.RED)) {
                redStudents.add(color);
            }
            if (color.equals(Color.BLUE)) {
                blueStudents.add(color);
            }
            if (color.equals(Color.GREEN)) {
                greenStudents.add(color);
            }
            if (color.equals(Color.PINK)) {
                pinkStudents.add(color);
            }
        }
    }
    public void remove(Color color){
        if(color.equals(Color.YELLOW)){
            yellowStudents.remove(color);
        }
        if (color.equals(Color.RED)){
            redStudents.remove(color);
        }
        if(color.equals(Color.BLUE)){
            blueStudents.remove(color);
        }
        if(color.equals(Color.GREEN)){
            greenStudents.remove(color);
        }
        if(color.equals(Color.PINK)){
            pinkStudents.remove(color);
        }

    }
    public void remove(Color color, int num){
        for(int i=0; i<num; i++) {
            if (color.equals(Color.YELLOW)) {
                yellowStudents.remove(color);
            }
            if (color.equals(Color.RED)) {
                redStudents.remove(color);
            }
            if (color.equals(Color.BLUE)) {
                blueStudents.remove(color);
            }
            if (color.equals(Color.GREEN)) {
                greenStudents.remove(color);
            }
            if (color.equals(Color.PINK)) {
                pinkStudents.remove(color);
            }
        }


    }
    public int numStudents(){
        return yellowStudents.size()+blueStudents.size()+ pinkStudents.size()+ redStudents.size()+ blueStudents.size();
    }
    public int numStudents(Color color){
        if (color.equals(Color.YELLOW)) {
            return yellowStudents.size();
        }
        if (color.equals(Color.RED)) {
            return redStudents.size();
        }
        if (color.equals(Color.BLUE)) {
            return blueStudents.size();
        }
        if (color.equals(Color.GREEN)) {
            return greenStudents.size();
        }
        if (color.equals(Color.PINK)) {
            return pinkStudents.size();
        }
        //fare exception per colori non validi!
        return -1;
    }
}

