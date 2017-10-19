/*
 * Copyright (c) 2016. Fundd.co. You are allowed to re-use this code for free, but please share our product in some way on social media or just share some Fundd love with the people that are near to you.
 */

package com.vanmarsbergen.mars.data;

import com.vanmarsbergen.mars.data.impl.ValidationImpl;
import io.vertx.core.json.JsonObject;
import org.jooq.Field;
import org.jooq.Query;

import java.util.Set;

/**
 * Created by patrick on 17/3/16.
 *
 * Conventions:
 * Private Key-value data in Models aka columns (as in database) are always written in snake_case
 * Methods and relation-keys are always written in camelCase
 * Classes and Model-Object-Types are always written in PascalCase
 */
public interface Model {

  /**
   * Returns the table name bind to the Model
   *
   * @return String with Table name
   */
  String getTableName();

  /**
   * Returns the object type used in the API result
   *
   * @return String with Object type name
   */
  String getObjectType();

  /**
   * Returns a map with exception rules
   *
   * @return HashMap with Key value pair of column name and an array of ValidationImpl rules
   */
  Set<ValidationImpl> rules();

  /**
   * Store a batch of data in the Model
   *
   * @param data JsonObject with a key-value representation of the Model
   */
  Model set(JsonObject data);

  /**
   * Set a value on a specific column.
   * @param key
   * @param value
   * @return
   */
  Model put(String key, Object value);

  /**
   * Get a value for a specific column.
   * @param key
   * @return
   */
  Object getValue(String key);

  /**
   * Get the string value for a specific column.
   * @param key
   * @return
   */
  String getString(String key);

  /**
   * Get the integer value for a specific column.
   * @param key
   * @return
   */
  Integer getInteger(String key);

  /**
   * Get the whole raw data set.
   * @return
   */
  JsonObject getAll();

  /**
   * Returns a JsonObject which is a simple extraction of the Model's data with an API-ready structure.
   *
   * @return JsonObject with data from the Model
   */
  JsonObject toApi();

  /**
   * Returns a JsonObject which is a simple extraction of the Model's data with an API-ready structure.
   *
   * @return JsonObject with data from the Model
   */
  JsonObject toApi(ModelOptions modelOptions);

  /**
   * Returns a map that is ready for JOOQ's set() method when you want to execute an inserting/updating query into
   * the database.
   * It will take all the columns/keys from the model and make them ready for JOOQ's {@link Field}
   *
   * @return A map with the (JOOQ) database field and a value which can be anything like String, Int, etc.
   */
  Object toDatabase();

  boolean isEmpty();

  /**
   * Returns a Query object with a basic SELECT * query based on the Model's tablename and ID. If the ID is null it
   * return a SELECT * query
   *
   * @return JOOQ Query Object
   */
  Query getSelectQuery();

  Query getInsertQuery();
}
