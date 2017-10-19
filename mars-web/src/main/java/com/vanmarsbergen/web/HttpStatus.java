/*
 * Copyright (c) 2016. Fundd.co. You are allowed to re-use this code for free, but please share our product in some way on social media or just share some Fundd love with the people that are near to you.
 */

package com.vanmarsbergen.mars.web;

public class HttpStatus {
  public static final HttpStatus OK = new HttpStatus(200, "OK");
  public static final HttpStatus CREATED = new HttpStatus(201, "Created");
  public static final HttpStatus ACCEPTED = new HttpStatus(202, "Accepted");
  public static final HttpStatus NO_CONTENT = new HttpStatus(204, "No Content");
  public static final HttpStatus FOUND = new HttpStatus(302, "Found");
  public static final HttpStatus NOT_MODIFIED = new HttpStatus(304, "Not Modified");
  public static final HttpStatus MOVED_TEMPORARY = new HttpStatus(307, "Moved temporary");
  public static final HttpStatus BAD_REQUEST = new HttpStatus(400, "Bad Request");
  public static final HttpStatus UNAUTHORIZED = new HttpStatus(401, "Unauthorized");
  public static final HttpStatus PAYMENT_REQUIRED = new HttpStatus(402, "Payment Required");
  public static final HttpStatus FORBIDDEN = new HttpStatus(403, "Forbidden");
  public static final HttpStatus NOT_FOUND = new HttpStatus(404, "Not Found");
  public static final HttpStatus METHOD_NOT_ALLOWED = new HttpStatus(405, "Method Not Allowed");
  public static final HttpStatus NOT_ACCEPTABLE = new HttpStatus(406, "Not Acceptable");
  public static final HttpStatus CONFLICT = new HttpStatus(409, "Conflict");
  public static final HttpStatus GONE = new HttpStatus(410, "Gone");
  public static final HttpStatus UNSUPPORTED_MEDIA_TYPE = new HttpStatus(415, "Unsupported Media Type");
  public static final HttpStatus TEAPOT = new HttpStatus(418, "I'm a teapot");
  public static final HttpStatus AUTHENTICATION_VERIFICATION_FAILED = new HttpStatus(420, "Authentication Verification Failed");
  public static final HttpStatus LOCKED = new HttpStatus(423, "Locked");
  public static final HttpStatus INTERNAL_SERVER_ERROR = new HttpStatus(500, "Internal Server Error");
  public static final HttpStatus NOT_IMPLEMENTED = new HttpStatus(501, "Not Implemented");
  public static final HttpStatus BAD_GATEWAY = new HttpStatus(502, "Bad Gateway");
  public static final HttpStatus SERVICE_UNAVAILABLE = new HttpStatus(503, "Service Unavailable");
  public static final HttpStatus GATEWAY_TIMEOUT = new HttpStatus(504, "Gateway Timeout");

  private final int code;
  private final String statusPhrase;

  private HttpStatus(int code, String statusPhrase) {
    if (code < 0) {
      throw new IllegalArgumentException(
        "code: " + code + " (expected: 0+)");
    }

    if (statusPhrase == null) {
      throw new NullPointerException("statusPhrase");
    }

    for (int i = 0; i < statusPhrase.length(); i++) {
      char c = statusPhrase.charAt(i);
      // Check prohibited characters.
      switch (c) {
        case '\n':
        case '\r':
          throw new IllegalArgumentException(
            "statusPhrase contains one of the following prohibited characters: " +
              "\\r\\n: " + statusPhrase);
      }
    }

    this.code = code;
    this.statusPhrase = statusPhrase;
  }

  /**
   * Returns the code of this {@link HttpStatus}.
   */
  public int code() {
    return code;
  }

  /**
   * Returns the reason phrase of this {@link HttpStatus}.
   */
  public String statusPhrase() {
    return statusPhrase;
  }

  /**
   * Equality of {@link HttpStatus} only depends on {@link #code()}. The
   * reason phrase is not considered for equality.
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof HttpStatus && code() == ((HttpStatus) o).code();

  }

  @Override
  public String toString() {
    return String.valueOf(code) + ' ' + statusPhrase;
  }
}
