package com.axiomsl.hotel.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.DateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Reservation.class)
public abstract class Reservation_ {

	public static volatile SingularAttribute<Reservation, Boolean> cancelled;
	public static volatile SingularAttribute<Reservation, Guest> guest;
	public static volatile SingularAttribute<Reservation, DateTime> from;
	public static volatile SingularAttribute<Reservation, Long> id;
	public static volatile SingularAttribute<Reservation, DateTime> to;
	public static volatile SingularAttribute<Reservation, Integer> version;
	public static volatile SingularAttribute<Reservation, Room> room;

}

