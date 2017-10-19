package com.vanmarsbergen.mars.core.api;

public class ApiStatus {
  public static final ApiStatus SUCCESS = new ApiStatus(10, "OK");
  public static final ApiStatus GREAT_SUCCESS = new ApiStatus(11, "Very nice, great success!");
  public static final ApiStatus APRIL_FOOLS = new ApiStatus(14, "1 april, kikker in je bil!");

  public static final ApiStatus FAILED = new ApiStatus(20, "An unknown error occurred");
  public static final ApiStatus FAILED_INSERT = new ApiStatus(21, FAILED);
  public static final ApiStatus FAILED_UPDATE = new ApiStatus(22, FAILED);
  public static final ApiStatus FAILED_FIND = new ApiStatus(23, FAILED);
  public static final ApiStatus FAILED_HANDLER = new ApiStatus(24, "We were unable to fulfill your request");

  public static final ApiStatus HTTP_ERROR_GENERAL = new ApiStatus(30, FAILED);
  public static final ApiStatus HTTP_ERROR_JSON_FORMAT = new ApiStatus(31,
    "There is not a valid JSON object given in the Request Body");
  public static final ApiStatus HTTP_ERROR_CONTENT_TYPE = new ApiStatus(32,
    "The given Content-Type header in your Request is not recognized");
  public static final ApiStatus HTTP_EMPTY_JSON_OBJECT = new ApiStatus(33,
    "The JSON Object given in the Request Body is does not contain any valuable data");
  public static final ApiStatus HTTP_MODEL_NOT_FOUND = new ApiStatus(34, "The data you are looking for is not found");
  public static final ApiStatus HTTP_HEADER_INCORRECT = new ApiStatus(35, "Incorrect header passed");

  public static final ApiStatus HTTP_ERROR_INSERT_FAILED = new ApiStatus(40,
    "An unknown error occurred during inserting the object into the database");
  public static final ApiStatus HTTP_ERROR_INSERT_VALIDATION = new ApiStatus(41,
    "Object exception failed, in the JSON key 'keys' you can find the invalid model keys");
  public static final ApiStatus HTTP_ERROR_INSERT_FIELDS_MISSING = new ApiStatus(42,
    "There are fields missing in your Request Body");
  public static final ApiStatus HTTP_ERROR_INSERT_NO_BODY = new ApiStatus(43,
    "Request body is missing, modification of data is not possible without new input");
  public static final ApiStatus HTTP_ERROR_RELATION_INSERT_FAILED = new ApiStatus(44, FAILED);
  public static final ApiStatus HTTP_ERROR_INSERT_INVALID_VALUE_TYPES = new ApiStatus(45,
    "Request Body contains invalid value types, please check the API Documentation for more information");
  public static final ApiStatus HTTP_ERROR_INSERT_INVALID_DOCUMENT_TYPE = new ApiStatus(46,
    "Request Body contains invalid document type and body does not match the requirements, please check the API " +
      "Documentation for the exact document type " +
      "for this request");
  public static final ApiStatus HTTP_ERROR_INSERT_DUPLICATE = new ApiStatus(47,
    "The object you try to insert does already exist");

  public static final ApiStatus HTTP_ERROR_UPDATE_FAILED = new ApiStatus(50, FAILED);
  public static final ApiStatus HTTP_ERROR_UPDATE_VALIDATION = new ApiStatus(51, HTTP_ERROR_INSERT_VALIDATION);
  public static final ApiStatus HTTP_ERROR_UPDATE_FIELDS_MISSING = new ApiStatus(52, "There are fields missing in " +
    "your Request Body");
  public static final ApiStatus HTTP_ERROR_UPDATE_NO_BODY = new ApiStatus(53, HTTP_ERROR_INSERT_NO_BODY);
  public static final ApiStatus HTTP_ERROR_UPDATE_INVALID_DOCUMENT_TYPE = new ApiStatus(56,
    HTTP_ERROR_INSERT_INVALID_DOCUMENT_TYPE);

  public static final ApiStatus HTTP_ERROR_DELETE_FAILED = new ApiStatus(60,
    "Due to unknown reason the model couldn't be deleted");

