package com.axiomsl.hotel;

import com.axiomsl.hotel.builder.GuestBuilder;
import com.axiomsl.hotel.builder.HotelBuilder;
import com.axiomsl.hotel.builder.ReservationBuilder;
import com.axiomsl.hotel.builder.RoomBuilder;
import com.axiomsl.hotel.configuration.HotelConfiguration;
import com.axiomsl.hotel.controller.HotelController;
import com.axiomsl.hotel.model.*;
import com.axiomsl.hotel.service.GuestService;
import com.axiomsl.hotel.service.HotelService;
import com.axiomsl.hotel.service.ReservationService;
import com.axiomsl.hotel.service.RoomService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Test Configuraion Class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {HotelConfiguration.class})
//@ActiveProfiles("testing")
public class HotelTest {
    private MockMvc mockMvc;
    private Hotel hotel;
    private Guest guest;
    private Room room;
    private Reservation reservation;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    HotelService hotelService;

    @Autowired
    GuestService guestService;

    @Autowired
    RoomService roomService;

    @Autowired
    ReservationService reservationService;

    @InjectMocks
    HotelController hotelController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset();
        guest = new GuestBuilder()
                .login("test")
                .password("test")
                .confirm("test")
                .role(GuestRole.ROLE_ADMIN)
                .firstName("Test")
                .lastName("Test")
                .build();
        room = new RoomBuilder()
                .number("9999")
                .type(RoomType.StandardRoom)
                .direction(RoomDirection.NORTH)
                .hotel(hotel)
                .build();
        reservation = new ReservationBuilder()
                .from(dateTimeFormatter.parseDateTime("01-06-2016"))
                .to(dateTimeFormatter.parseDateTime("05-06-2016"))
                .cancelled(false)
                .build();
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testListRooms() throws Exception {
        hotel = hotelService.findOne(1L);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("hotel", hasProperty("id", is(hotel.getId()))))
                .andExpect(model().attribute("hotel", hasProperty("name", is(hotel.getName()))))
                .andExpect(model().attribute("hotel", hasProperty("address", is(hotel.getAddress()))))
                .andExpect(model().attribute("hotel", hasProperty("city", is(hotel.getCity()))))
                .andExpect(model().attribute("hotel", hasProperty("zip", is(hotel.getZip()))))
                .andExpect(model().attribute("hotel", hasProperty("state", is(hotel.getState()))));
    }

    @Test
    public void testLocationGet() throws Exception {
        hotel = hotelService.findOne(1L);
        mockMvc.perform(get("/location"))
                .andExpect(status().isOk())
                .andExpect(view().name("hotel"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("hotel", hasProperty("id", is(hotel.getId()))))
                .andExpect(model().attribute("hotel", hasProperty("name", is(hotel.getName()))))
                .andExpect(model().attribute("hotel", hasProperty("address", is(hotel.getAddress()))))
                .andExpect(model().attribute("hotel", hasProperty("city", is(hotel.getCity()))))
                .andExpect(model().attribute("hotel", hasProperty("zip", is(hotel.getZip()))))
                .andExpect(model().attribute("hotel", hasProperty("state", is(hotel.getState()))));
    }

    @Test
    public void testLocationPost() throws Exception {
        hotel = hotelService.findOne(1L);
        if (hotelService.findByName("test") != null) {
            hotelService.delete(hotelService.findByName("test").getId());
        }
        mockMvc.perform(post("/location")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("hotel", new HotelBuilder().build())
                .param("name", "test")
                .param("address", hotel.getAddress())
                .param("city", hotel.getCity())
                .param("zip", hotel.getZip() + "")
                .param("state", hotel.getState()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"))
                .andExpect(forwardedUrl(null));
        hotelService.delete(hotelService.findByName("test").getId());
    }

    @Test
    public void testLocationPostNameError() throws Exception {
        hotel = hotelService.findOne(1L);
        mockMvc.perform(post("/location")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("hotel", new HotelBuilder().build())
                .param("name", "")
                .param("address", hotel.getAddress())
                .param("city", hotel.getCity())
                .param("zip", hotel.getZip() + "")
                .param("state", hotel.getState()))
                .andExpect(status().isOk())
                .andExpect(view().name("hotel"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("hotel", "name"));
    }

    @Test
    public void testRegisterGet() throws Exception {
        mockMvc.perform(get("/sec_registry"))
                .andExpect(status().isOk())
                .andExpect(view().name("sec_registry"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("guest", hasProperty("id", nullValue())))
                .andExpect(model().attribute("guest", hasProperty("login", nullValue())))
                .andExpect(model().attribute("guest", hasProperty("password", nullValue())))
                .andExpect(model().attribute("guest", hasProperty("confirm", nullValue())))
                .andExpect(model().attribute("guest", hasProperty("role", nullValue())))
                .andExpect(model().attribute("guest", hasProperty("firstName", nullValue())))
                .andExpect(model().attribute("guest", hasProperty("lastName", nullValue())));
    }

    @Test
    public void testRegisterPost() throws Exception {
        if (guestService.findByLogin(guest.getLogin()) != null) {
            guestService.delete(guestService.findByLogin(guest.getLogin()).getId());
        }
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", guest.getPassword())
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", guest.getLastName()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"))
                .andExpect(forwardedUrl(null));
        SecurityContextHolder.getContext().setAuthentication(null);
        guestService.delete(guestService.findByLogin(guest.getLogin()).getId());
    }

    @Test
    public void testRegisterPostLoginError() throws Exception {
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", "")
                .param("password", guest.getPassword())
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", guest.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("sec_registry"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("guest", "login"));
    }

    @Test
    public void testRegisterPostPasswordError() throws Exception {
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", "")
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", guest.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("sec_registry"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("guest", "password"));
    }

    @Test
    public void testRegisterPostConfirmError() throws Exception {
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", guest.getPassword())
                .param("confirm", "")
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", guest.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("sec_registry"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("guest", "confirm"));
    }

    @Test
    public void testRegisterPostFirstNameError() throws Exception {
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", guest.getPassword())
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", "")
                .param("lastName", guest.getLastName()))
                .andExpect(status().isOk())
                .andExpect(view().name("sec_registry"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("guest", "firstName"));
    }

    @Test
    public void testRegisterPostLastNameError() throws Exception {
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", guest.getPassword())
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("sec_registry"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("guest", "lastName"));
    }

    @Test
    public void testRegisterInfo() throws Exception {
        if (guestService.findByLogin(guest.getLogin()) != null) {
            guestService.delete(guestService.findByLogin(guest.getLogin()).getId());
        }
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", guest.getPassword())
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", guest.getLastName()));
        guest = guestService.findByLogin(guest.getLogin());
        mockMvc.perform(get("/"));
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(view().name("info"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("guest", hasProperty("id", is(guest.getId()))))
                .andExpect(model().attribute("guest", hasProperty("login", is(guest.getLogin()))))
                .andExpect(model().attribute("guest", hasProperty("password", is(guest.getPassword()))))
                .andExpect(model().attribute("guest", hasProperty("confirm", is(guest.getConfirm()))))
                .andExpect(model().attribute("guest", hasProperty("role", is(guest.getRole()))))
                .andExpect(model().attribute("guest", hasProperty("firstName", is(guest.getFirstName()))))
                .andExpect(model().attribute("guest", hasProperty("lastName", is(guest.getLastName()))));
        SecurityContextHolder.getContext().setAuthentication(null);
        guestService.delete(guest.getId());
    }

    @Test
    public void testRoomGet() throws Exception {
        mockMvc.perform(get("/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("new"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("room", hasProperty("id", nullValue())))
                .andExpect(model().attribute("room", hasProperty("number", nullValue())))
                .andExpect(model().attribute("room", hasProperty("type", nullValue())))
                .andExpect(model().attribute("room", hasProperty("direction", nullValue())))
                .andExpect(model().attribute("room", hasProperty("hotel", nullValue())));
    }

    @Test
    public void testRoomPost() throws Exception {
        if (roomService.findByNumber(room.getNumber()) != null) {
            roomService.delete(roomService.findByNumber(room.getNumber()).getId());
        }
        mockMvc.perform(get("/"));
        mockMvc.perform(post("/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("room", new RoomBuilder().build())
                .param("number", room.getNumber())
                .param("type", room.getType().name())
                .param("direction", room.getDirection().name()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"))
                .andExpect(forwardedUrl(null));
        roomService.delete(roomService.findByNumber(room.getNumber()).getId());
    }

    @Test
    public void testRoomPostNumberError() throws Exception {
        mockMvc.perform(get("/"));
        mockMvc.perform(post("/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("room", new RoomBuilder().build())
                .param("number", "")
                .param("type", room.getType().name())
                .param("direction", room.getDirection().name()))
                .andExpect(status().isOk())
                .andExpect(view().name("new"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("room", "number"));
    }

    @Test
    public void testRoomShow() throws Exception {
        if (roomService.findByNumber(room.getNumber()) != null) {
            roomService.delete(roomService.findByNumber(room.getNumber()).getId());
        }
        hotel = hotelService.findOne(1L);
        mockMvc.perform(get("/"));
        mockMvc.perform(post("/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("room", new RoomBuilder().build())
                .param("number", room.getNumber())
                .param("type", room.getType().name())
                .param("direction", room.getDirection().name()));
        room = roomService.findByNumber(room.getNumber());
        mockMvc.perform(get("/{roomId}", room.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("show"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("room", hasProperty("id", is(room.getId()))))
                .andExpect(model().attribute("room", hasProperty("number", is(room.getNumber()))))
                .andExpect(model().attribute("room", hasProperty("type", is(room.getType()))))
                .andExpect(model().attribute("room", hasProperty("direction", is(room.getDirection()))))
                .andExpect(model().attribute("room", hasProperty("hotel", notNullValue())));
        Assert.assertEquals(hotel.getId(), room.getHotel().getId());
        roomService.delete(room.getId());
    }

    @Test
    public void testRoomEdit() throws Exception {
        if (roomService.findByNumber(room.getNumber()) != null) {
            roomService.delete(roomService.findByNumber(room.getNumber()).getId());
        }
        hotel = hotelService.findOne(1L);
        mockMvc.perform(get("/"));
        mockMvc.perform(post("/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("room", new RoomBuilder().build())
                .param("number", room.getNumber())
                .param("type", room.getType().name())
                .param("direction", room.getDirection().name()));
        room = roomService.findByNumber(room.getNumber());
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("edit", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("room", hasProperty("id", is(room.getId()))))
                .andExpect(model().attribute("room", hasProperty("number", is(room.getNumber()))))
                .andExpect(model().attribute("room", hasProperty("type", is(room.getType()))))
                .andExpect(model().attribute("room", hasProperty("direction", is(room.getDirection()))))
                .andExpect(model().attribute("room", hasProperty("hotel", notNullValue())));
        Assert.assertEquals(hotel.getId(), room.getHotel().getId());
        roomService.delete(room.getId());
    }

    @Test
    public void testRoomDelete() throws Exception {
        if (roomService.findByNumber(room.getNumber()) != null) {
            roomService.delete(roomService.findByNumber(room.getNumber()).getId());
        }
        hotel = hotelService.findOne(1L);
        mockMvc.perform(get("/"));
        mockMvc.perform(post("/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("room", new RoomBuilder().build())
                .param("number", room.getNumber())
                .param("type", room.getType().name())
                .param("direction", room.getDirection().name()));
        room = roomService.findByNumber(room.getNumber());
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("delete", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"))
                .andExpect(forwardedUrl(null));
        Assert.assertNull(roomService.findByNumber(room.getNumber()));
    }

    @Test
    public void testReservationGet() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("make"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attribute("reservation", hasProperty("id", nullValue())))
                .andExpect(model().attribute("reservation", hasProperty("from", nullValue())))
                .andExpect(model().attribute("reservation", hasProperty("to", nullValue())))
                .andExpect(model().attribute("reservation", hasProperty("cancelled", is(false))))
                .andExpect(model().attribute("reservation", hasProperty("guest", notNullValue())))
                .andExpect(model().attribute("reservation", hasProperty("room", notNullValue())));
        destroyReservation();
    }

    @Test
    public void testReservationPost() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""));
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(reservation.getFrom().toInstant()))
                .param("to", dateTimeFormatter.print(reservation.getTo().toInstant()))
                .param("cancelled", reservation.isCancelled() ? "true" : "false"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"))
                .andExpect(forwardedUrl(null));
        destroyReservation();
    }

    @Test
    public void testReservationPostFromError() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""));
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", "")
                .param("to", dateTimeFormatter.print(reservation.getTo().toInstant()))
                .param("cancelled", reservation.isCancelled() ? "true" : "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("make"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("reservation", "from"));
        destroyReservation();
    }

    @Test
    public void testReservationPostToError() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""));
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(reservation.getFrom().toInstant()))
                .param("to", "")
                .param("cancelled", reservation.isCancelled() ? "true" : "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("make"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"))
                .andExpect(model().attributeHasFieldErrors("reservation", "to"));
        destroyReservation();
    }

    @Test
    public void testReservationPostToBeforeFromError() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""));
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(reservation.getTo().toInstant()))
                .param("to", dateTimeFormatter.print(reservation.getFrom().toInstant()))
                .param("cancelled", reservation.isCancelled() ? "true" : "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("make"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"));
        destroyReservation();
    }

    @Test
    public void testReservationPostBusyError() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""));
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(reservation.getFrom().toInstant()))
                .param("to", dateTimeFormatter.print(reservation.getTo().toInstant()))
                .param("cancelled", reservation.isCancelled() ? "true" : "false"));
        DateTime newFrom = reservation.getFrom().plusDays(1);
        DateTime newTo = reservation.getTo().plusDays(1);
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(newFrom.toInstant()))
                .param("to", dateTimeFormatter.print(newTo.toInstant()))
                .param("cancelled", reservation.isCancelled() ? "true" : "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("make"))
                .andExpect(forwardedUrl("/WEB-INF/layouts/default.jspx"));
        destroyReservation();
    }

    @Test
    public void testReservationPostNotBusyIfCancelled() throws Exception {
        initReservation();
        mockMvc.perform(get("/{roomId}", room.getId())
                .param("add", ""));
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(reservation.getFrom().toInstant()))
                .param("to", dateTimeFormatter.print(reservation.getTo().toInstant()))
                .param("cancelled", "true"));
        DateTime newFrom = reservation.getFrom().plusDays(1);
        DateTime newTo = reservation.getTo().plusDays(1);
        mockMvc.perform(post("/{roomId}", room.getId())
                .param("add", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("reservation", new ReservationBuilder().build())
                .param("from", dateTimeFormatter.print(newFrom.toInstant()))
                .param("to", dateTimeFormatter.print(newTo.toInstant()))
                .param("cancelled", reservation.isCancelled() ? "true" : "false"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/"))
                .andExpect(forwardedUrl(null));
        destroyReservation();
    }

    private void initReservation() throws Exception {
        if (guestService.findByLogin(guest.getLogin()) != null) {
            guestService.delete(guestService.findByLogin(guest.getLogin()).getId());
        }
        mockMvc.perform(post("/sec_registry")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("guest", new GuestBuilder().build())
                .param("login", guest.getLogin())
                .param("password", guest.getPassword())
                .param("confirm", guest.getConfirm())
                .param("role", guest.getRole().name())
                .param("firstName", guest.getFirstName())
                .param("lastName", guest.getLastName()));
        guest = guestService.findByLogin(guest.getLogin());
        if (roomService.findByNumber(room.getNumber()) != null) {
            roomService.delete(roomService.findByNumber(room.getNumber()).getId());
        }
        mockMvc.perform(get("/"));
        mockMvc.perform(post("/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("room", new RoomBuilder().build())
                .param("number", room.getNumber())
                .param("type", room.getType().name())
                .param("direction", room.getDirection().name()));
        room = roomService.findByNumber(room.getNumber());
        for (Reservation reservationToDelete : reservationService.findAllByGuestAndRoom(guest, room)) {
            reservationService.delete(reservationToDelete.getId());
        }
    }

    private void destroyReservation() {
        for (Reservation reservationToDelete : reservationService.findAllByGuestAndRoom(guest, room)) {
            reservationService.delete(reservationToDelete.getId());
        }
        roomService.delete(room.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
        guestService.delete(guest.getId());
    }
}
