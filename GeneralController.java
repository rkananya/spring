package com.app.demo.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.model.User;
import com.app.demo.services.BookingServices;
import com.app.demo.services.CateringServices;
import com.app.demo.services.EventServices;
import com.app.demo.services.HotelServices;
import com.app.demo.services.UserServices;
import com.app.demo.services.VendorServices;

@RestController
@RequestMapping("/api")
public class GeneralController {

    @Autowired
    private UserServices userservice;

    @Autowired
    private HotelServices hotelservice;

    @Autowired
    private EventServices eventservice;

    @Autowired
    private CateringServices caterservice;

    @Autowired
    private VendorServices vendorservice;

    @Autowired
    private BookingServices bookingservice;

    @Autowired
    private JavaMailSender mailSender;

    // ---------------- HOME & BASIC ROUTES ----------------
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to EXQUISITE API Home");
    }

    @GetMapping("/signin")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Signin endpoint");
    }

    @GetMapping("/signup")
    public ResponseEntity<String> register() {
        return ResponseEntity.ok("Signup endpoint");
    }

    @GetMapping("/contactus")
    public ResponseEntity<String> contactus() {
        return ResponseEntity.ok("Contact Us endpoint");
    }

    @GetMapping("/aboutus")
    public ResponseEntity<String> aboutus() {
        return ResponseEntity.ok("About Us endpoint");
    }

    // ---------------- USER HOME ----------------
    @GetMapping("/userhome")
    public ResponseEntity<?> userhome(HttpSession session) {
        if (session.getAttribute("User_id") == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Please login first"));
        } else {
            int id = (int) session.getAttribute("User_id");
            Map<String, Long> data = new HashMap<>();

            data.put("hotel_count", hotelservice.hotelCount());
            data.put("event_count", eventservice.eventCount());
            data.put("catering_count", caterservice.cateringcount());
            data.put("vendor_count", vendorservice.vendorcount());
            data.put("booking_count", bookingservice.bookingcountById(id));
            data.put("booking_paid_count", bookingservice.bookingcountPaidById(id));
            data.put("booking_unpaid_count", bookingservice.bookingcountunPaidById(id));
            data.put("booking_cancelled_by_admin", bookingservice.bookingcountcancelByAdminById(id));
            data.put("booking_cancelled_by_user", bookingservice.bookingcountcancelByUserById(id));
            data.put("booking_pending_count", bookingservice.bookingPendingcountById(id));

            return ResponseEntity.ok(data);
        }
    }

    // ---------------- ADMIN HOME ----------------
    @GetMapping("/adminhome")
    public ResponseEntity<?> adminhome() {
        Map<String, Long> data = new HashMap<>();

        data.put("user_count", userservice.userCount());
        data.put("hotel_count", hotelservice.hotelCount());
        data.put("event_count", eventservice.eventCount());
        data.put("catering_count", caterservice.cateringcount());
        data.put("vendor_count", vendorservice.vendorcount());
        data.put("booking_count", bookingservice.bookingcount());
        data.put("booking_paid_count", bookingservice.bookingcountPaid());
        data.put("booking_unpaid_count", bookingservice.bookingcountunPaid());
        data.put("booking_cancelled_by_admin", bookingservice.bookingcountcancelByAdmin());
        data.put("booking_cancelled_by_user", bookingservice.bookingcountcancelByUser());
        data.put("booking_pending_count", bookingservice.bookingpendingcount());

        return ResponseEntity.ok(data);
    }

    // ---------------- SUBADMIN HOME ----------------
    @GetMapping("/subadminhome")
    public ResponseEntity<?> subAdminHome() {
        Map<String, Long> data = new HashMap<>();

        data.put("user_count", userservice.userCount());
        data.put("hotel_count", hotelservice.hotelCount());
        data.put("event_count", eventservice.eventCount());
        data.put("catering_count", caterservice.cateringcount());
        data.put("vendor_count", vendorservice.vendorcount());
        data.put("booking_count", bookingservice.bookingcount());
        data.put("booking_paid_count", bookingservice.bookingcountPaid());
        data.put("booking_unpaid_count", bookingservice.bookingcountunPaid());
        data.put("booking_cancelled_by_admin", bookingservice.bookingcountcancelByAdmin());
        data.put("booking_cancelled_by_user", bookingservice.bookingcountcancelByUser());
        data.put("booking_pending_count", bookingservice.bookingpendingcount());

        return ResponseEntity.ok(data);
    }

    // ---------------- SUPERADMIN HOME ----------------
    @GetMapping("/superadminhome")
    public ResponseEntity<?> superAdminHome() {
        Map<String, Long> data = new HashMap<>();

        data.put("user_count", userservice.userCount());
        data.put("hotel_count", hotelservice.hotelCount());
        data.put("event_count", eventservice.eventCount());
        data.put("catering_count", caterservice.cateringcount());
        data.put("vendor_count", vendorservice.vendorcount());
        data.put("booking_count", bookingservice.bookingcount());
        data.put("booking_paid_count", bookingservice.bookingcountPaid());
        data.put("booking_unpaid_count", bookingservice.bookingcountunPaid());
        data.put("booking_cancelled_by_admin", bookingservice.bookingcountcancelByAdmin());
        data.put("booking_cancelled_by_user", bookingservice.bookingcountcancelByUser());
        data.put("booking_pending_count", bookingservice.bookingpendingcount());

        return ResponseEntity.ok(data);
    }

    // ---------------- LOGOUT ----------------
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    // ---------------- LOGIN FAILED ----------------
    @GetMapping("/loginfailed")
    public ResponseEntity<String> loginfailed() {
        return ResponseEntity.status(401).body("Login failed");
    }

    // ---------------- CONTACT FORM ----------------
    @PostMapping("/contactForm")
    public ResponseEntity<?> contactForm(
            @RequestParam("FirstName") String f_name,
            @RequestParam("LastName") String l_name,
            @RequestParam("Email") String email,
            @RequestParam("Message") String msg) throws MessagingException, UnsupportedEncodingException {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            User user = userservice.findByRole("Admin");

            helper.setFrom(email, "EXQUISITE");
            helper.setTo(user.getEmail());

            String subject = "User Query";
            String content = "<p>First Name : " + f_name + "</p>"
                    + "<p>Last Name : " + l_name + "</p>"
                    + "<p>Email : " + email + "</p>"
                    + "<br>"
                    + "<p>Message / Query / FeedBack : </p>"
                    + "<br>"
                    + "<h1 style='color:orange'>" + msg + "</h1>";

            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);

            return ResponseEntity.ok(Map.of("success", "Your message has been sent successfully. We'll get back to you soon."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send message: " + e.getMessage()));
        }
    }
}
