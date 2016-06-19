package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Guest;
import com.axiomsl.hotel.model.Reservation;
import com.axiomsl.hotel.model.Room;
import org.joda.time.DateTime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Reservation repository extension interface
 */
interface ReservationRepository extends CrudRepository<Reservation, Long> {
    /* Custom queries with join */
    List<Reservation> findOneByRoomAndDates(@Param("id") Long id, @Param("rid") Long roomId, @Param("fr") DateTime from, @Param("to") DateTime to);
    List<Reservation> findAllByGuestAndRoom(Guest guest, Room room);
}
