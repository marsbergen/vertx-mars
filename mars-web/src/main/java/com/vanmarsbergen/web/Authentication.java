package com.vanmarsbergen.mars.web;

import com.vanmarsbergen.mars.web.impl.AuthenticationImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.handler.JWTAuthHandler;

public interface Authentication {
  static JWTAuth getAuthProvider(Vertx vertx, String password) {
    return new AuthenticationImpl(vertx, password).getAuthProvider();
  }

  static JWTAuthHandler getJWTAuthHandler(Vertx vertx, String password) {
    return JWTAuthHandler.create(Authentication.getAuthProvider(vertx, password));
  }
}
