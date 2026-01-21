package com.example.crosspayment.service;


import com.example.crosspayment.client.FXServiceClient;
import com.example.crosspayment.dto.PaymentRequest;
import com.example.crosspayment.dto.PaymentResponse;
import com.example.crosspayment.exception.FxServiceException;
import com.example.crosspayment.exception.PaymentNotFoundException;
import com.example.crosspayment.model.Payment;
import com.example.crosspayment.model.PaymentStatus;
import com.example.crosspayment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 *  * Payment Service to handle payment processing
 *
 *  This class will:
 *  *  - Process payments between different countries and currencies
 *  *  - Interact with FXServiceClient to get exchange rates and the database
 *  *  - Calculate final amounts after conversion
 *  *  - Handle payment validations and error scenarios
 *  *  - Update payment statuses
 *  *  - Return responses to the caller
 *
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final FXServiceClient fxServiceClient;
    private final PaymentRepository paymentRepository;


    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        try {
//            if (!fxServiceClient.isCurrencySupported(request.getSourceCurrency())) {
//                throw new IllegalArgumentException("Source currency not supported: " + request.getSourceCurrency());
//            }
            if (!fxServiceClient.isCurrencySupported(request.getDestinationCurrency())) {
                throw new IllegalArgumentException("Target currency not supported: " + request.getDestinationCurrency());
            }
        } catch (FxServiceException e) {
            log.error("Error checking supported currencies: {}", e.getMessage());
            throw new FxServiceException("Failed to validate supported currencies", e);
        }

        /**
         *
         * Create a new Payment entity with PENDING status
         *
         */
        Payment payment = Payment.builder().sender(request.getSender()).receiver(request.getReceiver()).amount(request.getAmount()).
                sourceCurrency(request.getSourceCurrency()).destinationCurrency(request.getDestinationCurrency()).
                status(PaymentStatus.PENDING).build();

        /**
         *
         * Saving initial payment with PENDING status
         * Starting payment processing
         */
        payment = paymentRepository.save(payment);

        try {
            /**
             *
             * Get exchange rate from FXServiceClient
             *
             */
            BigDecimal exchangeRate = fxServiceClient.getExchangeRate(request.getSourceCurrency(), request.getDestinationCurrency());

            /**
             *
             * Calculate final amount after conversion
             *
             */
            BigDecimal finalAmount = request.getAmount().multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);

            /**
             *
             * Update payment status to COMPLETED and set final amount
             *
             */
            payment.setExchangeRate(exchangeRate);
            payment.setPayOutAmount(finalAmount);
            payment.setMessage("Payment processed successfully.");
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            log.info("Payment processed successfully: {}", payment.getId());
        } catch (FxServiceException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setMessage(e.getMessage());

            log.error("Payment processing failed for payment id {}: {}", payment.getId(), e.getMessage());
        }
        paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    /**
     *
     *
     * Gets payment from the database
     *
     *
     *
     * @param id PaymentID
     * @return PAyment response with all details
     * @throws PaymentNotFoundException if payment not found
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id){
        log.info("Retreiving payment with id: {}", id);

        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    /**
     *
     *  Maps Payment entity to PaymentResponse DTO
     *  Converts internal Payment representation to external response format
     *
     * @param payment Payment entity
     * @return PaymentResponse DTO
     */
    private PaymentResponse mapToResponse(Payment payment){
        return PaymentResponse.builder().id(payment.getId()).sender(payment.getSender()).
                receiver(payment.getReceiver()).amount(payment.getAmount()).
                sourceCurrency(payment.getSourceCurrency()).exchangeRate(payment.getExchangeRate()).
                destinationCurrency(payment.getDestinationCurrency()).
                payoutAmount(payment.getPayOutAmount().setScale(2, RoundingMode.HALF_UP)).
                status(payment.getStatus()).
                message(payment.getMessage()).
                createdAt(payment.getCreatedAt()).
                updatedAt(payment.getProcessedAt()).
                build();
    }
}
