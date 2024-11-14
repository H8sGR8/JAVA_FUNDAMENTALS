package telephone_numbers;

import java.util.Iterator;
import java.util.TreeMap;

public class TelephoneTreeMap {
    
    TreeMap<TelephoneNumber, TelephoneEntry> telephoneMap;

    public TelephoneTreeMap(){
        telephoneMap = new TreeMap<>();
    }

    public void addPerson(String name, String surname, String adress, String number){
        Person person = new Person(name, surname, adress, number);
        telephoneMap.put(person.getNumber(), person);
    }

    public void addCompany(String name, String adress, String number){
        Company company = new Company(name, adress, number);
        telephoneMap.put(company.getNumber(), company);
    }

    public void printMap(){
        Iterator<TelephoneNumber> keys = telephoneMap.keySet().iterator();
        TelephoneNumber key;
        while(keys.hasNext()){
            key = keys.next();
            System.out.println("Under " + key.toString() + " phone number can be found " + telephoneMap.get(key).description());
        }
    }

    public static void main(String[] args) {
        TelephoneTreeMap testMap = new TelephoneTreeMap();
        testMap.addPerson("Mateusz", "Kobalczyk", "Polska, Lodz, Kwietniowa 28", "+48 692-151-004");
        testMap.addCompany("Google", "USA, California, Silicon Vally", "+1 123456789");
        testMap.printMap();
    }
}
