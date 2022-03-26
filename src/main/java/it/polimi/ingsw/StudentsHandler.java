package it.polimi.ingsw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
//The control about the increment of the tokens is controlled by the methods isAddable and isRemovable.
//The add function just

public class StudentsHandler {
    private static final int MAXSIZE = 26;
    private HashMap<Color, Integer> students;
    //maximum size that you can have for every single color, if not specified it is 26
    int sizeMax;

    public StudentsHandler() {
        students = new HashMap<>();
        for (Color color:
             Color.values()) {
            students.put(color, 0);
        }
        this.sizeMax = MAXSIZE;
    }

    public StudentsHandler(int size){
        students = new HashMap<>();
        for (Color color:
                Color.values()) {
            students.put(color, 0);
        }
        if(size > MAXSIZE || size<0){
            throw new IllegalArgumentException("Il numero di pedine non può superare 26");
        }
        else{
            this.sizeMax = size;
        }
    }

    public void add(Color color) {
            Integer temp;
            temp = students.get(color);
            if(temp+1<=sizeMax) {
                students.put(color, temp + 1);
            }
    }

    public void add(Color color, int num){
            Integer temp;
            temp = students.get(color);
            if (num<0){
                throw new IllegalArgumentException("Il numero non può essere negativo");
            }
            if(temp+num >= sizeMax){
                students.put(color, sizeMax); //Careful, you can lose some tokens in the exchange process!
            }
            else{
                students.put(color, temp+num);
            }
    }

    public boolean isAddable(Color color){
        if(students.get(color)+1<=sizeMax) return true;
        return false;
    }

    public boolean isAddable(Color color, int num){
        if(students.get(color)+num<=sizeMax) return true;
        return false;
    }
    //if called in an already empty students, does nothing
    public void remove(Color color){
            Integer temp;
            temp = students.get(color);
            if(temp>0){
                students.put(color, temp-1);
            }
            else{
                students.put(color, 0);
            }
    }
    public void remove(Color color, int num) throws IllegalArgumentException{
            if (num<0){
                throw new IllegalArgumentException();
            }
            Integer temp;
            temp = students.get(color);
            if(temp-num>0){
                students.put(color, temp-num);
            }
            else{
                students.put(color, 0);
            }

    }
    public boolean isRemovable(Color color){
        if(students.get(color)-1>=0) return true;
        return false;
    }

    public boolean isRemovable(Color color, int num){
        if(students.get(color)-num>=0) return true;
        return false;
    }

    public int numStudents(){
        int sum = 0;
        for (Color color :
                Color.values()) {
            sum += students.get(color);
        }
        return sum;
    }
    public int numStudents(Color color) {
        if(color.equals(null)){
            throw new NullPointerException("Colore null");
        }
        return students.get(color);
    }
}

