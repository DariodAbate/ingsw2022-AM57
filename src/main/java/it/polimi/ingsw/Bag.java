package it.polimi.ingsw;


public class Bag {
    private Students token;
    //2 constructor: the first is very specific the second is a generic one
    public Bag(int red, int blue, int green, int pink, int yellow){
        token = new Students();
        token.add(Color.RED, red);
        token.add(Color.YELLOW,yellow);
        token.add(Color.GREEN, green);
        token.add(Color.BLUE,blue);
        token.add(Color.PINK, pink);
    }

    public Bag(int size){
        //iteration of all enum values
        token= new Students();
        for (Color color:Color.values()
             ) {
            token.add(color, size);
        }
    }

    public Color draw(){
        double prob;
        double pink = (double)token.numStudents(Color.PINK)/ (double)token.numStudents();
        double yellow = pink + (double)token.numStudents(Color.YELLOW)/ (double)token.numStudents();
        double red = yellow + (double)token.numStudents(Color.RED)/ (double)token.numStudents();
        double blue = red + (double)token.numStudents(Color.BLUE)/ (double)token.numStudents();
        double green = blue + (double)token.numStudents(Color.GREEN)/ (double)token.numStudents();

        prob=Math.random();
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

}