package com.vanmarsbergen.mars.data.mysql;

import io.vertx.core.json.JsonObject;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public interface MySQL {
  static JsonObject prepareRequest(Query query) {
    return new JsonObject()
      .put("query", query.getSQL())
      .put("queryParams", query.getBindValues());
  }

  DSLContext JooqQuery = DSL.using(SQLDialect.MARIADB);
}
