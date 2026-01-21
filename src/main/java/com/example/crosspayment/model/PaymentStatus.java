package com.example.crosspayment.model;

/**
 *
 * This class will create 4 ENUM status based the states a payment can potentially be in
 *
 * ENUM values: PENDING, COMPLETED, FAILED, CANCELLED
 * Enums will give us more control of the statuses and avoid potential errors with string literals & Clean db values
 *
 * Flow:
 * Payment created -> PENDING
 * Payment processed successfully & FX Service responds -> COMPLETED
 * Payment processing failed due to FX Service failure-> FAILED
 * Payment cancelled by user before processing -> CANCELLED - NOTE: This status won't interact with FX Service - future scope
 *
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED
}
