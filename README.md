# Hotel
Hotel Reservations Spring MVC Web Application

# Requirements:
- Hotel has Location
- Location has Address (eg. City, Street, Zip-code)
- Hotel has many rooms
- There are 3 types of Rooms: StandardRoom, SpecialRoom, ExclusiveRoom
- Room can be booked by Guests
- Room can be booked for period of time: from - to
- Room can have windows exposed to one of the following directions: NORTH, SOUTH, WEST, EAST

# To do:
1. Create system described above using best practices and principles of Object-Oriented programming
2. It should be possible to:
    - Access system using web browser
    - Create room reservation (persist it in database)
    - Check if room is free for the given date range (search in database)
    - Cancel reservation (mark reservation as canceled)
    - Write appropriate JUnit tests
    - It is allowed to use external libraries for example from maven repository
    - Publish code on github
    - It should be possible to build and run project using single maven command

# Installing and running notes:
1. Set up MySQL database connection options in src\main\resources\META-INF\config\jdbc.properties
2. Create and initialize database by running script from files\hotel.sql
3. Database schema with fields and connections is in files\hotel.pdf
4. There are one defined default hotel with id=1 in database and two defined guest profiles:
    - login: admin password: admin with ROLE_ADMIN rights,
    - login: user password: uer with ROLE_GUEST rights.
5. Non-authenticated guest can view rooms information and register into database with ROLE_GUEST rights.
6. Login, password, first name and last name are required on registration.
7. Registered and authenticated guest can view rooms make its reservations and mark them cancelled.
8. In addition guest with ROLE_ADMIN rights can edit Hotel information, add, edit and delete rooms.
9. For the hotel only name are required, location fields are optional.
10. For the room number, type and windows direction are required.
11. For the reservation start and end date are required and end date should be after start date.
12. During reservation system check if the reservation is possible among of all non-cancelles reservations.