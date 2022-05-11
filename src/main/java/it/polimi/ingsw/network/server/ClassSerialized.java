package it.polimi.ingsw.network.server;

import java.io.Serializable;

public class ClassSerialized implements Serializable {
    private String field1;
    private int field2;

    public ClassSerialized(String field1, int field2){
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public String toString(){
        return "Field1:" + field1 + "\nField2:" + field2;
    }
}
