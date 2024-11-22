package telephone_numbers;

public class TelephoneNumber implements Comparable<TelephoneNumber> {
    String countryCode = "";
    String localNumber = "";
    final int LOCAL_NUMBER_LEN = 9;

    public TelephoneNumber(String number) {
        String numberWithNoDivisors = "";       
        String dividedNumber[] = number.split("[-\\s]");
        for (String numberPart : dividedNumber) numberWithNoDivisors += numberPart;
        char numberArray[] = numberWithNoDivisors.toCharArray();
        if(numberArray[0] != '+') this.countryCode += "+";
        for(int i = 0; i < numberWithNoDivisors.length() - LOCAL_NUMBER_LEN; i++) this.countryCode += numberArray[i];
        for(int i = LOCAL_NUMBER_LEN; i > 0 ; i--) this.localNumber += numberArray[numberArray.length - i];
    }

    @Override
    public String toString(){
        return this.countryCode + " " + this.localNumber;
    }

    @Override
    public int compareTo(TelephoneNumber other) {
        if (this.toString().length() != other.toString().length()) return this.toString().length() - other.toString().length();
        return this.toString().compareTo(other.toString());
    }
}