  public static final ApiStatus STATUS_DB_FAILURE = new ApiStatus(70, "There is an unknown issue with the data source");
  public static final ApiStatus STATUS_DB_QUERY_FAILURE = new ApiStatus(71, STATUS_DB_FAILURE);
  public static final ApiStatus STATUS_DB_CONNECTION_FAILED = new ApiStatus(72,
    "The connection with the data source failed");
  public static final ApiStatus STATUS_DB_NO_RECORDS = new ApiStatus(73, "There were no records found in the data " +
    "source");
  public static final ApiStatus STATUS_DB_AUTO_COMMIT_FAILED = new ApiStatus(74, STATUS_DB_CONNECTION_FAILED);
  public static final ApiStatus STATUS_DB_DUPLICATE_RECORD = new ApiStatus(75, "The record you try to insert is already stored");

  public static final ApiStatus AUTH_USER_PASSWORD_WRONG = new ApiStatus(80,
    "The combination of the username and password is incorrect");
  public static final ApiStatus AUTH_OTP_REQUIRED = new ApiStatus(81,
    "You are authorized but haven't verified with OTP. Please verify your account before continue");
  public static final ApiStatus AUTH_OTP_INVALID = new ApiStatus(82,
    "The OTP code you tried to verify your account with is invalid");
  public static final ApiStatus AUTH_OTP_ALREADY_SETUP = new ApiStatus(83,
    "You have already setup OTP for this account");
  public static final ApiStatus AUTH_OTP_NOT_SETUP = new ApiStatus(84,
    "You are required to setup OTP before you continue");
  public static final ApiStatus AUTH_USER_NOT_FOUND = new ApiStatus(85,
    "The user you try to authenticate is not found");
  public static final ApiStatus AUTH_OTP_ALREADY_VALIDATED = new ApiStatus(86, AUTH_OTP_INVALID);

  public static final ApiStatus MODEL_GENERAL = new ApiStatus(90, "There is an issue with the model");
  public static final ApiStatus MODEL_RELATION_MISSING = new ApiStatus(91,
    "The relation with another model is missing");
  public static final ApiStatus MODEL_UNKNOWN_OWNER = new ApiStatus(92,
    "It's unknown who the rightful owner is of the requested model");

  public static final ApiStatus HTTP_ERROR_DATE_RANGE = new ApiStatus(100,
    "The selected date range is not possible");

  public static final ApiStatus REDIS_JWTTOKEN_GENERAL = new ApiStatus(110,
    "There is an unknown issue regarding your JWTToken");
  public static final ApiStatus REDIS_JWTTOKEN_NOT_FOUND = new ApiStatus(111, "Your JWTToken is not found");
  public static final ApiStatus REDIS_JWTTOKEN_INSERT_FAILED = new ApiStatus(112, "Your JWTToken could not be stored");
  public static final ApiStatus REDIS_JWTTOKEN_EXPIRE_FAILED = new ApiStatus(113, "The JWTToken expire time could not" +
    " be set");
  public static final ApiStatus REDIS_JWTTOKEN_ALREADY_UPDATED = new ApiStatus(114, "The JWTToken is already updated");

  public static final ApiStatus HTTP_FIND_FIELDS_MISSING = new ApiStatus(120, "Fields needed to find the correct object are missing in the request");

  private final int errorStatus;
  private final String errorMessage;

  public ApiStatus(int errorStatus, String errorMessage) {
    this.errorStatus = errorStatus;
    this.errorMessage = errorMessage;
  }

  /**
   * Create an {@link ApiStatus} object with unique number and copied Error Message
   *
   * @param errorStatus Unique Error status number
   * @param extendFrom  Pass on an {@link ApiStatus} object to use its error message
   */
  public ApiStatus(int errorStatus, ApiStatus extendFrom) {
    this.errorStatus = errorStatus;
    this.errorMessage = extendFrom.toString();
  }

  public ApiStatus(int errorStatus) {
    this.errorStatus = errorStatus;
    this.errorMessage = null;
  }

  public int statusCode() {
    return this.errorStatus;
  }

  @Override
  public String toString() {
    return errorMessage;
  }

}
