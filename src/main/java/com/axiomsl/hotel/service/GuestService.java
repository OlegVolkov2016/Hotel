package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Guest;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Guest Service interface
 */
public interface GuestService {
    /* Basic CRUD operations */
    Guest findByLogin(String login);
    Guest save(Guest guest);
    void delete(Long id);
}
