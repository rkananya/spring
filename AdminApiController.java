package com.app.demo.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.app.demo.UserExcelExporter;
import com.app.demo.model.*;
import com.app.demo.services.*;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired private HotelServices hotelService;
    @Autowired private UserServices userService;
    @Autowired private CateringServices cateringService;
    @Autowired private VendorServices vendorService;
    @Autowired private EventServices eventService;
    @Autowired private BookingServices bookingService;


    // ---------------- USER APIs ----------------
    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        if (keyword.isEmpty())
            return ResponseEntity.ok(userService.findAll());
        return ResponseEntity.ok(userService.findBykey(keyword));
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            userService.deleteUser(user.getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/users/{id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody User user) {
        userService.updateUserDetails(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getContactno(),
                user.getAddress(),
                user.getRole(),
                id
        );
        return ResponseEntity.ok().build();
    }

    // ---------------- HOTEL APIs ----------------
    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAll());
    }

    @PostMapping("/hotels")
    public ResponseEntity<String> addHotel(
            @RequestParam("hotelName") String name,
            @RequestParam("hotelDesc") String desc,
            @RequestParam("location") String location,
            @RequestParam("price") int price,
            @RequestParam("hotelImg1") MultipartFile file) {
        hotelService.savehoteltoDB(file, name, desc, location, price);
        return ResponseEntity.status(HttpStatus.CREATED).body("Hotel added successfully");
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable int id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PutMapping("/hotels/{id}")
    public ResponseEntity<String> updateHotel(
            @PathVariable int id,
            @RequestParam("hotelName") String name,
            @RequestParam("hotelDesc") String desc,
            @RequestParam("location") String location,
            @RequestParam("price") int price,
            @RequestParam(value = "hotelImg1", required = false) MultipartFile file) {

        if (file == null || file.isEmpty()) {
            hotelService.updateHotelDetails(name, desc, location, price, id);
        } else {
            hotelService.updateHotelDetailswithImage(name, desc, location, price, file, id);
        }
        return ResponseEntity.ok("Hotel updated successfully");
    }

    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }


    // ---------------- CATERING APIs ----------------
    @GetMapping("/caterings")
    public ResponseEntity<List<Catering>> getAllCaterings() {
        return ResponseEntity.ok(cateringService.findAll());
    }

    @PostMapping("/caterings")
    public ResponseEntity<String> addCatering(
            @RequestParam("catername") String name,
            @RequestParam("cater_desc") String desc,
            @RequestParam("cater_location") String location,
            @RequestParam("cater_price") int price,
            @RequestParam("cater_img") MultipartFile file) {
        cateringService.savecatertoDB(file, name, desc, location, price);
        return ResponseEntity.status(HttpStatus.CREATED).body("Catering added successfully");
    }

    @PutMapping("/caterings/{id}")
    public ResponseEntity<String> updateCatering(
            @PathVariable int id,
            @RequestParam("catername") String name,
            @RequestParam("cater_desc") String desc,
            @RequestParam("cater_location") String location,
            @RequestParam("cater_price") int price,
            @RequestParam(value = "cater_img", required = false) MultipartFile file) {

        if (file == null || file.isEmpty()) {
            cateringService.updateCaterDetails(name, desc, location, price, id);
        } else {
            cateringService.updateCaterDetailswithImage(name, desc, location, price, file, id);
        }
        return ResponseEntity.ok("Catering updated successfully");
    }

    @DeleteMapping("/caterings/{id}")
    public ResponseEntity<Void> deleteCatering(@PathVariable int id) {
        cateringService.deletecater(id);
        return ResponseEntity.noContent().build();
    }


    // ---------------- EVENT APIs ----------------
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @PostMapping("/events")
    public ResponseEntity<String> addEvent(
            @RequestParam("eventname") String name,
            @RequestParam("event_desc") String desc,
            @RequestParam("event_img") MultipartFile file) {
        eventService.saveeventtoDB(file, name, desc);
        return ResponseEntity.status(HttpStatus.CREATED).body("Event added successfully");
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<String> updateEvent(
            @PathVariable int id,
            @RequestParam("eventname") String name,
            @RequestParam("event_desc") String desc,
            @RequestParam(value = "event_img", required = false) MultipartFile file) {

        if (file == null || file.isEmpty()) {
            eventService.updateeventDetails(name, desc, id);
        } else {
            eventService.updateeventDetailswithImage(name, desc, file, id);
        }
        return ResponseEntity.ok("Event updated successfully");
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable int id) {
        eventService.deleteevent(id);
        return ResponseEntity.noContent().build();
    }


    // ---------------- BOOKING APIs ----------------
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    @PostMapping("/bookings/{id}/accept")
    public ResponseEntity<String> acceptBooking(@PathVariable int id) {
        bookingService.bookingacceptByAdmin(id);
        return ResponseEntity.ok("Booking accepted");
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable int id) {
        bookingService.bookingcancelByAdmin(id);
        return ResponseEntity.ok("Booking cancelled");
    }

    @GetMapping("/bookings/export/excel")
    public void exportBookingsToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        response.setHeader("Content-Disposition", "attachment; filename=BookingDetails_" + currentDateTime + ".xlsx");
        List<Booking> bookings = bookingService.findAllandSortBy();
        new UserExcelExporter(bookings).export(response);
    }
}
