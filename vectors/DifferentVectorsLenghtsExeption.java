package vectors;

public class DifferentVectorsLenghtsExeption extends Exception {

    Vector exceptionV1;
    Vector exceptionV2;

    public DifferentVectorsLenghtsExeption(Vector v1, Vector v2) {
    exceptionV1 = v1;
    exceptionV2 = v2;
    }

    public String lowerOrBigger(){
        if(exceptionV1.getVectorLenght() > exceptionV2.getVectorLenght()) return "lower";
        return "bigger";
    }

    

    public void getMessage(String message){
        System.out.println(message);
    }
}
