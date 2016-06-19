package com.axiomsl.hotel.model;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Reservation Entity
 */
@Entity
@Table(name = "reservations")
@NamedQueries({
        @NamedQuery(name = "Reservation.findOneByRoomAndDates",
                query = "select distinct s from Reservation s where (s.cancelled = 0) " +
                        "and (s.id <> :id) and (s.room.id = :rid) " +
                        "and ((:fr < s.to) and (:to > s.from))")
})
public class Reservation {
    private Long id;
    private int version;
    private Guest guest;
    private Room room;
    private DateTime from;
    private DateTime to;
    private boolean cancelled;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    @Column(name = "reservation_version")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @ManyToOne
    @JoinColumn(name = "guest_id")
    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    @ManyToOne
    @JoinColumn(name = "room_id")
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @NotNull(message = "{not_empty_date}")
    @Column(name = "reservation_from")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @DateTimeFormat(pattern = "dd-MM-yy")
    public DateTime getFrom() {
        return from;
    }

    public void setFrom(DateTime from) {
        this.from = from;
    }

    @NotNull(message = "{not_empty_date}")
    @Column(name = "reservation_to")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @DateTimeFormat(pattern = "dd-MM-yy")
    public DateTime getTo() {
        return to;
    }

    public void setTo(DateTime to) {
        this.to = to;
    }

    @Column(name = "reservation_cancelled")
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Transient
    public String getFromString() {
        String fromString = "";
        if (from != null)
            fromString = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd").print(from);
        return fromString;
    }

    @Transient
    public String getToString() {
        String fromString = "";
        if (to != null)
            fromString = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd").print(to);
        return fromString;
    }


    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", version=" + version +
                ", guest=" + guest +
                ", room=" + room +
                ", from=" + from +
                ", to=" + to +
                ", cancelled=" + cancelled +
                '}';
    }
}
