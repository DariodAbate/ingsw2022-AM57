package it.polimi.ingsw;

public class CloudTile {
    private StudentsHandler cloudStud;
    private final int maxSize;


    private static final int SIZE2PLAYER = 3;
    private static final int SIZE3PLAYER = 4;


    public CloudTile(int numPlayer){
        if (numPlayer == 2) {
            cloudStud = new StudentsHandler(SIZE2PLAYER);
            maxSize = SIZE2PLAYER;
        }
        else if (numPlayer == 3){
            cloudStud = new StudentsHandler(SIZE3PLAYER);
            maxSize = SIZE3PLAYER;
        }
        else
            throw new IllegalArgumentException("Illegal number of players");

    }


    public int numStudOn(){
        return cloudStud.numStudents();
    }

    public int numStudOn(Color color){
        if(color == null)
            throw new NullPointerException();
        return cloudStud.numStudents(color);
    }


    public boolean isEmpty(){ return cloudStud.numStudents() == 0; }

    //this method can be used by "Game" class
    public boolean isFillable(){ return cloudStud.numStudents() < maxSize; }

    //drawing single student from bag
    //isAddable is a control over a single color, I have to check the total number of student on a tile
    public void fill(Color color){
        if(color == null)
            throw new NullPointerException();
        else if (isFillable() && cloudStud.isAddable(color))
            cloudStud.add(color);
    }

    //we can obtain students from a tile iff the tile is full, otherwise unchanged
    public StudentsHandler getTile(){
        if(isEmpty() || isFillable())
            throw new IllegalStateException();

        StudentsHandler temp = new StudentsHandler(maxSize);
        for(Color color: Color.values()){
            while(cloudStud.numStudents(color) > 0){
                temp.add(color);
                cloudStud.remove(color);
            }
        }
        return temp;

    }
}
