package vectors;

import java.util.Scanner;

public class VectorApp {

    Scanner input = new Scanner(System.in);
    Vector v1;
    Vector v2;
    Vector v3;
    boolean run = true;

    public void getInputVectors(){
        System.out.println("Input first vector");
        v1 = new Vector(input.nextLine());
        System.out.println("Input second vector");
        v2 = new Vector(input.nextLine());
    }

    public void makeAddition(){
        try{
            v3 = VectorAddition.addVectors(v1, v2);
            run = false;
        }catch(DifferentVectorsLenghtsExeption e){
            e.getMessage(e.exceptionV1.vCords + " the vector lenght is " + e.lowerOrBigger() + " than " + e.exceptionV2.getVectorCords() + " vector lenght");
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
