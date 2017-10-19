package com.vanmarsbergen.mars.data;

import com.vanmarsbergen.mars.data.exception.ValidationException;
import com.vanmarsbergen.mars.data.impl.ValidationImpl;

public interface Validation {
  String TYPE_BOOLEAN = "Boolean";
  String TYPE_STRING = "String";
  String TYPE_NUMBER = "Number";
  String TYPE_ENUM = "Enum";

  /**
   *
   * @param column The column you want to validate
   * @return Validation
   */
  static Validation create(String column) {
    return new ValidationImpl(column);
  }

  /**
   * Make the column as required
   *
   * @return Validation
   */
  Validation required();

  /**
   *
   * @param minLength
   * @return Validation
   */
  Validation minLength(int minLength);

  Validation maxLength(int maxLength);

  Validation isBoolean();

  Validation isString();

  Validation isNumber();

  Validation isEnum(Object... options);

  Validation defaultValue(Object value);

  void validate(Object data) throws ValidationException;

  String getColumnName();

  Validation email();
}
