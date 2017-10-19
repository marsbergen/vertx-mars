package com.vanmarsbergen.mars.data.exception;

public class ValidationException extends Exception {
  public ValidationException() {
      super();
  }

  public ValidationException(String message) {
      super(message);
  }
}
