package com.example.crosspayment.dto;


import com.example.crosspayment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *   payment response: This dto represents the response our client will receive after initiating a payment.   *  It contains details about the transaction, including status and any relevant messages.   example SUCCESS response:   *  {       "transactionId": 123456,       "sender": "Bob Doe",       "receiver": "John Wick",       "amount": 400.00,       "sourceCurrency": "USD",       "destinationCurrency": "EUR",       "exchangeRate": 0.85,       "convertedAmount": 340.00,       "status": "SUCCESS",       "message": "Payment processed successfully",       "createdAt": "2023-10-01T12:00:00",       "processedAt": "2023-10-01T12:00:05"        }       Failed Response will contain NULL for fields like exchangeRate, convertedAmount, processedAt      and STATUS will be "FAILED" with an appropriate error message.      These fields ensure client has visibility into the transaction lifecycle and any issues encountered.
 *
 */ /**
 *  payment response: This dto represents the response our client will receive after initiating a payment.
 *  *  It contains details about the transaction, including status and any relevant messages.
 *
 *  example SUCCESS response:
 *  * {
 *      "transactionId": 123456,
 *      "sender": "Bob Doe",
 *      "receiver": "John Wick",
 *      "amount": 400.00,
 *      "sourceCurrency": "USD",
 *      "destinationCurrency": "EUR",
 *      "exchangeRate": 0.85,
 *      "convertedAmount": 340.00,
 *      "status": "SUCCESS",
 *      "message": "Payment processed successfully",
 *      "createdAt": "2023-10-01T12:00:00",
 *      "processedAt": "2023-10-01T12:00:05"
 *      }
 *
 *
 *      Failed Response will contain NULL for fields like exchangeRate, convertedAmount, processedAt
 *     and STATUS will be "FAILED" with an appropriate error message.
 *
 *     These fields ensure client has visibility into the transaction lifecycle and any issues encountered.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    /**
     * transaction id
     * This can be used to track the payment and get its status later.
     *  Unique ID for each payment transaction
     */
    private Long id;

    /**
     * sender
     * Where the payment is coming from
     */
    private String sender;

    /**
     * receiver
     * Where the payment is going to
     *
     */
    private String receiver;

    /**
     * amount
     * the amount to be transferred
     */
    private BigDecimal amount;

    /**
     * source currency
     * The original currency of the amount being sent
     *
     */
    private String sourceCurrency;

    /**
     * destination currency
     * The currency to which the amount is being converted
     *
     */
    private String destinationCurrency;

    /**
     * exchange rate
     * The rate received from the FX Service
     * This will be NULL for:
     * - failed payments
     * - pending payments
     *
     *  For SUCESSFUL payments it will look like:
     *  "exchangeRate": 0.8500
     *
     */
    private BigDecimal exchangeRate;

    /**
     * converted amount - The amount after conversion to the destination currency
     * Calculation = amount * exchange rate
     * Ex. for SUCCESSFUL payment: 100 USD to EUR at 0.85 rate = 85.00 EUR
     *
     * This will be NULL for:
     * - failed payments
     * - pending payments
     *
     */
    private BigDecimal payoutAmount;

    /**
     * status - The current status of the payment
     * Can be: PENDING, SUCCESS, FAILED
     * Tells the client if payment was processed or not.
     *
     */
    private PaymentStatus status;

    /**
     * message - Additional information about the payment status
     * Null for SUCCESS payments
     * For FAILED payments it will contain the error reason
     *
     * Example:
     * "Unable to reach the FX Service, please try again later."
     * "Invalid currency code provided."
     * "Amount must be at least x.xx in source currency."
     *
     */
    private String message;

    /**
     * created at - The timestamp when the payment was created
     * Useful for:
     *  - tracking payment age
     *  - auditing purposes
     *  - debugging issues
     *
     */
    private LocalDateTime createdAt;

    /**
     * Updated at - The timestamp when the payment was processed
     *
     * This changes when:
     * - payment moves from PENDING to SUCCESS or FAILED
     * - Exchange rate is obtained and conversion is done
     *
     *
     */
    private LocalDateTime updatedAt;
}
