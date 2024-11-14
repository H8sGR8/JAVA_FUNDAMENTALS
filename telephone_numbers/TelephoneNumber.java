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

    public String toString(){
        return this.countryCode + " " + this.localNumber;
    }

    @Override
    public int compareTo(TelephoneNumber other) {
        char number1[] = (this.toString()).toCharArray();
        char number2[] = (other.toString()).toCharArray();
        if(number1.length - number2.length != 0) return number1.length - number2.length;
        for(int i = 0; i < number1.length; i++) if((int)number1[i] - (int)number2[i] != 0) return (int)number1[i] - (int)number2[i];
        return 0;
    }
}
