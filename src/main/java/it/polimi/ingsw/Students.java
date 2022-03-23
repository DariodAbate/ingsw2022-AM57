package it.polimi.ingsw;

import java.util.HashSet;
import java.util.Set;
// da rifare con gli int
public class Students {
    private int yellowStudents;
    private int blueStudents;
    private int redStudents;
    private int greenStudents;
    private int pinkStudents;

    public Students() {
        this.yellowStudents = 0;
        this.blueStudents = 0;
        this.redStudents = 0;
        this.greenStudents = 0;
        this.pinkStudents = 0;
    }

    public void add(Color color) {
        if(color.equals(Color.YELLOW)){
            yellowStudents+=1;
        }
        if (color.equals(Color.RED)){
            redStudents+=1;
        }
        if(color.equals(Color.BLUE)){
            blueStudents+=1;
        }
        if(color.equals(Color.GREEN)){
            greenStudents+=1;
        }
        if(color.equals(Color.PINK)){
            pinkStudents+=1;
        }
    }
    public void add(Color color, int num){

            if (color.equals(Color.YELLOW)) {
                yellowStudents+=num;
            }
            if (color.equals(Color.RED)) {
                redStudents+=num;
            }
            if (color.equals(Color.BLUE)) {
                blueStudents+=num;
            }
            if (color.equals(Color.GREEN)) {
                greenStudents+=num;
            }
            if (color.equals(Color.PINK)) {
                pinkStudents+=num;
            }


    }
    public void remove(Color color){
        if(color.equals(Color.YELLOW)){
                //throw exception
            yellowStudents-=1;
        }
        if (color.equals(Color.RED)){
            redStudents-=1;
        }
        if(color.equals(Color.BLUE)){
            blueStudents-=1;
        }
        if(color.equals(Color.GREEN)){
            greenStudents-=1;
        }
        if(color.equals(Color.PINK)){
            pinkStudents-=1;
        }

    }
    public void remove(Color color, int num){
            if (color.equals(Color.YELLOW)) {
                yellowStudents-=num;
            }
            if (color.equals(Color.RED)) {
                redStudents-=num;
            }
            if (color.equals(Color.BLUE)) {
                blueStudents-=num;
            }
            if (color.equals(Color.GREEN)) {
                greenStudents-=num;
            }
            if (color.equals(Color.PINK)) {
                pinkStudents-=num;
            }
        }



    public int numStudents(){
        return yellowStudents+blueStudents+ pinkStudents+ redStudents+ greenStudents;
    }
    public int numStudents(Color color){
        if (color.equals(Color.YELLOW)) {
            return yellowStudents;
        }
        if (color.equals(Color.RED)) {
            return redStudents;
        }
        if (color.equals(Color.BLUE)) {
            return blueStudents;
        }
        if (color.equals(Color.GREEN)) {
            return greenStudents;
        }
        if (color.equals(Color.PINK)) {
            return pinkStudents;
        }
        //fare exception per colori non validi!
        return -1;
    }
}

