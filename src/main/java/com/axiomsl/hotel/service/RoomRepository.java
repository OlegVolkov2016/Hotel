package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Room repository extension interface
 */
interface RoomRepository extends CrudRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    /* Custom queries with join */
    Room findByNumber(String number);
}
