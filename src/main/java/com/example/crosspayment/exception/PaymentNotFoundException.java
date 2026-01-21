package com.example.crosspayment.exception;


/**
 *
 *  Payment not found exception:
 *      - Thrown when client requests a payment that does not exist in the system.
 *      - Helps in identifying invalid payment IDs and improves error handling.
 *      - Provides clear feedback to the client about the non-existence of the requested payment.
 *
 *      Example: Client requests payment status for transaction ID 99999, GET/payments/1234
 *      - If transaction ID 99999 does not exist, this exception is thrown.
 */
public class PaymentNotFoundException extends RuntimeException{

    /**
     * Constructor for PaymentNotFoundException with message.
     *
     * @param message The exception message.
     */
    public PaymentNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for PaymentNotFoundException with paymentId.
     * @param paymentId
     */
    public PaymentNotFoundException(Long paymentId){
        super("Payment with ID " + paymentId + " not found.");}
}
