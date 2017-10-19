/*
 * Copyright (c) 2015. Fundd.co. You are allowed to re-use this code for free, but please share our product in some way on social media or just share some Fundd love with the people that are near to you.
 */

package com.vanmarsbergen.mars.data.mysql;

import com.vanmarsbergen.mars.core.api.ApiStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.jooq.Query;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class MySQLStorageVerticle extends AbstractVerticle {
  private JDBCClient client;

  private AsyncResult<UpdateResult> lastResult;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    this.client = JDBCClient.createShared(vertx, config(), config().getString("data_source_name", "MySQLDataSource"));

    vertx.eventBus().consumer("db.mysql.query.find", this::queryFind);
    vertx.eventBus().consumer("db.mysql.query.findOne", this::queryFindOne);
    vertx.eventBus().consumer("db.mysql.query.update", this::queryUpdate);
    vertx.eventBus().consumer("db.mysql.query.update.transactional", this::queryUpdateTransactional);
    vertx.eventBus().consumer("db.mysql.query.batch", this::queryBatch);
    vertx.eventBus().consumer("db.mysql.findByPk", this::findByPk);
    vertx.eventBus().consumer("db.mysql.findByUUID", this::findByUUID);

    client.getConnection(result -> {
      if (result.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(result.cause());
      }
    });
  }

  @Override
  public void stop() throws Exception {
    this.client.close();
  }

  /**
   * Make a query on the MySQL database connected via JDBC. Available on the eventbus, address can be found in the
   * start method
   * If succeeded, it will return a JsonArray to the EventBus
   *
   * @param msg Eventbus Message containing a JsonObject
   */
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private void queryFind(Message<JsonObject> msg) {
    JsonObject message = msg.body();

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      JsonArray queryParams = message.getJsonArray("queryParams");
      connection.queryWithParams(message.getString("query"), queryParams, queryResult -> {
        connection.close();

        if (queryResult.failed()) {
          msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), queryResult.cause().getMessage());
          return;
        }

        JsonArray result = new JsonArray(queryResult.result().getRows());
        msg.reply(result);
      });
    });
  }

  /**
   * Make a query on the MySQL database connected via JDBC. Available on the eventbus, address can be found in the
   * start method
   * If succeeded, it will return a JsonArray to the EventBus
   *
   * @param msg Eventbus Message containing a JsonObject
   */
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private void queryFindOne(Message<JsonObject> msg) {
    JsonObject message = msg.body();

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      JsonArray queryParams = message.getJsonArray("queryParams");
      connection.queryWithParams(message.getString("query"), queryParams, queryResult -> {
        connection.close();

        if (queryResult.failed()) {
          msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), queryResult.cause().getMessage());
          return;
        }

        if (queryResult.result().getRows().isEmpty()) {
          msg.reply(null);
          return;
        }

        msg.reply(queryResult.result().getRows().iterator().next());
      });
    });
  }

  /**
   * Make a query on the MySQL database connected via JDBC. Available on the eventbus, address can be found in the
   * start method
   * If succeeded, it will return a JsonArray to the EventBus
   *
   * @param msg Eventbus Message containing a JsonObject
   */
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private void findByPk(Message<JsonObject> msg) {
    String tableName = msg.body().getString("bucket");
    Integer primaryKey = msg.body().getInteger("primaryKey");

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      Query query = MySQL.JooqQuery.select().from(table(tableName)).where(field("id").equal(primaryKey));
      JsonArray queryParams = new JsonArray(query.getBindValues());

      connection.queryWithParams(query.getSQL(), queryParams, queryResult -> {
        connection.close();

        if (queryResult.failed()) {
          msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), queryResult.cause().getMessage());
          return;
        }

        JsonArray result = new JsonArray(queryResult.result().getRows());
        msg.reply(result.getJsonObject(0));
      });
    });
  }

  /**
   * Make a query on the MySQL database connected via JDBC. Available on the eventbus, address can be found in the
   * start method
   * If succeeded, it will return a JsonArray to the EventBus
   *
   * @param msg Eventbus Message containing a JsonObject
   */
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private void findByUUID(Message<JsonObject> msg) {
    String tableName = msg.body().getString("bucket");
    String uuid = msg.body().getString("uuid");

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      Query query = MySQL.JooqQuery.select().from(table(tableName)).where(field("uuid").equal(uuid));
      JsonArray queryParams = new JsonArray(query.getBindValues());

      connection.queryWithParams(query.getSQL(), queryParams, queryResult -> {
        connection.close();

        if (queryResult.failed()) {
          msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), queryResult.cause().getMessage());
          return;
        }

        JsonArray result = new JsonArray(queryResult.result().getRows());
        if (result.isEmpty()) {
          msg.reply(null);
          return;
        }

        msg.reply(result.getJsonObject(0));
      });
    });
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private void queryUpdate(Message<JsonObject> msg) {
    JsonObject message = msg.body();

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      JsonArray queryParams = message.getJsonArray("queryParams");
      connection.updateWithParams(message.getString("query"), queryParams, queryResult -> {
        connection.close();

        if (queryResult.failed()) {
          msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), queryResult.cause().getMessage());
          return;
        }

        msg.reply(queryResult.result().toJson());
      });
    });
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private void queryUpdateTransactional(Message<JsonObject> msg) {
    JsonObject message = msg.body();

    JsonArray queries = message.getJsonArray("queries");
    JsonArray queryParams = message.getJsonArray("queryParams");

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      connection.setAutoCommit(false, autoCommitRes -> {
        if (autoCommitRes.failed()) {
          msg.fail(ApiStatus.STATUS_DB_AUTO_COMMIT_FAILED.statusCode(), res.cause().getMessage());
          return;
        }

        Iterator queriesIterator = queries.iterator();
        int iteratorPosition = 0;
        while (queriesIterator.hasNext()) {
          String query = (String) queriesIterator.next();

          connection.updateWithParams(query, queryParams.getJsonArray(iteratorPosition), updateRes -> {
            if (updateRes.failed()) {
              msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), updateRes.cause().getMessage());
              return;
            }

            this.lastResult = updateRes;
          });

          iteratorPosition++;
        }

        connection.commit(commitResult -> {
          if (commitResult.failed()) {
            msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), commitResult.cause().getMessage());
            return;
          }

          msg.reply(this.lastResult.result().toJson());
          return;
        });
      });
    });
  }

  private void queryBatch(Message<JsonObject> msg) {
    JsonObject request = msg.body();

    List queries = Arrays.asList(request.getString("queries").split(";"));
    final List[] result = new List[1];

    client.getConnection(res -> {
      if (res.failed()) {
        msg.fail(ApiStatus.STATUS_DB_CONNECTION_FAILED.statusCode(), res.cause().getMessage());
        return;
      }

      SQLConnection connection = res.result();

      connection.setAutoCommit(false, autoCommitRes -> {
        if (autoCommitRes.failed()) {
          msg.fail(ApiStatus.STATUS_DB_AUTO_COMMIT_FAILED.statusCode(), res.cause().getMessage());
          return;
        }

        connection.batch(queries, updateRes -> {
          if (updateRes.failed()) {
            msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), updateRes.cause().getMessage());
            return;
          }
          result[0] = updateRes.result();
        }).commit(commitResult -> {
          if (commitResult.failed()) {
            msg.fail(ApiStatus.STATUS_DB_QUERY_FAILURE.statusCode(), commitResult.cause().getMessage());
            return;
          }

          msg.reply(new JsonArray(result[0]));
        });

      });
    });
  }
}
