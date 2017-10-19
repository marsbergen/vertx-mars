package com.vanmarsbergen.mars.web.impl;

import com.vanmarsbergen.mars.web.Authentication;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

public class AuthenticationImpl implements Authentication {
  private JWTAuth authProvider;

  public AuthenticationImpl(Vertx vertx, String password) {
    JsonObject jwtConfig = new JsonObject().put("keyStore", new JsonObject()
      .put("path", "keystore.jceks")
      .put("type", "jceks")
      .put("password", password));

    this.authProvider = JWTAuth.create(vertx, jwtConfig);
  }

  public JWTAuth getAuthProvider() {
    return authProvider;
  }
}
