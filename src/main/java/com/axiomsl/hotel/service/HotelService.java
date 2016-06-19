package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Hotel;

import java.util.List;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Hotel Service interface
 */
public interface HotelService {
    /* Basic CRUD operations */
    List<Hotel> findAllByCriteria(String name, String author, String genre);
    Hotel findOne(Long id);
    Hotel findByName(String name);
    Hotel save(Hotel hotel);
    void delete(Long id);
}
