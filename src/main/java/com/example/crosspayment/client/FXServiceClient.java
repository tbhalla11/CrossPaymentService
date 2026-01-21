package com.example.crosspayment.client;


import com.example.crosspayment.dto.FXRateQuote;
import com.example.crosspayment.dto.FXRateResponse;
import com.example.crosspayment.dto.FXSupportedCurrency;
import com.example.crosspayment.dto.PaymentRequest;
import com.example.crosspayment.exception.FxServiceException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class FXServiceClient {

    private final RestTemplate restTemplate;

    private final String fxServiceUrl;

    public FXServiceClient(RestTemplate restTemplate,
                           @Value("${fx.service.url}") String fxServiceUrl) {
        this.restTemplate = restTemplate;
        this.fxServiceUrl = fxServiceUrl;
    }

    @Retry(name = "fxService", fallbackMethod = "getDefaultExchangeRate")
    @CircuitBreaker(name = "fxService", fallbackMethod = "getDefaultExchangeRate")
    public BigDecimal getExchangeRate(String sourceCountry, String destinationCounter){
        log.info("Calling FX service for exchange rate from {} to {}", sourceCountry, destinationCounter);

        try{
            String url = fxServiceUrl + "/twirp/payments.v1.FXService/GetQuote";
            FXRateQuote request = FXRateQuote.builder()
                    .sourceCountry(sourceCountry)
                    .targetCountry(destinationCounter)
                    .build();

            ResponseEntity<FXRateResponse> response = restTemplate.postForEntity(url, request, FXRateResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null){
                FXRateResponse fxRateResponse = response.getBody();
                log.info("Received exchange rate: {} with expiry time: {}", fxRateResponse.getExchangeRate(), fxRateResponse.getExpiryTime());

                if (fxRateResponse.getExchangeRate() == null || fxRateResponse.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0){
                    log.error("Invalid exchange rate received: {}", fxRateResponse.getExchangeRate());
                    throw new FxServiceException("Invalid exchange rate received from FX service");
                }

                if (fxRateResponse.getExpiryTime() != null){
                    String convertedExpiryTime = fxRateResponse.getExpiryTime();
                    Instant expiryInstant = Instant.parse(convertedExpiryTime);
                    Instant currentTime = Instant.now();
                    if (currentTime.isAfter(expiryInstant)){
                        log.error("Received expired exchange rate with expiry time: {}", convertedExpiryTime);
                        throw new FxServiceException("Received expired exchange rate from FX service");
                    }
                }
                log.info("Successfully retrieved exchange rate from FX service",
                    fxRateResponse.getExchangeRate(),
                    fxRateResponse.getExpiryTime());


                return fxRateResponse.getExchangeRate();
            }else{
                log.error("Invalid response from FX service: {}", response.getStatusCode(), response.getBody());
                throw new FxServiceException("Invalid response from FX service");
            }
        }catch(HttpClientErrorException e){
            log.error("HTTP error while calling FX service: {}", e.getStatusCode(), e);
            throw new FxServiceException("HTTP error while calling FX service: " + e.getStatusCode(), e);
        }catch (ResourceAccessException e){
            log.error("Resource access error while calling FX service: {}", e.getMessage(), e);
            throw new FxServiceException("Resource access error while calling FX service: " + e.getMessage(), e);
        }
    }



    public BigDecimal getDefaultExchangeRate(String sourceCountry, String destinationCounter, Exception t){
        log.error("FX service is unavailable.", sourceCountry, destinationCounter, t);

        throw new FxServiceException(
            "FX service is unavailable after multiple attempts. Cannot retrieve exchange rate from " + sourceCountry + " to " + destinationCounter, t
        );
    }

    /**
     *
     *
     * Methods for getting all supported below.
     *
     *
     */

    @Retry(name = "fxService", fallbackMethod = "getDefaultSupportedCurrencies")
    @CircuitBreaker(name = "fxService", fallbackMethod = "getDefaultSupportedCurrencies")
    public List<String> getSupportedCurrency(){
        log.info("Calling FX service for supported currencies");

        try{
            String url = fxServiceUrl + "/twirp/payments.v1.FXService/GetSupportedCurrencies";
            Map<String, Object> body = new HashMap<>();

            ResponseEntity<FXSupportedCurrency> response = restTemplate.postForEntity(url, body, FXSupportedCurrency.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null){
                List<String> supportedCurrenciesResponse = response.getBody().getCurrencies();
                log.info("Received supported currencies: {}", supportedCurrenciesResponse);

                return supportedCurrenciesResponse;
            }else{
                log.error("Invalid response from FX service: {}", response.getStatusCode());
                throw new FxServiceException("Invalid response from FX service");
            }
        }catch(HttpClientErrorException e){
            log.error("HTTP error while calling FX service: {}", e.getStatusCode(), e);
            throw new FxServiceException("HTTP error while calling FX service: " + e.getStatusCode(), e);
        }catch (ResourceAccessException e){
            log.error("Resource access error while calling FX service: {}", e.getMessage(), e);
            throw new FxServiceException("Resource access error while calling FX service: " + e.getMessage(), e);
        }
    }

    public List<String> getDefaultSupportedCurrencies(Exception t){
        log.error("FX service is unavailable.", t);

        return Collections.emptyList();
    }

    /**
     *
     * Future methods for FX service can be added here.
     * Is Currency Supported should be added to validate currencies.
     */

    public boolean isCurrencySupported(String currencyCode) {
        try {
            List<String> supportedCurrencies = getSupportedCurrency();
            return supportedCurrencies.contains(currencyCode);
        }catch (FxServiceException e){
            log.error("Failed to check if currency is supported: {}", currencyCode, e);
            return false;
        }
    }
}
