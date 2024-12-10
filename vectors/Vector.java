package vectors;

import java.util.ArrayList;

public class Vector {
    ArrayList<Double> vCords = new ArrayList<>();

    public Vector(String inputVector){

        String inputVCords[] = inputVector.split(",");
        double vDoubleCord;
        for(String vStringCord : inputVCords){
            if(Double.isNaN(vDoubleCord = convertToDouble(vStringCord))) continue;
            vCords.add(vDoubleCord);
        }
    }

    @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
    private Double convertToDouble(String vLenght){
        try{
            return Double.parseDouble(vLenght);
        }catch(NumberFormatException e){
            return Double.NaN;
        }
    }

    public ArrayList<Double> getVectorCords(){
        return vCords;
    }

    public int getVectorLenght(){
        return vCords.size();
    }
}
