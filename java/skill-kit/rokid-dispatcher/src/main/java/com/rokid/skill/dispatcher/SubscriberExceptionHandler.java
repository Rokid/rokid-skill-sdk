package com.rokid.skill.dispatcher;

/**
 * Handler for exceptions thrown by event subscribers.
 */
public interface SubscriberExceptionHandler {
  /**
   * Handles exceptions thrown by subscribers.
   */
  void handleException(Throwable exception, SubscriberExceptionContext context);
}
