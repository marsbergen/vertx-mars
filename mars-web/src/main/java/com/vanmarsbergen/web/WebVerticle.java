package com.vanmarsbergen.mars.web;

import com.vanmarsbergen.mars.core.api.ApiStatus;
import com.vanmarsbergen.mars.web.impl.WebResponseImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

import java.util.Objects;

/**
 * @author Patrick van Marsbergen <patrick@fundd.co>
 * @version 1.0
 * @copyright Copyright (c) 2017, Fundd
 */
public class WebVerticle extends AbstractVerticle {
  protected Router router;

  private void setupHealthCheck(String route) {
    router.route(HttpMethod.GET, String.format("%s/check", route)).handler(routingContext ->
      new WebResponseImpl(routingContext.response()).end()
    );
  }

  private void setupRequestBehavior() {
    router.route()
      .handler(CorsHandler.create("http://dev.escape.dailyco.de:7001|https://escape.dailyco.de")
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.DELETE)
        .allowedMethod(HttpMethod.OPTIONS)
        .allowedMethod(HttpMethod.PATCH)
        .allowedMethod(HttpMethod.PUT)
        .allowedMethod(HttpMethod.POST)
        .allowedHeader("Origin")
        .allowedHeader("Depth")
        .allowedHeader("User-Agent")
        .allowedHeader("X-Requested-With")
        .allowedHeader("Content-Type")
        .allowedHeader("Accept")
        .allowedHeader("Authorization")
        .allowedHeader("Cache-Control")
        .allowedHeader("X-File-Name")
        .allowedHeader("If-Modified-Since").maxAgeSeconds(6000)
      );
    router.route().method(HttpMethod.OPTIONS).handler(this::optionsRequest);
    router.route().method(HttpMethod.POST).handler(BodyHandler.create());
    router.route().method(HttpMethod.PATCH).handler(BodyHandler.create());
  }

  protected void prerequisite(RoutingContext routingContext) {
    if (!hasRequestContentType(routingContext)) {
      return;
    }

    routingContext.next();
  }

  protected JWTAuthHandler getAuthenticationHandler() {
    String keyStorePassword = config().getJsonObject("authentication").getJsonObject("key_store").getString("password");
    return Authentication.getJWTAuthHandler(vertx, keyStorePassword);
  }

  private void optionsRequest(RoutingContext ctx) {
    new WebResponseImpl(ctx.response())
      .end(HttpStatus.OK);
  }

  protected boolean hasRequestContentType(HttpServerRequest httpServerRequest) {
    return Objects.equals(httpServerRequest.getHeader(HttpHeaders.CONTENT_TYPE), new WebResponseImpl().getContentType());
  }

  private boolean hasRequestContentType(RoutingContext ctx) {
    boolean correctContentType = Objects.equals(ctx.request().getHeader(HttpHeaders.CONTENT_TYPE), new WebResponseImpl().getContentType());
    if (!correctContentType) {
      new WebResponseImpl(ctx.response())
        .addError(ApiStatus.HTTP_ERROR_CONTENT_TYPE)
        .end(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    return correctContentType;
  }

  private void defaultFailedCheck(HttpServerResponse response, AsyncResult<Message<Object>> reply) {
    if (((ReplyException) reply.cause()).failureCode() == ApiStatus.STATUS_DB_NO_RECORDS.statusCode()) {
      new WebResponseImpl(response)
        .addError(ApiStatus.HTTP_MODEL_NOT_FOUND)
        .end(HttpStatus.NOT_FOUND);
      return;
    }

    if (((ReplyException) reply.cause()).failureCode() == ApiStatus.STATUS_DB_DUPLICATE_RECORD.statusCode()) {
      new WebResponseImpl(response)
        .addError(ApiStatus.HTTP_ERROR_INSERT_DUPLICATE)
        .end(HttpStatus.CONFLICT);
      return;
    }

    Logger logger = LoggerFactory.getLogger(getClass().getName());
    logger.warn(reply.cause().getMessage());

    new WebResponseImpl(response)
      .addError(reply.cause())
      .end(HttpStatus.BAD_REQUEST);
  }

  protected JsonObject postContentCheck(RoutingContext ctx, String objectType) {
    return postContentCheck(ctx, objectType, null);
  }

  private JsonObject postContentCheck(RoutingContext ctx, String objectType, JsonArray extraFields) {
    JsonObject requestBody;
    try {
      requestBody = ctx.getBodyAsJson();
      if (requestBody == null || requestBody.isEmpty()) {
        new WebResponseImpl(ctx.response())
          .addError(ApiStatus.HTTP_ERROR_INSERT_NO_BODY)
          .end(HttpStatus.NOT_ACCEPTABLE);
        return null;
      }
    } catch (DecodeException exception) {
      new WebResponseImpl(ctx.response())
        .addError(ApiStatus.HTTP_ERROR_JSON_FORMAT)
        .end(HttpStatus.NOT_ACCEPTABLE);
      return null;
    }

    if (requestBody.containsKey("type") &&
      !requestBody.getString("type").equals(objectType) ||
      (extraFields != null && !requestBody.fieldNames().containsAll(extraFields.getList()))
      ) {

      ApiStatus apiStatus;

      if (requestBody.getString("type").equals(objectType)) {
        apiStatus = ctx.request().method() == HttpMethod.POST ?
          ApiStatus.HTTP_ERROR_INSERT_FIELDS_MISSING : ApiStatus.HTTP_ERROR_UPDATE_FIELDS_MISSING;
      } else {
        apiStatus = ctx.request().method() == HttpMethod.POST ?
          ApiStatus.HTTP_ERROR_INSERT_INVALID_DOCUMENT_TYPE : ApiStatus.HTTP_ERROR_UPDATE_INVALID_DOCUMENT_TYPE;
      }

      new WebResponseImpl(ctx.response())
        .addError(apiStatus)
        .end(HttpStatus.NOT_ACCEPTABLE);
      return null;
    }

    return requestBody;
  }

  protected void dataResultToHttpResponse(HttpServerResponse response, AsyncResult<Message<Object>> reply) {
    if (reply.failed()) {
      defaultFailedCheck(response, reply);
      return;
    }

    if (reply.result().body() instanceof JsonArray) {
      JsonArray resultArray = (JsonArray) reply.result().body();

      if (reply.result() == null || reply.result().body() == null || resultArray.isEmpty()) {
        new WebResponseImpl(response)
          .end(HttpStatus.NO_CONTENT);
        return;
      }

      new WebResponseImpl(response)
        .end(resultArray);
      return;

    }

    JsonObject resultObject = (JsonObject) reply.result().body();

    if (reply.result() == null || reply.result().body() == null || resultObject.isEmpty()) {
      new WebResponseImpl(response)
        .end(HttpStatus.NO_CONTENT);
      return;
    }

    new WebResponseImpl(response)
      .end(resultObject);
  }

  protected void start(String route) throws Exception {
    this.router = Router.router(vertx);

    setupHealthCheck(route);
    setupRequestBehavior();

    vertx.createHttpServer().requestHandler(router::accept).listen(config().getJsonObject("web").getInteger("port"));
  }
}
