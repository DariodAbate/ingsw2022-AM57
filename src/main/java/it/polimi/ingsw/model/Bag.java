package it.polimi.ingsw.model;

/**
 * This class represents the bag of the game: the two constructors initialize the bag with the specified number of tokens
 * @author Lorenzo Corrado
 */
public class Bag {
    private StudentsHandler studentsHandlerToken;
    //2 constructor: the first is very specific, the second is a generic one
    public Bag(int red, int blue, int green, int pink, int yellow){
        //token is the set of students inside the bag
        studentsHandlerToken = new StudentsHandler();
        studentsHandlerToken.add(Color.RED, red);
        studentsHandlerToken.add(Color.YELLOW,yellow);
        studentsHandlerToken.add(Color.GREEN, green);
        studentsHandlerToken.add(Color.BLUE, blue);
        studentsHandlerToken.add(Color.PINK, pink);
    }

    public Bag(int size){
        //token is the set of students inside the bag
        studentsHandlerToken = new StudentsHandler();
        //iteration of all enum values
        for (Color color:Color.values()
             ) {
            studentsHandlerToken.add(color, size);
        }
    }

    /**
     * This method simulates a real draw calculating the probability of each color to be extracted, than he removes the
     * extracted token from the bag
     * @return The color of the extracted token, if the bag is empty return null
     */
    public Color draw(){
        if(isEmpty())
        {
            return null; //Need to add endgame() from Game class
        }
        //every time I need to draw, I calculate the probability of it;
        double prob;
       // try{
        double pink = (double) studentsHandlerToken.numStudents(Color.PINK)/ (double) studentsHandlerToken.numStudents();
        double yellow = pink + (double) studentsHandlerToken.numStudents(Color.YELLOW)/ (double) studentsHandlerToken.numStudents();
        double red = yellow + (double) studentsHandlerToken.numStudents(Color.RED)/ (double) studentsHandlerToken.numStudents();
        double blue = red + (double) studentsHandlerToken.numStudents(Color.BLUE)/ (double) studentsHandlerToken.numStudents();
        double green = blue + (double) studentsHandlerToken.numStudents(Color.GREEN)/ (double) studentsHandlerToken.numStudents();//removable

        //math.random() generates from 0<=x<1, I adjusted that to 0<x<=1
        prob=Math.random();
        prob = Math.abs(prob-1);


        if(prob<=pink){
            studentsHandlerToken.remove(Color.PINK);
            return Color.PINK;
        }
        else if(pink<prob && prob<=yellow){
            studentsHandlerToken.remove(Color.YELLOW);
            return Color.YELLOW;
        }
        else if(yellow<prob && prob<=red){
            studentsHandlerToken.remove(Color.RED);
            return Color.RED;
        }
        else if(red<prob && prob<=blue){
            studentsHandlerToken.remove(Color.BLUE);
            return Color.BLUE;
        }
        else{
            studentsHandlerToken.remove(Color.GREEN);
            return Color.GREEN;
        }
    }

    /**
     * @return The current size of the bag
     */
    public int size(){
        return studentsHandlerToken.numStudents();
    }

    /**
     *
     * @return If the bag is empty or not
     */
    public boolean isEmpty(){
        return studentsHandlerToken.numStudents() == 0;
    }
}