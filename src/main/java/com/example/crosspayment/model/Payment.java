package com.example.crosspayment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    /**
     *
     * The primary key for the Payment entity, auto-generated.
     * @Id indicates that this field is the primary key.
     * The PK will also serve as a reference for tracking payment transactions.
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *
     * Sender - the individual or entity initiating the payment.
     */
    @Column (nullable = false)
    private String sender;

    /**
     *
     * Receiver - the individual or entity receiving the payment.
     */
    @Column(nullable = false)
    private String receiver;

    /**
     *
     * The currency from which the payment is being made.
     * This field is mandatory and cannot be null.
     *
     */
    @Column(nullable = false, length = 3)
    private String sourceCurrency;

    /**
     *
     * The currency to which the payment is being converted.
     * This field is mandatory and cannot be null.
     *
     */
    @Column(nullable = false, length = 3)
    private String destinationCurrency;

    /**
     *
     * The amount of money being transferred in the payment.
     * This field is mandatory and cannot be null.
     *
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     *
     * The exchange rate applied for the currency conversion.
     * This field is NULLABLE because when if FX Service fails to respond, the exchangeRate remains NULL.
     *
     */
    @Column(precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    /**
     *
     * The amount to be paid out after currency conversion.
     * This field is NULLABLE because when a payment is first created, the payOutAmount is NULL, and if
     * CANCELLED before processing, it remains NULL or if FAILED during processing.
     * Calculated as Exchange Rate * amount
     */
    @Column(precision = 19, scale = 2)
    private BigDecimal payOutAmount;

    /**
     *
     * The current status of the payment.
     * This field uses the PaymentStatus enum to ensure valid status values.
     * The status is stored as a string in the database for readability.
     *
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     *
     * A message providing additional information about the payment status.
     *
     */
    @Column(length = 500)
    private String message;

    /**
     *
     * The date when the payment was created or processed.
     * This field is stored as a LocalDateTime.
     *
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *
     * This updates the last time the payment was updated
     * Will give when the payment failed or was processed
     *
     *
     */
    @Column (nullable = false)
    private LocalDateTime processedAt;

    /**
     *
     * Lifecycle callback to set the createdAt and processedAt before persisting.
     *
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.processedAt = LocalDateTime.now();
    }

    /**
     *
     * Lifecycle callback to update the processedAt before updating.
     *
     */
    @PreUpdate
    protected void onUpdate() {
        this.processedAt = LocalDateTime.now();
    }
}
