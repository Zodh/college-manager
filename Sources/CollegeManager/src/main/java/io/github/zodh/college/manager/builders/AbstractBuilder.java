package io.github.zodh.college.manager.builders;


public abstract class AbstractBuilder {

  public abstract Object build(Object request, String user, String requestId);

  abstract boolean isValidRequest(Object request);
}
