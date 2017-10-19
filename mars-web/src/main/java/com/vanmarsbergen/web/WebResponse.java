package com.vanmarsbergen.mars.web;

import com.vanmarsbergen.mars.core.api.ApiStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface WebResponse {
  String getContentType();

  WebResponse setContentType(String contentType);

  WebResponse setHttpServerResponse(HttpServerResponse httpServerResponse);

  WebResponse setStatus(HttpStatus httpStatus);

  WebResponse setPagination(int currentPage, int totalPages);

  WebResponse setData(String data);

  WebResponse setData(JsonObject data);

  WebResponse setData(JsonArray data);

  WebResponse addError(Throwable throwable);

  WebResponse addError(ApiStatus errorCode);

  WebResponse addError(ApiStatus errorCode, String customErrorMessage);

  WebResponse addError(ApiStatus errorCode, String pointer, String customErrorMessage);

  WebResponse clearErrors();

  WebResponse setMeta(JsonObject customMetaData);

  String toString();

  void end(String data, HttpStatus httpStatus);

  void end(String data);

  void endNull();

  void end(JsonObject singleItem, HttpStatus httpStatus);

  void end(JsonObject singleItem);

  void end(JsonArray multiItems, HttpStatus httpStatus);

  void end(JsonArray multiItems);

  void end(HttpStatus httpStatus);

  void end();
}
