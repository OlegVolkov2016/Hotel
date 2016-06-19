package com.axiomsl.hotel.controller;

import com.axiomsl.hotel.configuration.UrlUtil;
import com.axiomsl.hotel.model.*;
import com.axiomsl.hotel.service.GuestService;
import com.axiomsl.hotel.service.HotelService;
import com.axiomsl.hotel.service.ReservationService;
import com.axiomsl.hotel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Main controller
 */
@Controller
@RequestMapping("/")
public class HotelController {
    private final Logger logger = LoggerFactory.getLogger(HotelController.class);

    private GuestService guestService;
    private UserDetailsService userDetailsService;
    private HotelService hotelService;
    private RoomService roomService;
    private ReservationService reservationService;
    private MessageSource messageSource;
    private Md5PasswordEncoder md5PasswordEncoder;

    private Guest guest;
    private Hotel hotel;
    private Room room;
    private List<Room> rooms;
    private Room searchRoom;
    private String oldPassword;

    private final Long HOTEL_ID = 1L;

    /* Start page with rooms list */
    @RequestMapping(method = RequestMethod.GET)
    public String listRooms(Model model) {
        setGuest();
        logger.info("Listing rooms");
        if (searchRoom == null) {
            searchRoom = new Room();
        }
        hotel = hotelService.findOne(HOTEL_ID);
        rooms = roomService.findAllByCriteria(searchRoom.getNumber(), searchRoom.getType(), searchRoom.getDirection());
        logger.info("No. of rooms: " + rooms.size());
        setMainAttributes(model);
        return "list";
    }

    /* Update search criterias */
    @RequestMapping(method = RequestMethod.POST)
    public String searchRooms(Room searchRoom, Model model, HttpServletRequest httpServletRequest,
                              RedirectAttributes redirectAttributes, Locale locale) {
        this.searchRoom = searchRoom;
        return "redirect:/";
    }

