package com.axiomsl.hotel.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Guest.class)
public abstract class Guest_ {

	public static volatile SingularAttribute<Guest, String> confirm;
	public static volatile SingularAttribute<Guest, String> firstName;
	public static volatile SingularAttribute<Guest, String> lastName;
	public static volatile SingularAttribute<Guest, String> password;
	public static volatile SingularAttribute<Guest, GuestRole> role;
	public static volatile SetAttribute<Guest, Reservation> reservations;
	public static volatile SingularAttribute<Guest, Long> id;
	public static volatile SingularAttribute<Guest, String> login;
	public static volatile SingularAttribute<Guest, Integer> version;

}

