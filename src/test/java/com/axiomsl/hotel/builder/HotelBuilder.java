package com.axiomsl.hotel.builder;

import com.axiomsl.hotel.model.Hotel;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Hotel Builder Class
 */
public class HotelBuilder {

    private Hotel model;

    public HotelBuilder() {
        model = new Hotel();
    }

    public HotelBuilder id(Long id) {
        ReflectionTestUtils.setField(model, "id", id);
        return this;
    }

    public HotelBuilder name(String name) {
        model.setName(name);
        return this;
    }

    public HotelBuilder address(String address) {
        model.setAddress(address);
        return this;
    }

    public HotelBuilder city(String city) {
        model.setCity(city);
        return this;
    }

    public HotelBuilder zip(int zip) {
        model.setZip(zip);
        return this;
    }

    public HotelBuilder state(String state) {
        model.setState(state);
        return this;
    }

    public Hotel build() {
        return model;
    }
}
