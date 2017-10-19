/*
 * Copyright (c) 2016. Fundd.co. You are allowed to re-use this code for free, but please share our product in some way on social media or just share some Fundd love with the people that are near to you.
 */

package com.vanmarsbergen.mars.web.impl;

import com.vanmarsbergen.mars.core.api.ApiStatus;
import com.vanmarsbergen.mars.web.HttpStatus;
import com.vanmarsbergen.mars.web.WebResponse;
import com.vanmarsbergen.mars.web.exception.WebResponseException;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class WebResponseImpl implements WebResponse {
  private JsonObject data = new JsonObject();
  private JsonObject metaData = new JsonObject();
  private JsonArray errors = new JsonArray();
  private HttpServerResponse httpServerResponse;
  private HttpStatus httpStatus = HttpStatus.OK;
  private String contentType = "application/vnd.escape-v1+json";

  public WebResponseImpl() {
  }

  public WebResponseImpl(HttpServerResponse httpServerResponse) {
    this.httpServerResponse = httpServerResponse;
  }

  public WebResponseImpl(HttpServerResponse httpServerResponse, HttpStatus httpStatus) {
    this.httpServerResponse = httpServerResponse;
    this.httpStatus = httpStatus;
  }

  public String getContentType() {
    return contentType;
  }

  public WebResponse setContentType(String contentType) {
    this.contentType = contentType;

    return this;
  }

  public WebResponse setHttpServerResponse(HttpServerResponse httpServerResponse) {
    this.httpServerResponse = httpServerResponse;

    return this;
  }

  public WebResponse setStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;

    return this;
  }

  /**
   * Sets pagination to the meta data of the to be returned object
   *
   * @param currentPage Integer of the current page number. Always make sure that the first page is 1.
   * @param totalPages  Integer of the total pages with available data.
   */
  public WebResponse setPagination(int currentPage, int totalPages) {
    this.metaData
      .put("current-page", currentPage)
      .put("previous-page", (currentPage > 1) ? currentPage - 1 : null)
      .put("next-page", (currentPage < totalPages) ? currentPage + 1 : null)
      .put("total-pages", totalPages)
      .put("is-first-page", currentPage == 1)
      .put("is-last-page", currentPage == totalPages);

    return this;
  }

  /**
   * Set JsonObject to data which will return as a Json object
   *
   * @param data
   */
  public WebResponse setData(String data) {
    this.data = new JsonObject().put("data", data);
    return this;
  }

  /**
   * Set JsonObject to data which will return as a Json object
   *
   * @param data
   */
  public WebResponse setData(JsonObject data) {
    this.data = new JsonObject().put("data", data);
    return this;
  }

  /**
   * Set JsonArray to data which will return as a Json array
   *
   * @param data
   */
  public WebResponse setData(JsonArray data) {
    this.data = new JsonObject().put("data", data);
    return this;
  }

  public WebResponse addError(Throwable throwable) {
    JsonObject errorObject = new JsonObject()
      .put("status", ((ReplyException) throwable).failureCode())
      .put("title", throwable.getMessage())
      .put("source", new JsonObject().putNull("pointer"));
    this.errors.add(errorObject);

    return this;
  }

  /**
   * Set an error with errorCode and get the default message
   *
   * @param errorCode
   */
  public WebResponse addError(ApiStatus errorCode) {
    return addError(errorCode, null, errorCode.toString());
  }

  /**
   * Set an error with an errorCode and a custom error message
   *
   * @param errorCode
   * @param customErrorMessage
   */
  public WebResponse addError(ApiStatus errorCode, String customErrorMessage) {
    return addError(errorCode, null, customErrorMessage);
  }

  /**
   * Set an error with a custom errorCode, errorMessage and additional data
   *
   * @param errorCode
   * @param customErrorMessage
   * @param pointer
   */
  public WebResponse addError(ApiStatus errorCode, String pointer, String customErrorMessage) {
    JsonObject errorObject = new JsonObject()
      .put("status", errorCode.statusCode())
      .put("title", customErrorMessage)
      .put("source", new JsonObject().put("pointer", pointer));
    this.errors.add(errorObject);

    return this;
  }

  public WebResponse clearErrors() {
    this.errors = new JsonArray();

    return this;
  }

  public WebResponse setMeta(JsonObject customMetaData) {
    this.metaData = customMetaData;

    return this;
  }

  private JsonObject toJsonObject() {
    JsonObject response = new JsonObject();

    if (!metaData.isEmpty()) {
      response.put("meta", metaData);
    }

    if (errors.isEmpty()) {
      response.mergeIn(data);
    } else {
      response.put("errors", errors);
    }

    return response;
  }

  @Override
  public String toString() {
    if (
      httpStatus.equals(HttpStatus.NO_CONTENT) ||
        httpStatus.equals(HttpStatus.MOVED_TEMPORARY)
      ) {
      return "";
    }

    return toJsonObject().encode();
  }

  public void end(String data, HttpStatus httpStatus) {
    setData(data);
    setStatus(httpStatus);
    end();
  }

  public void end(String data) {
    setData(data);
    end();
  }

  public void endNull() {
    setStatus(HttpStatus.NO_CONTENT);
    end();
  }

  public void end(JsonObject singleItem, HttpStatus httpStatus) {
    setData(singleItem);
    setStatus(httpStatus);
    end();
  }

  public void end(JsonObject singleItem) {
    setData(singleItem);
    end();
  }

  public void end(JsonArray multiItems, HttpStatus httpStatus) {
    setData(multiItems);
    setStatus(httpStatus);
    end();
  }

  public void end(JsonArray multiItems) {
    setData(multiItems);
    end();
  }

  public void end(HttpStatus httpStatus) {
    setStatus(httpStatus);
    end();
  }

  public void end() {
    try {
      if (httpServerResponse == null) {
        throw new WebResponseException("No HTTP Server Response found");
      }
    } catch (WebResponseException e) {
      e.printStackTrace();
      return;
    }

    httpServerResponse
      .putHeader(HttpHeaders.CONTENT_TYPE, getContentType())
      .setStatusCode(httpStatus.code())
      .setStatusMessage(httpStatus.statusPhrase())
      .end(toString());
  }
}
