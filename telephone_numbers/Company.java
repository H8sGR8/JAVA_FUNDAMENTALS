package telephone_numbers;

public class Company extends TelephoneEntry{

    public Company(String name, String adress, String number){
        this.name = name;
        this.adress = new Adress(adress);
        this.number = new TelephoneNumber(number);
    }

    @Override
    public String description() {
        return "Company (Name: " + name + ", Adress: " + adress.getAdress() + ")";
    }

}
