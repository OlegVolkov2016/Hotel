# Hotel
Hotel Reservations Spring MVC Web Application

1. Set up MySQL database connection in src\main\resources\META-INF\config\jdbc.properties
2. Create anf initialize database by running script files\hotel.sql
3. Database schema is in files\hotel.pdf
4. There are one defined default hotel with id=1 and two defined guest profiles:
	- login: admin password: admin with ROLE_ADMIN rights
	- login: user password: uer with ROLE_GUEST rights
5. Guest user can view rooms information and register into database with ROLE_GUEST rights
6. Refistered user can view rooms make its reservations and mark them cancelled
7. In addition user with ROLE_ADMIN rights can edit Hotel information, add, edit and delete rooms
