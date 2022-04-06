package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class utilizes a Hashmap to represent the tokens, and a MaxSize that is the maximum
 * number of students for each color
 * @author Lorenzo Corrado
 */
public class StudentsHandler {
    private static final int MAXSIZE = 26;
    private Map<Color, Integer> students;
    //maximum size that you can have for every single color, if not specified it is 26
    int sizeMax;

    /**
     * This constructor utilizes the default maximum size for EACH color
     */
    public StudentsHandler() {
        students = new HashMap<>();
        for (Color color:
             Color.values()) {
            students.put(color, 0);
        }
        this.sizeMax = MAXSIZE;
    }

    /**
     * This constructor utilizes a custom size
     * @throws IllegalArgumentException if put an illegal size
     * @param size
     */

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


    /**
     * This method adds one single student for the specified color
     * @param color The color of the student
     */
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

    /**
     * This method add a custom number of students for the specified color
     * If you add more students than the maximum size it will set the number of students to sizeMax
     * @param color The color of the students
     * @param num The number of students to add
     */
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

    /**
     * This method checks if an add would increment the number of students more than the maximum size
     * @param color The color of the students to check
     * @return
     */
    public boolean isAddable(Color color){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) + 1 <= sizeMax;
    }

    /**
     * This method checks if an add would increment the number of students more than the maximum size
     * @param color The color of the students to check
     * @param num The number of the increment
     * @return
     */
    public boolean isAddable(Color color, int num){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) + num <= sizeMax;
    }

    /**
     * This method remove one student token of the specified color
     * @param color The specified color
     */
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

    /**
     * This method remove a custom number of students of the specified color
     * If you remove more students than the actual that are present it will set the number of students to 0
     * @param color The specified color
     * @param num The number of students to remove
     */
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

    /**
     * This method checks if is possible to remove one single student(i/e if is empty)
     * @param color The specified color
     * @return
     */
    public boolean isRemovable(Color color){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) - 1 >= 0;
    }

    /**
     * This method check if is possible to remove a custom number of students without losing tokens
     * @param color The specified color
     * @param num The number of students to remove
     * @return
     */
    public boolean isRemovable(Color color, int num){
        if(color == null){
            throw new NullPointerException("Colore null");
        }

        return students.get(color) - num >= 0;
    }

    /**
     * @return the TOTAL number of students
     */
    public int numStudents(){
        int sum = 0;
        for (Color color :
                Color.values()) {
            sum += students.get(color);
        }
        return sum;
    }

    /**
     * @param color The specified color
     * @return The number of students of that specified color
     */
    public int numStudents(Color color) {
        if(color == null){
            throw new NullPointerException("Colore null");
        }
        return students.get(color);
    }
}

