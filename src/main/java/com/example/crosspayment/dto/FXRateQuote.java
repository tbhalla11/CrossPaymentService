package com.example.crosspayment.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

/**
 * *  This class represents the JSON we send to the FX Service when requesting a FX rate quote.
 * *  Example:
 * *  {
 * *      "source_country":"USD",
 * *      "target_country":"EUR"
 * *  }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FXRateQuote {

    /**
     * source country
     * Always in ISO 4217 format
     * Always 3 uppercase letters
     */
    @JsonProperty("source_currency")
    private String sourceCountry;

    /**
     * target country
     * Always in ISO 4217 format
     * Always 3 uppercase letters
     */
    @JsonProperty("target_currency")
    private String targetCountry;

}
