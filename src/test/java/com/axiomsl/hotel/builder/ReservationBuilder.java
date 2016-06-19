package com.axiomsl.hotel.builder;

import com.axiomsl.hotel.model.*;
import org.joda.time.DateTime;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Reservation Builder Class
 */
public class ReservationBuilder {

    private Reservation model;

    public ReservationBuilder() {
        model = new Reservation();
    }

    public ReservationBuilder id(Long id) {
        ReflectionTestUtils.setField(model, "id", id);
        return this;
    }

    public ReservationBuilder guest(Guest guest) {
        model.setGuest(guest);
        return this;
    }

    public ReservationBuilder room(Room room) {
        model.setRoom(room);
        return this;
    }

    public ReservationBuilder from(DateTime from) {
        model.setFrom(from);
        return this;
    }

    public ReservationBuilder to(DateTime to) {
        model.setTo(to);
        return this;
    }

    public ReservationBuilder cancelled(Boolean cancelled) {
        model.setCancelled(cancelled);
        return this;
    }

    public Reservation build() {
        return model;
    }
}
