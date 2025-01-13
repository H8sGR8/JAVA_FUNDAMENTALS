package vectors;

import java.util.ArrayList;
import java.util.Scanner;

public class VectorApp {

    Scanner input = new Scanner(System.in);
    ArrayList<Vector> vectors = new ArrayList<>();
    Vector v3;
    boolean run = true;

    public void getInputVectors(){
        vectors.clear();
        System.out.println("Input vectors");
        for(String v : input.nextLine().split(" ")){
            vectors.add(new Vector(v));
        }
    }

    public void makeAddition(){
        try{
            v3 = VectorAddition.addVectors(vectors);
            run = false;
        }catch(DifferentVectorsLenghtsExeption e){
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        VectorApp vApp = new VectorApp();
        while(vApp.run){
            vApp.getInputVectors();
            vApp.makeAddition();
        }
        System.out.println("Sum of vectors is eqaul to " + vApp.v3.getVectorCords());
    }
}
