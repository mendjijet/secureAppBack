package com.jet.com.secureappback.exception;

public class ApiException extends RuntimeException {
  public ApiException(String message) {
    super(message);
  }
}
