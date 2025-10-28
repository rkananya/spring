package com.app.demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import com.app.demo.UserNotFoundException;
import com.app.demo.UserPDFExporter;
import com.app.demo.Utility;
import com.app.demo.model.Booking;
import com.app.demo.model.Catering;
import com.app.demo.model.Event;
import com.app.demo.model.Hotel;
import com.app.demo.model.User;
import com.app.demo.model.Vendor;
import com.app.demo.services.BookingServices;
import com.app.demo.services.CateringServices;
import com.app.demo.services.EventServices;
import com.app.demo.services.HotelServices;
import com.app.demo.services.TwillioService;
import com.app.demo.services.UserServices;
import com.app.demo.services.VendorServices;
import com.lowagie.text.DocumentException;

import net.bytebuddy.utility.RandomString;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired private UserServices userservice;
    @Autowired private HotelServices hotelservice;
    @Autowired private CateringServices caterservice;
    @Autowired private VendorServices vendorservice;
    @Autowired private EventServices eventservice;
    @Autowired private BookingServices bookingservice;
    @Autowired private JavaMailSender mailSender;
    @Autowired private TwillioService twillioService;

    @Value("${app.twillio.fromPhoneNo}")
    private String from;
    @Value("${app.twillio.toPhoneNo}")
    private String to;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        this.to = "+91" + user.getContactno();

        User existing = userservice.findByEmail(user.getEmail());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        userservice.save(user);
        String body = "Hello " + fullName + "\nWelcome to EXQUISITE\nThank you for registering!\nEmail: " + user.getEmail() + "\nPassword: " + user.getPassword();
        twillioService.sendSms(to, from, body);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpSession session) {
        User userDetail = userservice.findByEmail(user.getEmail());
        if (userDetail != null && userDetail.getPassword().equals(user.getPassword())) {
            session.setAttribute("User", userDetail);
            return ResponseEntity.ok(userDetail);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings(HttpSession session) {
        if (session.getAttribute("User") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        User user = (User) session.getAttribute("User");
        List<Booking> bookings = bookingservice.findAllByUser(user);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User user, HttpSession session) {
        userservice.updateUserProfile(
            user.getEmail(), user.getFirstName(), user.getLastName(), user.getGender(),
            user.getContactno(), user.getAddress(), user.getRole(),
            user.getPassword(), user.getConfirmPassword(), user.getId()
        );
        session.setAttribute("User", user);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getHotels() {
        return ResponseEntity.ok(hotelservice.findAll());
    }

    @GetMapping("/caterings")
    public ResponseEntity<List<Catering>> getCaterings() {
        return ResponseEntity.ok(caterservice.findAll());
    }

    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getVendors() {
        return ResponseEntity.ok(vendorservice.findAll());
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(eventservice.findAll());
    }

    @GetMapping("/hotel/{id}")
    public ResponseEntity<?> getHotel(@PathVariable int id) {
        try {
            return ResponseEntity.ok(hotelservice.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hotel not found");
        }
    }

    @GetMapping("/catering/{id}")
    public ResponseEntity<?> getCatering(@PathVariable int id) {
        try {
            return ResponseEntity.ok(caterservice.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Catering not found");
        }
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<?> getEvent(@PathVariable int id) {
        try {
            return ResponseEntity.ok(eventservice.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
    }

    @GetMapping("/vendor/{id}")
    public ResponseEntity<?> getVendor(@PathVariable int id) {
        try {
            return ResponseEntity.ok(vendorservice.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }
    }

    @PostMapping("/booking")
    public ResponseEntity<?> makeBooking(@RequestBody Booking booking,
                                         @RequestParam int user_id,
                                         @RequestParam int hotel_id,
                                         @RequestParam int event_id,
                                         @RequestParam int catering_id) {
        User user = userservice.findById(user_id);
        Hotel hotel = hotelservice.findById(hotel_id);
        Catering catering = caterservice.findById(catering_id);
        Event event = eventservice.findById(event_id);
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setCatering(catering);
        booking.setEvent(event);
        bookingservice.save(booking);
        return ResponseEntity.ok("Booking created successfully");
    }

    @PostMapping("/cancelBooking")
    public ResponseEntity<?> cancelBooking(@RequestParam int booking_id) {
        bookingservice.bookingcancelByUser(booking_id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @GetMapping("/invoice/{booking_id}")
    public void downloadInvoice(@PathVariable int booking_id, HttpServletResponse response) throws IOException, DocumentException {
        Booking booking = bookingservice.findById(booking_id);
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Invoice_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        UserPDFExporter exporter = new UserPDFExporter(booking);
        exporter.export(response);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam String email, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        String token = RandomString.make(30);
        try {
            userservice.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            return ResponseEntity.ok("Reset password link sent to email");
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    private void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        helper.setFrom("ganapathydaprojects@gmail.com", "EXQUISITE");
        helper.setTo(recipientEmail);
        helper.setSubject("Password Reset Link");
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br><p>Ignore if not requested.</p>";
        helper.setText(content, true);
        mailSender.send(msg);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String password) {
        User customer = userservice.getByResetPasswordToken(token);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        userservice.updatePassword(customer, password);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }
}
