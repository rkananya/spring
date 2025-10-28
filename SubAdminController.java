package com.app.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.demo.model.Catering;
import com.app.demo.model.Event;
import com.app.demo.model.Hotel;
import com.app.demo.model.User;
import com.app.demo.model.Vendor;
import com.app.demo.services.CateringServices;
import com.app.demo.services.EventServices;
import com.app.demo.services.HotelServices;
import com.app.demo.services.UserServices;
import com.app.demo.services.VendorServices;

@RestController
@RequestMapping("/api/subadmin")
public class SubAdminController {

    @Autowired
    private HotelServices hotelservice;

    @Autowired
    private VendorServices vendorservice;

    @Autowired
    private CateringServices caterservice;

    @Autowired
    private EventServices eventservice;

    @Autowired
    private UserServices userservice;

    // ---------------- CATERING ----------------
    @GetMapping("/cateringdetails")
    public ResponseEntity<?> subAdminCateringDetails() {
        List<Catering> cater = caterservice.findAll();
        return ResponseEntity.ok(Map.of("caterlist", cater));
    }

    @PostMapping("/cateringSearch")
    public ResponseEntity<?> subadminCateringSearch(@RequestParam("valueToSearch") String searchkey) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(searchkey);

        if (searchkey.equals("")) {
            List<Catering> cater = caterservice.findAll();
            response.put("caterlist", cater);
        } else {
            response.put("catering_keyword", searchkey);
            List<Catering> cater = caterservice.findBykey(searchkey);
            response.put("caterlist", cater);
        }
        return ResponseEntity.ok(response);
    }

    // ---------------- HOTEL ----------------
    @GetMapping("/hoteldetails")
    public ResponseEntity<?> subAdminHotelDetails() {
        List<Hotel> hotel = hotelservice.findAll();
        return ResponseEntity.ok(Map.of("hotellist", hotel));
    }

    @PostMapping("/hotelSearch")
    public ResponseEntity<?> subadminHotelSearch(@RequestParam("valueToSearch") String searchkey) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(searchkey);

        if (searchkey.equals("")) {
            List<Hotel> hotel = hotelservice.findAll();
            response.put("hotellist", hotel);
        } else {
            response.put("hotel_keyword", searchkey);
            List<Hotel> hotel = hotelservice.findBykey(searchkey);
            response.put("hotellist", hotel);
        }
        return ResponseEntity.ok(response);
    }

    // ---------------- VENDOR ----------------
    @GetMapping("/vendordetails")
    public ResponseEntity<?> subAdminVendorDetails() {
        List<Vendor> vendor = vendorservice.findAll();
        return ResponseEntity.ok(Map.of("vendorlist", vendor));
    }

    @PostMapping("/vendorSearch")
    public ResponseEntity<?> subadminVendorSearch(@RequestParam("valueToSearch") String searchkey) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(searchkey);

        if (searchkey.equals("")) {
            List<Vendor> vendor = vendorservice.findAll();
            response.put("vendorlist", vendor);
        } else {
            response.put("vendor_keyword", searchkey);
            List<Vendor> vendor = vendorservice.findBykey(searchkey);
            response.put("vendorlist", vendor);
        }
        return ResponseEntity.ok(response);
    }

    // ---------------- BOOKING ----------------
    @GetMapping("/bookingdetails")
    public ResponseEntity<?> subAdminBookingDetails() {
        return ResponseEntity.ok(Map.of("message", "Booking details endpoint - not implemented"));
    }

    // ---------------- EVENT ----------------
    @GetMapping("/eventdetails")
    public ResponseEntity<?> subAdminEventDetails() {
        List<Event> event = eventservice.findAll();
        return ResponseEntity.ok(Map.of("eventlist", event));
    }

    @PostMapping("/eventSearch")
    public ResponseEntity<?> subadminEventSearch(@RequestParam("valueToSearch") String searchkey) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(searchkey);

        if (searchkey.equals("")) {
            List<Event> event = eventservice.findAll();
            response.put("eventlist", event);
        } else {
            response.put("event_keyword", searchkey);
            List<Event> event = eventservice.findBykey(searchkey);
            response.put("eventlist", event);
        }
        return ResponseEntity.ok(response);
    }

    // ---------------- ACCOUNT ----------------
    @GetMapping("/account")
    public ResponseEntity<?> subAdminAccount(HttpSession session) {
        Map<String, Object> accountDetails = new HashMap<>();
        accountDetails.put("firstname", session.getAttribute("Subadmin_firstname"));
        accountDetails.put("lastname", session.getAttribute("Subadmin_lastname"));
        accountDetails.put("email", session.getAttribute("Subadmin_email"));
        accountDetails.put("phone", session.getAttribute("Subadmin_phone"));
        accountDetails.put("address", session.getAttribute("Subadmin_address"));
        accountDetails.put("gender", session.getAttribute("Subadmin_gender"));
        accountDetails.put("role", session.getAttribute("Subadmin_role"));
        return ResponseEntity.ok(accountDetails);
    }

    // ---------------- EDIT PROFILE ----------------
    @PostMapping("/editsubadminprofile")
    public ResponseEntity<?> updateUserProfile(@ModelAttribute("subadmineditprofile") User subadmin, HttpSession session) {
        System.out.println(subadmin);

        userservice.updateUserProfile(
                subadmin.getEmail(),
                subadmin.getFirstName(),
                subadmin.getLastName(),
                subadmin.getGender(),
                subadmin.getContactno(),
                subadmin.getAddress(),
                subadmin.getRole(),
                subadmin.getPassword(),
                subadmin.getConfirmPassword(),
                subadmin.getId());

        // Update session attributes
        session.setAttribute("Subadmin_firstname", subadmin.getFirstName());
        session.setAttribute("Subadmin_lastname", subadmin.getLastName());
        session.setAttribute("Subadmin_email", subadmin.getEmail());
        session.setAttribute("Subadmin_phone", subadmin.getContactno());
        session.setAttribute("Subadmin_address", subadmin.getAddress());
        session.setAttribute("Subadmin_gender", subadmin.getGender());
        session.setAttribute("Subadmin_id", subadmin.getId());
        session.setAttribute("Subadmin_role", subadmin.getRole());
        session.setAttribute("Subadmin_cpassword", subadmin.getConfirmPassword());
        session.setAttribute("Subadmin_password", subadmin.getPassword());

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    // ---------------- LOGOUT ----------------
    @GetMapping("/logout")
    public ResponseEntity<?> subadminlogout(HttpSession session) {
        if (session != null) {
            session.removeAttribute("Subadmin_firstname");
            session.removeAttribute("Subadmin_lastname");
            session.removeAttribute("Subadmin_email");
            session.removeAttribute("Subadmin_phone");
            session.removeAttribute("Subadmin_address");
            session.removeAttribute("Subadmin_gender");
            session.removeAttribute("Subadmin_id");
            session.removeAttribute("Subadmin_cpassword");
            session.removeAttribute("Subadmin_password");
            session.removeAttribute("Subadmin_role");
        }
        return ResponseEntity.ok(Map.of("message", "Subadmin logged out successfully"));
    }
}
