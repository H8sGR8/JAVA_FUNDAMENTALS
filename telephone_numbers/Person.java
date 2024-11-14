package telephone_numbers;

public class Person extends TelephoneEntry{

    String surname;

    public Person(String name, String surname, String adress, String number){
        this.name = name;
        this.surname = surname;
        this.adress = new Adress(adress);
        this.number = new TelephoneNumber(number);
    }

    @Override
    public String description() {
        return "Person (Name: " + name + ", Surname: " + surname + ", Adress: " + adress.getAdress() + ")";
    }

}
