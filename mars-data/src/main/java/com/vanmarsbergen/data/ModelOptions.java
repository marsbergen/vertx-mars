/*
 * Copyright (c) 2016. Fundd.co. You are allowed to re-use this code for free, but please share our product in some way on social media or just share some Fundd love with the people that are near to you.
 */

package com.vanmarsbergen.mars.data;

import com.vanmarsbergen.mars.data.impl.ModelOptionsImpl;

public interface ModelOptions {

  static ModelOptions create() {
    return new ModelOptionsImpl();
  }

  /**
   * When you enable relations on API results, relations are added to the API result.
   * By default relations won't be passed due to the heavier database usage.
   *
   * @return this
   */
  ModelOptions enableRelations();

  /**
   * When you disable relations on API results, relations are not passed to the API result.
   * By default relations won't be passed due to the heavier database usage.
   *
   * @return this
   */
  ModelOptions disableRelations();

  /**
   * When relations are enabled, this will add all the relations of relations
   * By default relations of relations won't be passed due to the heavier database usage.
   *
   * @return this
   */
  ModelOptions enableDeepRelations();

  ModelOptions disableDeepRelations();

  /**
   * Check if relations are enabled.
   * @return True if so, false if not.
   */
  boolean isRelationEnabled();

  boolean isDeepRelationEnabled();

  ModelOptions hideAuthorizedData();

  ModelOptions showAuthorizedData();

  boolean isAuthorizedDataVisible();

}
