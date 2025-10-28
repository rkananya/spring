package com.app.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.demo.model.*;
import com.app.demo.services.*;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminApiController {

    @Autowired
    private HotelServices hotelService;

    @Autowired
    private VendorServices vendorService;

    @Autowired
    private CateringServices cateringService;

    @Autowired
    private EventServices eventService;

    @Autowired
    private UserServices userService;

    @Autowired
    private BookingServices bookingService;

    // -------------------- CATERING --------------------

    @GetMapping("/caterings")
    public ResponseEntity<List<Catering>> getAllCaterings() {
        return ResponseEntity.ok(cateringService.findAll());
    }

    @GetMapping("/caterings/search")
    public ResponseEntity<List<Catering>> searchCaterings(@RequestParam String keyword) {
        if (keyword.isBlank()) {
            return ResponseEntity.ok(cateringService.findAll());
        }
        return ResponseEntity.ok(cateringService.findBykey(keyword));
    }

    // -------------------- HOTELS --------------------

    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAll());
    }

    @GetMapping("/hotels/search")
    public ResponseEntity<List<Hotel>> searchHotels(@RequestParam String keyword) {
        if (keyword.isBlank()) {
            return ResponseEntity.ok(hotelService.findAll());
        }
        return ResponseEntity.ok(hotelService.findBykey(keyword));
    }

    // -------------------- VENDORS --------------------

    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.findAll());
    }

    @GetMapping("/vendors/search")
    public ResponseEntity<List<Vendor>> searchVendors(@RequestParam String keyword) {
        if (keyword.isBlank()) {
            return ResponseEntity.ok(vendorService.findAll());
        }
        return ResponseEntity.ok(vendorService.findBykey(keyword));
    }

    // -------------------- BOOKINGS --------------------

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable("id") int bookingId) {
        bookingService.bookingcancelByAdmin(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @PutMapping("/bookings/{id}/accept")
    public ResponseEntity<String> acceptBooking(@PathVariable("id") int bookingId) {
        bookingService.bookingacceptByAdmin(bookingId);
        return ResponseEntity.ok("Booking accepted successfully");
    }

    // -------------------- EVENTS --------------------

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/events/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        if (keyword.isBlank()) {
            return ResponseEntity.ok(eventService.findAll());
        }
        return ResponseEntity.ok(eventService.findBykey(keyword));
    }

    // -------------------- SUPERADMIN ACCOUNT --------------------

    @GetMapping("/account/{id}")
    public ResponseEntity<User> getSuperAdminProfile(@PathVariable("id") int id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/account/update")
    public ResponseEntity<String> updateSuperAdminProfile(@RequestBody User superAdmin) {
        userService.updateUserProfile(
                superAdmin.getEmail(),
                superAdmin.getFirstName(),
                superAdmin.getLastName(),
                superAdmin.getGender(),
                superAdmin.getContactno(),
                superAdmin.getAddress(),
                superAdmin.getRole(),
                superAdmin.getPassword(),
                superAdmin.getConfirmPassword(),
                superAdmin.getId()
        );

        return ResponseEntity.ok("Super admin profile updated successfully");
    }

    // -------------------- LOGOUT --------------------
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // In a real REST app, you'd remove JWT or invalidate token instead of clearing session
        return ResponseEntity.ok("Super admin logged out successfully");
    }
}
