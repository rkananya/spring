package com.app.demo.controller;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.demo.model.PaytmDetails;
import com.app.demo.model.User;
import com.app.demo.services.BookingServices;
import com.app.demo.services.UserServices;
import com.paytm.pg.merchant.PaytmChecksum;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    @Autowired
    private PaytmDetails paytmDetails;

    @Autowired
    private Environment env;

    @Autowired
    private BookingServices bookingService;

    @Autowired
    private UserServices userService;

    private int bookingId;
    private int userId;

    // Step 1: Initialize payment request
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiatePayment(
            @RequestParam("booking_id") int bookingId,
            @RequestParam("booking_userid") int userId,
            @RequestParam("total_amt") long amount) {

        this.bookingId = bookingId;
        this.userId = userId;

        Map<String, Object> response = Map.of(
                "orderId", bookingId,
                "userId", userId,
                "amount", amount,
                "message", "Payment initiated successfully"
        );
        return ResponseEntity.ok(response);
    }

    // Step 2: Redirect to Paytm with checksum
    @PostMapping("/redirect")
    public ResponseEntity<Map<String, String>> getRedirectParams(
            @RequestParam("CUST_ID") String customerId,
            @RequestParam("TXN_AMOUNT") String transactionAmount,
            @RequestParam("ORDER_ID") String orderId) throws Exception {

        TreeMap<String, String> parameters = new TreeMap<>();
        paytmDetails.getDetails().forEach(parameters::put);

        parameters.put("MOBILE_NO", env.getProperty("paytm.mobile"));
        parameters.put("EMAIL", env.getProperty("paytm.email"));
        parameters.put("ORDER_ID", orderId);
        parameters.put("TXN_AMOUNT", transactionAmount);
        parameters.put("CUST_ID", customerId);

        String checkSum = PaytmChecksum.generateSignature(parameters, paytmDetails.getMerchantKey());
        parameters.put("CHECKSUMHASH", checkSum);

        parameters.put("redirectUrl", paytmDetails.getPaytmUrl());

        return ResponseEntity.ok(parameters);
    }

    // Step 3: Handle Paytm response
    @PostMapping("/response")
    public ResponseEntity<Map<String, Object>> getPaymentResponse(HttpServletRequest request) {

        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<>();
        mapData.forEach((key, val) -> parameters.put(key, val[0]));

        String checksum = parameters.getOrDefault("CHECKSUMHASH", "");

        try {
            boolean isValidChecksum = PaytmChecksum.verifySignature(parameters,
                    paytmDetails.getMerchantKey(), checksum);

            if (!isValidChecksum) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "FAILED", "message", "Checksum mismatch"));
            }

            if ("01".equals(parameters.get("RESPCODE"))) {
                bookingService.bookingPayment(bookingId);
                User user = userService.findById(userId);
                return ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "message", "Payment successful",
                        "user", user,
                        "bookingId", bookingId
                ));
            } else {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(Map.of("status", "FAILED", "message", "Payment failed"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }
}
