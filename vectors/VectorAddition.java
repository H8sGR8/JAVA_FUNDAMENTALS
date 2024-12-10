package vectors;

public class VectorAddition {

    public static  Vector addVectors(Vector v1, Vector v2) throws DifferentVectorsLenghtsExeption{
        String resultString = "";
        if(v1.getVectorLenght() != v2.getVectorLenght()) throw new DifferentVectorsLenghtsExeption(v1, v2);
        for(int i = 0; i < v1.getVectorLenght(); i++){
            if(i > 0) resultString += ",";
            resultString += (v1.vCords.get(i) + v2.vCords.get(i)); 
        }
        return new Vector(resultString);
    }


}
