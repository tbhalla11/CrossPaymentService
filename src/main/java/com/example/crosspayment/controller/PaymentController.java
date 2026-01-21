package com.example.crosspayment.controller;

import com.example.crosspayment.model.PaymentStatus;
import com.example.crosspayment.service.PaymentService;
import com.example.crosspayment.dto.PaymentRequest;
import com.example.crosspayment.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;


    /**
     * create payment
     * POST /api/payments
     *
     * Request Body:
     * {
     *     "sender" : "Bob Doe",
     *     "receiver": "John Wick",
     *     "amount": "400.00",
     *     "sourceCurrency": "USD",
     *     "destinationCurrency": "EUR"
     * }
     *
     * Response:
     * {
     *    "paymentId": "12345",
     *    "status": "SUCCESS",
     *    "convertedAmount": "350.00",
     *    "exchangeRate": "0.875",
     *    ...
     * }
     *
     * @param request request
     * @return {@link ResponseEntity}
     * @see ResponseEntity
     * @see PaymentResponse
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request){

        PaymentResponse response = paymentService.processPayment(request);

        HttpStatus status = PaymentStatus.SUCCESS.equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }


    /**
     *
     * GET Payment - GET /api/payments/{id}
     *
     * Response (found):
     * {
     *    "paymentId": "12345",
     *    "status": "SUCCESS",
     *    "convertedAmount": "350.00",
     *    "exchangeRate": "0.875",
     *    ...
     * }
     *
     * Response (not found):
     * {
     *   "error": "Payment not found"
     *   "status": 404,
     *   "timestamp": "2023-10-01T12:00:00"
     *   ...
     * }
     *
     * @param id payment id
     * @return {@link ResponseEntity}
     */

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id){
        log.info("Fetching payment with id: {}", id);
        PaymentResponse response = paymentService.getPaymentById(id);

        if(response == null){
            log.warn("Payment with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(response);
    }


}
