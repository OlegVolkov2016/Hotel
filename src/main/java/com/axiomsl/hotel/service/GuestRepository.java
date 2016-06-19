package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Guest;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Guest repository extension interface
 */
interface GuestRepository extends CrudRepository<Guest, Long> {
    /* Custom queries with join */
    Guest findByLogin(String login);
}
