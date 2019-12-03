package com.github.corourke;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;

import java.util.Locale;


public class Store {
    static Integer store_number = 12345;
    static Faker faker = new Faker(new Locale("en-US"));;

    Integer store_id;
    String city;
    String state;
    String zipcode;
    Double longitude;
    Double latitude;
    String timezone;

    /* TODO: The problem is that the address elements are not correlated */

    public Store() {
        Address store_address = faker.address();

        this.store_id = store_id = store_number++;
        this.city = store_address.city();
        this.state = store_address.stateAbbr();
        this.zipcode = store_address.zipCode();
        this.longitude = Double.valueOf(store_address.longitude());
        this.latitude = Double.valueOf(store_address.latitude());
        this.timezone = store_address.timeZone();
    }

    public Integer getStore_id() {
        return store_id;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getTimezone() {
        return timezone;
    }
}
