package com.example.crosspayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 *  *  This class represents the JSON we expect to receive from the FX Service when querying for types of currencies.
 *  *
 *  *
 *  *   Example for get all Currencies response:
 *  *
 *  * {
 *  *      "currencies":["USD","EUR","GBP","JPY","CAD","AUD","CHF","CNY","INR","MXN"]
 *  * }
 *  */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FXSupportedCurrency {

    /**
     *
     * Returns a list of currencies supported, each represented in ISO 4217 format.
     *
     */
    private List<String> currencies;
}
