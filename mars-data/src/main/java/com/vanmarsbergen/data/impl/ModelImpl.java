package com.vanmarsbergen.mars.data.impl;

import com.vanmarsbergen.mars.data.Model;
import com.vanmarsbergen.mars.data.ModelOptions;
import com.vanmarsbergen.mars.data.exception.ValidationException;
import com.vanmarsbergen.mars.data.mysql.MySQL;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.Field;
import org.jooq.Query;

import java.util.*;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public abstract class ModelImpl implements Model {

  protected String primaryKey = "id";
  protected List<String> ignoredColumnsOnInsert = new LinkedList<>();
  protected JsonObject columns = new JsonObject();
  private boolean insertPrimaryKeyInDB = false;
  private List<String> changedColumns = new LinkedList<>();
  private HashMap<String, Model> relationsToOne = new HashMap<>();
  private HashMap<String, List> relationsToMany = new HashMap<>();
  private boolean empty = true;

  public ModelImpl() {
  }

  public ModelImpl(JsonObject data) {
    set(data);
  }

  protected void setColumns(JsonObject columns) {
    this.columns = columns;
  }

  protected void addRelation(Model model) {
    if (model != null && !isRelationAdded(model.getObjectType())) {
      this.relationsToOne.put(model.getObjectType(), model);
    }
  }

  protected void addRelation(String objectType, List models) {
    if (!isRelationAdded(objectType)) {
      this.relationsToMany.put(objectType, models);
    }
  }

  private boolean isRelationAdded(String objectType) {
    return relationsToOne.containsKey(objectType) || relationsToMany.containsKey(objectType);
  }

  @Override
  public abstract String getTableName();

  @Override
  public abstract String getObjectType();

  @Override
  public abstract Set<ValidationImpl> rules();

  @Override
  public JsonObject toApi() {
    return toApi(ModelOptions.create());
  }

  @Override
  public JsonObject toApi(ModelOptions modelOptions) {
    JsonObject apiResponse = new JsonObject();

    if (modelOptions.isRelationEnabled()) {
      JsonObject relations = new JsonObject();
      if (relationsToOne.size() > 0) {
        relationsToOne.entrySet().forEach(entry -> {
          if (modelOptions.isDeepRelationEnabled()) {
            relations.put(entry.getKey(), entry.getValue().toApi(ModelOptions.create().enableRelations()));
          } else {
            relations.put(entry.getKey(), entry.getValue().toApi());
          }
        });
      }

      if (relationsToMany.size() > 0) {
        relationsToMany.entrySet().forEach(entry -> {
          JsonArray models = new JsonArray();
          for (Object o : entry.getValue()) {
            if (modelOptions.isDeepRelationEnabled()) {
              models.add(((Model) o).toApi(ModelOptions.create().enableRelations()));
            } else {
              models.add(((Model) o).toApi());
            }
          }
          relations.put(entry.getKey(), models);
        });
      }

      apiResponse
        .put("relations", relations);
    }

    apiResponse.put(primaryKey, columns.getInteger(primaryKey, null));
    JsonObject columnsToApi = columns;
    columnsToApi.remove(primaryKey);

    return apiResponse.put("attributes", columnsToApi);
  }

  public boolean isEmpty() {
    return empty;
  }

  @Override
  public Model set(JsonObject data) {
    if (data == null || data.isEmpty()) {
      return this;
    }

    this.empty = false;

    data.forEach(entry -> {
      if (columns.containsKey(entry.getKey())) {
        changedColumns.add(entry.getKey());
        columns.put(entry.getKey(), entry.getValue());
      }
    });

    return this;
  }

  @Override
  public Model put(String key, Object value) {
    if (columns.getValue(key) == value) {
      return this;
    }

    changedColumns.add(key);
    if (value != null) {
      columns.put(key, value);
    } else {
      columns.putNull(key);
    }
    this.empty = false;

    return this;
  }

  @Override
  public Map<Field<?>, Object> toDatabase() {
    return toDatabase(false);
  }

  public Map<Field<?>, Object> toDatabase(boolean patchMode) {
    Map<Field<?>, Object> map = new HashMap<>();

    columns.forEach(entry -> {
      if (
        (!(Objects.equals(entry.getKey(), primaryKey) && !insertPrimaryKeyInDB)) &&
          (ignoredColumnsOnInsert.indexOf(entry.getKey()) == -1) &&
          (!patchMode || changedColumns.indexOf(entry.getKey()) > -1)
        ) {
        map.put(field(entry.getKey()), entry.getValue());
      }
    });

    return map;
  }

  @Override
  public Object getValue(String key) {
    if (!columns.containsKey(key)) {
      return null;
    }

    return columns.getValue(key);
  }

  @Override
  public String getString(String key) {
    if (!columns.containsKey(key)) {
      return null;
    }

    return columns.getString(key);
  }

  @Override
  public Integer getInteger(String key) {
    if (!columns.containsKey(key)) {
      return null;
    }

    return columns.getInteger(key);
  }

  @Override
  public JsonObject getAll() {
    return columns;
  }

  @Override
  public Query getSelectQuery() {
    if (columns.getInteger(primaryKey) != null && columns.getInteger(primaryKey) != 0) {
      return MySQL.JooqQuery
        .select(field("*"))
        .from(table(getTableName()))
        .where(field(primaryKey).equal(columns.getInteger(primaryKey)));
    }

    return MySQL.JooqQuery
      .select(field("*"))
      .from(table(getTableName()));
  }

  @Override
  public Query getInsertQuery() {
    return MySQL.JooqQuery
      .insertInto(table(getTableName()))
      .set(toDatabase());
  }

  public Query getUpdateQuery() {
    return MySQL.JooqQuery
      .update(table(getTableName()))
      .set(toDatabase(true))
      .where(field(this.primaryKey).equal(getInteger(this.primaryKey)));
  }

  public Query getPatchUpdateQuery() {
    return MySQL.JooqQuery
      .update(table(getTableName()))
      .set(toDatabase(true))
      .where(field(this.primaryKey).equal(getInteger(this.primaryKey)));
  }

  public List<Query> getRelationInsertQueries() {
    List<Query> querySet = new LinkedList<>();
    for (Map.Entry<String, List> relation : relationsToMany.entrySet()) {
      if (relation.getValue() != null && !relation.getValue().isEmpty()) {
        relation.getValue().forEach(item -> {
          querySet.add(((Model) item).getInsertQuery());
        });
      }
    }

    for (Map.Entry<String, Model> relation : relationsToOne.entrySet()) {
      if (relation.getValue() != null) {
        querySet.add(relation.getValue().getInsertQuery());
      }
    }

    return querySet;
  }

  public void validate() throws ValidationException {
    for (ValidationImpl validation : rules()) {
      validation.validate(getValue(validation.getColumnName()));
    }
  }

  public void validateChanged() throws ValidationException {
    List<String> columnsToValidate = changedColumns;
    columnsToValidate.remove(columnsToValidate.indexOf(primaryKey));

    for (ValidationImpl validation : rules()) {
      if (columnsToValidate.contains(validation.getColumnName())) {
        validation.validate(getValue(validation.getColumnName()));
      }
    }
  }
}
