package com.example.crosspayment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 *  This class represents the JSON we expect to receive from the FX Service when querying for exchange rates.
 *
 *  Example: For exchange rate request from USD to EUR
 *  {
 *      "exchange_rate":0.916487620119132,
 *      "expiry_time":"2026-01-20T20:18:42Z"
 *  }
 *
 * Example for get all Currencies response:
 *
 * {
 *      "currencies":["USD","EUR","GBP","JPY","CAD","AUD","CHF","CNY","INR","MXN"]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FXRateResponse {

    /**
     * exchange rate
     * The FX rate from source to target currency
     */
    @JsonProperty("exchange_rate")
    private BigDecimal exchangeRate;

    /**
     * expiry time
     * The time when this FX rate quote expires
     */
    @JsonProperty("expiry_time")
    private String expiryTime;

}
