package com.vanmarsbergen.mars.data.redis;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public interface Redis {
  public static RedisClient createClient(Vertx vertx, JsonObject config) {
    return RedisClient.create(vertx, new RedisOptions().setHost(config.getString("host")));
  }
}
