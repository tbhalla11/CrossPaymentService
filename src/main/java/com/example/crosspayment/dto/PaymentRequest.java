package com.example.crosspayment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * payment request dto:
 * This class will represent the payment request data transfer object - or the JSON data our API will receive when a payment is initiated.
 *
 * example request:
 * Post api/payments
 * {
 *     "sender" : "Bob Doe",
 *     "receiver": "John Wick",
 *     "amount": "400.00",
 *     "source currency": "USD",
 *     "destination currency": "EUR"
 * }
 *
 *
 */



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /**
     * sender
     * Value cannot be null or empty
     * Error if validation fails
     *
     */
    @NotBlank(message = "Sender cannot be blank")
    private String sender;

    /**
     * receiver
     *Value cannot be null or empty
     * Error if validation fails
     *
     */
    @NotBlank(message = "Receiver cannot be blank")
    private String receiver;

    /**
     * amount
     * Value cannot be null or less than or equal to zero
     * Error if validation fails
     * BigDecimal to handle currency values accurately
     * Note: A min value of 0.01 is set to ensure the amount is greater than zero but has potential to cause issues
     * when converting to currencies with higher FX rates. Adjust as necessary based on business requirements.
     */
    @NotNull(message = "Amount cannot be null or less than or equal to zero")
    @DecimalMin(value = "0.01", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    /**
     * source currency
     * Value cannot be null or empty
     * Regex to validate 3-letter ISO currency codes (e.g., USD, EUR, GBP)
     */
    @NotBlank(message = "Source currency cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Source currency must be a valid 3-letter ISO currency code")
    private String sourceCurrency;

    /**
     * destination currency
     * Value cannot be null or empty
     * Regex to validate 3-letter ISO currency codes (e.g., USD, EUR, GBP)
     */
    @NotBlank(message = "Destination currency cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Destination currency must be a valid 3-letter ISO currency code")
    private String destinationCurrency;
}
