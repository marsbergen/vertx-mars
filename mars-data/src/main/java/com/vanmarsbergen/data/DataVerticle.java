package com.vanmarsbergen.mars.data;

import com.vanmarsbergen.mars.core.api.ApiStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;

public class DataVerticle extends AbstractVerticle {
  protected Logger logger;

  protected void validateMySQLAndReply(Message msg, AsyncResult<Message<Object>> reply) {
    if (reply.failed()) {
      msg.fail(ApiStatus.FAILED_FIND.statusCode(), ApiStatus.FAILED_FIND.toString());
      return;
    }

    if (reply.result().body() == null) {
      msg.fail(ApiStatus.STATUS_DB_NO_RECORDS.statusCode(), ApiStatus.STATUS_DB_NO_RECORDS.toString());
      return;
    }

    msg.reply(reply.result().body());
  }
}
