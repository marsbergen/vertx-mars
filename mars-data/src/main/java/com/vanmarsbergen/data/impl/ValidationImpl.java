package com.vanmarsbergen.mars.data.impl;

import com.vanmarsbergen.mars.data.Validation;
import com.vanmarsbergen.mars.data.exception.ValidationException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.vanmarsbergen.mars.data.Validation.TYPE_STRING;

public class ValidationImpl implements Validation {
  private String columnName;
  private boolean required = false;
  private int minLength = 0;
  private int maxLength = 0;
  private String type = TYPE_STRING;
  private Object[] enumOptions;
  private Object defaultValue;
  private boolean isEmail = false;

  public ValidationImpl(String columnName) {
    this.columnName = columnName;
  }

  private void validateRequired(String data) throws ValidationException {
    if (required && (data == null || data.isEmpty())) {
      throw new ValidationException(String.format("Column %s is required and may not be empty", columnName));
    }
  }

  private void validateRequired(Object data) throws ValidationException {
    if (required && data == null) {
      throw new ValidationException(String.format("Column %s is required and may not be empty", columnName));
    }
  }

  private void validateEmail(Object data) throws ValidationException {
    if (isEmail && data != null) {
      try {
        InternetAddress emailAddress = new InternetAddress((String) data);
        emailAddress.validate();
      } catch (AddressException e) {
        throw new ValidationException(String.format("Column %s should be an email address and it's not", columnName));
      }
    }
  }

  private void validateStringLength(String data) throws ValidationException {
    if (data == null || data.isEmpty()) {
      return;
    }

    if (minLength > 0 && data.length() < minLength) {
      throw new ValidationException(String.format("Column %s has a minimum length of %d", columnName, minLength));
    }

    if (maxLength > 0 && data.length() > maxLength) {
      throw new ValidationException(String.format("Column %s has a maximum length of %d", columnName, maxLength));
    }
  }

  private void validateType(Object data) throws ValidationException {
    if (data == null) {
      return;
    }

    if (Objects.equals(type, TYPE_STRING) && !data.getClass().getSimpleName().equals(TYPE_STRING) || Objects.equals(type, TYPE_BOOLEAN) && !data.getClass().getSimpleName().equals(TYPE_BOOLEAN)) {
      throw new ValidationException(String.format("Column %s should be a %s, instead it is: %s", columnName, type, data.getClass().getSimpleName()));
    } else if (Objects.equals(type, TYPE_NUMBER)) {
      try {
        Integer dataNumber = Integer.parseInt(data.toString());
      } catch (NumberFormatException exception) {
        throw new ValidationException(String.format("Column %s should be a Number, instead it is: %s", columnName, data.getClass().getSimpleName()));
      }
    } else if (Objects.equals(type, TYPE_ENUM)) {
      List enumOptionsList = Arrays.asList(enumOptions);
      if (!enumOptionsList.contains(data)) {

        StringBuilder enumOptionsListString = new StringBuilder();

        for (Object n : enumOptions) {
          enumOptionsListString.append(n).append(", ");
        }
        enumOptionsListString.deleteCharAt(enumOptionsListString.length() - 2);

        throw new ValidationException(
          String.format(
            "Column %s is not allowed, only the following options are allowed: %s",
            columnName,
            enumOptionsListString.toString().trim()
          )
        );
      }
    }
  }

  public ValidationImpl required() {
    this.required = true;
    return this;
  }

  public ValidationImpl minLength(int minLength) {
    this.minLength = minLength;
    return this;
  }

  public ValidationImpl maxLength(int maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public ValidationImpl isBoolean() {
    this.type = TYPE_BOOLEAN;
    return this;
  }

  public ValidationImpl isString() {
    this.type = TYPE_STRING;
    return this;
  }

  public ValidationImpl isNumber() {
    this.type = TYPE_NUMBER;
    return this;
  }

  public ValidationImpl isEnum(Object... options) {
    this.type = TYPE_ENUM;
    this.enumOptions = options;

    return this;
  }

  public ValidationImpl defaultValue(Object value) {
    this.defaultValue = value;

    return this;
  }

  public void validate(Object data) throws ValidationException {
    Object value = this.defaultValue;
    if (data != null) {
      value = data;
    }

    validateRequired(value);
    if (Objects.equals(type, TYPE_STRING) && value != null) {
      validateStringLength(value.toString());
    }
    validateType(value);
    validateEmail(value);
  }

  public String getColumnName() {
    return columnName;
  }

  public ValidationImpl email() {
    this.type = TYPE_STRING;
    this.isEmail = true;

    return this;
  }
}
