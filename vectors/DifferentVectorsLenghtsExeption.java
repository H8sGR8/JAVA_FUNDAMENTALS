package vectors;

import java.util.ArrayList;

public class DifferentVectorsLenghtsExeption extends Exception {

    private final ArrayList<Vector> vectors;

    public DifferentVectorsLenghtsExeption(String message, ArrayList<Vector> vectors){
        super(message);
        this.vectors = vectors;
    }

    public ArrayList<Vector> getVectors(){
        return vectors;
    }

}