    /* Clear search criterias */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Model model) {
        searchRoom = new Room();
        return "redirect:/";
    }

    /* Login error messagging */
    @RequestMapping(value = "/login_error", method = RequestMethod.GET)
    public String loginErrorMessage(Model model, Locale locale) {
        setMainAttributes(model);
        model.addAttribute("message", new Message("error", messageSource.getMessage("login_error_text", new Object[] {}, locale)));
        return "list";
    }

    /* Login need messaging */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginNeededMessage(Model model, Locale locale) {
        setMainAttributes(model);
        model.addAttribute("message", new Message("error", messageSource.getMessage("login_need_text", new Object[] {}, locale)));
        return "list";
    }

    /* Room information */
    @RequestMapping(value = "/{roomId}", method = RequestMethod.GET)
    public String showRoom(@PathVariable("roomId") Long roomId, Model model) {
        Room room = roomService.findOne(roomId);
        setMainAttributes(model);
        model.addAttribute("room", room);
        return "show";
    }

    /* Delete room from hotel */
    @RequestMapping(value = "/{roomId}", params = "delete", method = RequestMethod.GET)
    public String deleteRoom(@PathVariable("roomId") Long roomId, Model model) {
        roomService.delete(roomId);
        return "redirect:/";
    }

    /* Edit room information */
    @RequestMapping(value = "/{roomId}", params = "edit", method = RequestMethod.GET)
    public String editRoom(@PathVariable("roomId") Long roomId, Model model) {
        Room room = roomService.findOne(roomId);
        setMainAttributes(model);
        setRoomAttributes(model, room);
        return "edit";
    }

    /* Update room information */
    @RequestMapping(value = "/{roomId}", params = "edit", method = RequestMethod.POST)
    public String updateRoom(@Valid Room room, BindingResult bindingResult, Model model,
                             HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Updating room");
        room.setHotel(hotel);
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_save_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            setRoomAttributes(model, room);
            return "edit";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("room_save_success_text", new Object[]{}, locale)));
        roomService.save(room);
        setGuest();
        return "redirect:/" + UrlUtil.encodeUrlPathSegment(room.getId().toString(),
                httpServletRequest);
    }

    /* Create new room */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newRoom(Model model) {
        Room room = new Room();
        setMainAttributes(model);
        setRoomAttributes(model, room);
        return "new";
    }

    /* Add new room to hotel */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String createRoom(@Valid Room room, BindingResult bindingResult, Model model,
                             HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Creating room");
        room.setHotel(hotel);
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_save_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            setRoomAttributes(model, room);
            return "new";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("room_save_success_text", new Object[]{}, locale)));
        roomService.save(room);
        logger.info("Room id: " + room.getId());
        return "redirect:/";
    }

    /* Add reservation */
    @RequestMapping(value = "/{roomId}", params="add", method = RequestMethod.GET)
    public String addReservation(@PathVariable("roomId") Long roomId, Model model) {
        logger.info("Adding a reservation");
        room = roomService.findOne(roomId);
        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCancelled(false);
        setMainAttributes(model);
        model.addAttribute("room", room);
        model.addAttribute("reservation", reservation);
        return "make";
    }

    /* Make reservation */
    @RequestMapping(value = "/{roomId}", params="add", method = RequestMethod.POST)
    public String makeReservation(@Valid Reservation reservation, BindingResult bindingResult, Model model,
                                  HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Making reservation");
        reservation.setGuest(guest);
        reservation.setRoom(room);
        if (bindingResult.hasErrors() || reservation.getFrom() == null || reservation.getTo() == null) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_add_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            model.addAttribute("room", room);
            model.addAttribute("reservation", reservation);
            return "make";
        }
        if (reservation.getTo().isBefore(reservation.getFrom().plusDays(1).toInstant())) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_dates_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            model.addAttribute("room", room);
            model.addAttribute("reservation", reservation);
            return "make";
        }
        Long realId = reservation.getId();
        if (realId == null) {
            realId = 0L;
        }
        List<Reservation> existing = reservationService.findOneByRoomAndDates(realId,
                reservation.getRoom().getId(), reservation.getFrom(), reservation.getTo());
        if (existing.size() > 0) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_check_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            model.addAttribute("room", room);
            model.addAttribute("reservation", reservation);
            return "make";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("room_add_success_text", new Object[]{}, locale)));
        reservationService.save(reservation);
        return "redirect:/";
    }

    /* Edit reservation */
    @RequestMapping(value = "/{sId}", params="res", method = RequestMethod.GET)
    public String editReservation(@PathVariable("sId") Long reservationId, Model model) {
        logger.info("Editing a reservation");
        Reservation reservation = reservationService.findOne(reservationId);
        room = reservation.getRoom();
        setMainAttributes(model);
        model.addAttribute("room", room);
        model.addAttribute("reservation", reservation);
        return "make";
    }

    /* Updating reservation */
    @RequestMapping(value = "/{sId}", params="res", method = RequestMethod.POST)
    public String updateReservation(@Valid Reservation reservation, BindingResult bindingResult, Model model,
                                  HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Updating reservation");
        reservation.setGuest(guest);
        reservation.setRoom(room);
        if (bindingResult.hasErrors() || reservation.getFrom() == null || reservation.getTo() == null) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_add_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            model.addAttribute("room", room);
            model.addAttribute("reservation", reservation);
            return "make";
        }
        List<Reservation> existing = reservationService.findOneByRoomAndDates(reservation.getId(),
                reservation.getRoom().getId(), reservation.getFrom(), reservation.getTo());
        if (existing.size() > 0) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("room_check_error_text", new Object[]{}, locale)));
            setMainAttributes(model);
            model.addAttribute("room", room);
            model.addAttribute("reservation", reservation);
            return "make";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("room_add_success_text", new Object[]{}, locale)));
        reservationService.save(reservation);
        return "redirect:/";
    }

    /* Edit hotel information */
    @RequestMapping(value = "/location", method = RequestMethod.GET)
    public String editHotel(Model model) {
        setGuestAttributes(model, guest);
        return "hotel";
    }

    /* Update hotel information */
    @RequestMapping(value = "/location", method = RequestMethod.POST)
    public String updateHotel(@Valid Hotel hotel, BindingResult bindingResult, Model model,
                             HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Updating hotel");
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("hotel_save_error_text", new Object[]{}, locale)));
            this.hotel = hotel;
            setMainAttributes(model);
            return "hotel";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("hotel_save_success_text", new Object[]{}, locale)));
        this.hotel = hotelService.save(hotel);
        return "redirect:/";
    }

    /* Edit guest information */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String info(Model model) {
        oldPassword = guest.getPassword();
        setGuestAttributes(model, guest);
        return "info";
    }

    /* Update guest information */
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public String updateInfo(@Valid Guest guest, BindingResult bindingResult, Model model,
                             HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Updating guest");
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("user_save_error_text", new Object[]{}, locale)));
            setGuestAttributes(model, guest);
            return "info";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("user_save_success_text", new Object[]{}, locale)));
        String newPassword = oldPassword;
        if (!guest.getPassword().equals(oldPassword)) {
            logger.info("Updating password");
            newPassword = md5PasswordEncoder.encodePassword(guest.getPassword(), null);
        }
        guest.setPassword(newPassword);
        guest.setConfirm(newPassword);
        guestService.save(guest);
        authenticateGuest(guest);
        return "redirect:/";
    }

    /* New guest registration */
    @RequestMapping(value = "/sec_registry", method = RequestMethod.GET)
    public String register(Model model) {
        Guest guest = new Guest();
        setGuestAttributes(model, guest);
        return "sec_registry";
    }

    /* Add new guest to hotel */
    @RequestMapping(value = "/sec_registry", method = RequestMethod.POST)
    public String saveRegistry(@Valid Guest guest, BindingResult bindingResult, Model model,
                             HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Registering guest");
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", new Message("error",
                    messageSource.getMessage("user_save_error_text", new Object[]{}, locale)));
            setGuestAttributes(model, guest);
            return "sec_registry";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("user_save_success_text", new Object[]{}, locale)));
        String newPassword = oldPassword;
        if (!guest.getPassword().equals(oldPassword)) {
            logger.info("Registering password");
            newPassword = md5PasswordEncoder.encodePassword(guest.getPassword(), null);
        }
        guest.setPassword(newPassword);
        guest.setConfirm(newPassword);
        if (guest.getRole() == null) {
            guest.setRole(GuestRole.ROLE_GUEST);
        }
        guestService.save(guest);
        authenticateGuest(guest);
        return "redirect:/";
    }

    /* Set main attributes */
    private void setMainAttributes(Model model) {
        model.addAttribute("guest", guest);
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", rooms);
        model.addAttribute("search", searchRoom);
        model.addAttribute("types", RoomType.values());
        model.addAttribute("directions", RoomDirection.values());
    }

    /* Set guest attributes */
    private void setGuestAttributes(Model model, Guest guest) {
        model.addAttribute("hotel", hotel);
        model.addAttribute("guest", guest);
        model.addAttribute("roles", GuestRole.values());
    }

    /* Set room attributes */
    private void setRoomAttributes(Model model, Room room) {
        List<RoomType> listTypes = new ArrayList<RoomType>(Arrays.asList(RoomType.values()));
        listTypes.remove(RoomType.All);
        model.addAttribute("types", listTypes.toArray());
        List<RoomDirection> listDirections = new ArrayList<RoomDirection>(Arrays.asList(RoomDirection.values()));
        listDirections.remove(RoomDirection.ALL);
        model.addAttribute("directions", listDirections.toArray());
        model.addAttribute("room", room);
    }

    @Autowired
    public void setGuestService(GuestService guestService) {
        this.guestService = guestService;
    }

    @Autowired
    public void setHotelService(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Autowired
    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setMd5PasswordEncoder(Md5PasswordEncoder md5PasswordEncoder) {
        this.md5PasswordEncoder = md5PasswordEncoder;
    }

    /* Update guest from security engine */
    private void setGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails =
                    (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            guest = guestService.findByLogin(userDetails.getUsername());
            guest.setConfirm(guest.getPassword());
        } else {
            guest = null;
        }
    }

    /* Authenticate new guest in security engine */
    private void authenticateGuest(Guest guest) {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(guest.getRole().name()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(guest.getLogin());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities()));
    }
}
