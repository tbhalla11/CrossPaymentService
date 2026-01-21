package com.example.crosspayment.exception;

/**
 *  fx service exception:
 *
 *  *  Custom exception to handle errors related to FX service interactions.
 *     Lets the client know that the error has come from the FX Service and not from Payment Service
 *     Better error handling and debugging.
 *
 *     Will be thrown when there are issues such as:
 *      - FX service being unreachable
 *      - Invalid responses from FX service
 *      - Timeouts while waiting for FX service
 *      - Data parsing errors from FX service responses
 *      - FX service response time is > current date time
 *
 */
public class FxServiceException extends RuntimeException {

    /**
     * Constructor for FxServiceException with message.
     *
     * @param message The exception message.
     */
    public FxServiceException(String message) {
        super(message);
    }

    /**
     * Constructor for FxServiceException with message and cause.
     * @param message
     * @param cause
     */
    public FxServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
