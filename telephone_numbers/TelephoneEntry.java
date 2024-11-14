package telephone_numbers;

public abstract class TelephoneEntry {
    
    protected String name;
    protected Adress adress;
    protected TelephoneNumber number;

    public TelephoneNumber getNumber(){
        return number;
    }

    public abstract String description();
}
