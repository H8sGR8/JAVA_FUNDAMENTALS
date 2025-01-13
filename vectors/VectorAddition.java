package vectors;

import java.util.ArrayList;

public class VectorAddition {

    public static  Vector addVectors(ArrayList<Vector> vectors) throws DifferentVectorsLenghtsExeption{
        double cord;
        String resultString = "";
        for(int i = 0; i < vectors.size(); i++){
            if(vectors.get(0).getVectorLenght() != vectors.get(i).getVectorLenght()){
                ArrayList<Vector> incompatibleVectors = new ArrayList<>();
                incompatibleVectors.add(vectors.get(0));
                incompatibleVectors.add(vectors.get(i));
                throw new DifferentVectorsLenghtsExeption("Vectors have diffrent lenghts", incompatibleVectors);
            }
        }
        for(int i = 0; i < vectors.get(0).getVectorLenght(); i++){
            cord = 0;
            if(i > 0) resultString += ",";
            for(Vector v : vectors){
                cord += v.getVectorCords().get(i);
            }
            resultString += cord;
             
        }
        return new Vector(resultString);
    }


}
