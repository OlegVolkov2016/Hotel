package com.axiomsl.hotel.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Hotel.class)
public abstract class Hotel_ {

	public static volatile SingularAttribute<Hotel, Integer> zip;
	public static volatile SetAttribute<Hotel, Room> rooms;
	public static volatile SingularAttribute<Hotel, String> address;
	public static volatile SingularAttribute<Hotel, String> city;
	public static volatile SingularAttribute<Hotel, String> name;
	public static volatile SingularAttribute<Hotel, Long> id;
	public static volatile SingularAttribute<Hotel, String> state;
	public static volatile SingularAttribute<Hotel, Integer> version;

}

