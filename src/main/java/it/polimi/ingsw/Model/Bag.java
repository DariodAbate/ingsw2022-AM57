package it.polimi.ingsw.Model;

/**
 * This class represents the bag of the game: the two constructors initialize the bag with the specified number of tokens
 * @author Lorenzo Corrado
 */
public class Bag {
    private StudentsHandler token;
    //2 constructor: the first is very specific, the second is a generic one
    public Bag(int red, int blue, int green, int pink, int yellow){
        //token is the set of students inside the bag
        token = new StudentsHandler();
        token.add(Color.RED, red);
        token.add(Color.YELLOW,yellow);
        token.add(Color.GREEN, green);
        token.add(Color.BLUE, blue);
        token.add(Color.PINK, pink);
    }

    public Bag(int size){
        //token is the set of students inside the bag
        token= new StudentsHandler();
        //iteration of all enum values
        for (Color color:Color.values()
             ) {
            token.add(color, size);
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
        double pink = (double)token.numStudents(Color.PINK)/ (double)token.numStudents();
        double yellow = pink + (double)token.numStudents(Color.YELLOW)/ (double)token.numStudents();
        double red = yellow + (double)token.numStudents(Color.RED)/ (double)token.numStudents();
        double blue = red + (double)token.numStudents(Color.BLUE)/ (double)token.numStudents();
        double green = blue + (double)token.numStudents(Color.GREEN)/ (double)token.numStudents();//removable

        //math.random() generates from 0<=x<1, I adjusted that to 0<x<=1
        prob=Math.random();
        prob = Math.abs(prob-1);


        if(prob<=pink){
            token.remove(Color.PINK);
            return Color.PINK;
        }
        else if(pink<prob && prob<=yellow){
            token.remove(Color.YELLOW);
            return Color.YELLOW;
        }
        else if(yellow<prob && prob<=red){
            token.remove(Color.RED);
            return Color.RED;
        }
        else if(red<prob && prob<=blue){
            token.remove(Color.BLUE);
            return Color.BLUE;
        }
        else{
            token.remove(Color.GREEN);
            return Color.GREEN;
        }
    }

    /**
     * @return The current size of the bag
     */
    public int size(){
        return token.numStudents();
    }

    /**
     *
     * @return If the bag is empty or not
     */
    public boolean isEmpty(){
        return token.numStudents() == 0;
    }
}