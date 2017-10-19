/*
 * Copyright (c) 2016. Fundd.co. You are allowed to re-use this code for free, but please share our product in some way on social media or just share some Fundd love with the people that are near to you.
 */

package com.vanmarsbergen.mars.data.impl;

import com.vanmarsbergen.mars.data.ModelOptions;

public class ModelOptionsImpl implements ModelOptions {

  private boolean relations = false;
  private boolean deepRelations = false;
  private boolean authorizedDataVisible = true;

  @Override
  public ModelOptions enableRelations() {
    this.relations = true;
    return this;
  }

  @Override
  public ModelOptions disableRelations() {
    this.relations = false;
    return this;
  }

  @Override
  public ModelOptions enableDeepRelations() {
    this.deepRelations = true;
    return this;
  }

  @Override
  public ModelOptions disableDeepRelations() {
    this.deepRelations = false;
    return this;
  }

  public boolean isRelationEnabled() {
    return relations;
  }

  public boolean isDeepRelationEnabled() {
    return deepRelations;
  }

  @Override
  public ModelOptions hideAuthorizedData() {
    this.authorizedDataVisible = false;
    return this;
  }

  @Override
  public ModelOptions showAuthorizedData() {
    this.authorizedDataVisible = true;
    return this;
  }

  public boolean isAuthorizedDataVisible() {
    return authorizedDataVisible;
  }
}
