package telephone_numbers;

class Country{

    String countryName;
    
    public Country(String name){
        this.countryName = name;
    }

    public String getCountry(){
        return this.countryName;
    }
}

class City{

    String cityName;

    public City(String name){
        this.cityName = name;
    }

    public String getCity(){
        return this.cityName;
    }
}

class StreetAdress{

    String streetName = "";
    String houseNumber = "";

    public StreetAdress(String name){
        char nameArray[] = name.toCharArray();
        for(int i = 0; i < name.indexOf(" "); i++) this.streetName += nameArray[i];
        for(int i = name.indexOf(" ") + 1; i < name.length(); i++) this.houseNumber += nameArray[i];
    }

    public String getStreetAdress(){
        return this.streetName + " " + this.houseNumber;
    }
}

public class Adress {

    Country country;
    City city;
    StreetAdress street;

    public Adress(String adress){
        String adressComponents[] = adress.split(", ");
        country = new Country(adressComponents[0]);
        city = new City(adressComponents[1]);
        street = new StreetAdress(adressComponents[2]);
    }

    public String getAdress(){
        return country.getCountry() + ", " + city.getCity() + ", " + street.getStreetAdress();
    }
}
