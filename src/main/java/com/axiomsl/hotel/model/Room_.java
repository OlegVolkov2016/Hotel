package com.axiomsl.hotel.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Room.class)
public abstract class Room_ {

	public static volatile SingularAttribute<Room, String> number;
	public static volatile SetAttribute<Room, Reservation> reservations;
	public static volatile SingularAttribute<Room, Hotel> hotel;
	public static volatile SingularAttribute<Room, Long> id;
	public static volatile SingularAttribute<Room, RoomType> type;
	public static volatile SingularAttribute<Room, Integer> version;
	public static volatile SingularAttribute<Room, RoomDirection> direction;

}

