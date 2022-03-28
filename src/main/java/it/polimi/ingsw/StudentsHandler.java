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
            if(color == null){
                throw new NullPointerException("Colore null");
            }

            Integer temp;
            temp = students.get(color);
            if(temp+1<=sizeMax) {
                students.put(color, temp + 1);
            }
    }

    public void add(Color color, int num){
            if(color == null){
                throw new NullPointerException("Colore null");
            }

            Integer temp;
            temp = students.get(color);
            if (num<0){
                throw new IllegalArgumentException("Il numero non può essere negativo");
            }
        students.put(color, Math.min(temp + num, sizeMax)); //Careful, you can lose some tokens in the exchange process!
    }

    public boolean isAddable(Color color){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) + 1 <= sizeMax;
    }

    public boolean isAddable(Color color, int num){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) + num <= sizeMax;
    }
    //if called in an already empty students, does nothing
    public void remove(Color color){
            if(color == null){
                throw new NullPointerException("Colore null");
            }

            Integer temp;
            temp = students.get(color);

            if(temp>0){
                students.put(color, temp-1);
            }
            else{
                students.put(color, 0);
            }
    }
    public void remove(Color color, int num){
            if(color == null){
                throw new NullPointerException("Colore null");
            }

            if (num<0){
                throw new IllegalArgumentException("Il numero deve essere positivo");
            }

            Integer temp;
            temp = students.get(color);
        students.put(color, Math.max(temp - num, 0));

    }
    public boolean isRemovable(Color color){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) - 1 >= 0;
    }

    public boolean isRemovable(Color color, int num){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) - num >= 0;
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
        if(color == null){
            throw new NullPointerException("Colore null");
        }
        return students.get(color);
    }
}

