package com.rokid.skill.dispatcher;

import static com.google.common.base.Preconditions.checkNotNull;

import com.rokid.skill.protocol.RokidRequest;
import java.lang.reflect.Method;

/**
 * Context for an exception thrown by a subscriber.
 */
@SuppressWarnings("WeakerAccess")
public class SubscriberExceptionContext {
  private final RokidBus eventBus;
  private final RokidRequest event;
  private final Object subscriber;
  private final Method subscriberMethod;

  /**
   * @param eventBus The {@link RokidBus} that handled the event and the subscriber. Useful for
   *     broadcasting a a new event based on the error.
   * @param event The event object that caused the subscriber to throw.
   * @param subscriber The source subscriber context.
   * @param subscriberMethod the subscribed method.
   */
  SubscriberExceptionContext(
      RokidBus eventBus, RokidRequest event, Object subscriber, Method subscriberMethod) {
    this.eventBus = checkNotNull(eventBus);
    this.event = checkNotNull(event);
    this.subscriber = checkNotNull(subscriber);
    this.subscriberMethod = checkNotNull(subscriberMethod);
  }

  /**
   * @return The {@link RokidBus} that handled the event and the subscriber. Useful for broadcasting
   *     a a new event based on the error.
   */
  @SuppressWarnings("WeakerAccess")
  public RokidBus getEventBus() {
    return eventBus;
  }

  /**
   * @return The event object that caused the subscriber to throw.
   */
  public RokidRequest getEvent() {
    return event;
  }

  /**
   * @return The object context that the subscriber was called on.
   */
  @SuppressWarnings("WeakerAccess")
  public Object getSubscriber() {
    return subscriber;
  }

  /**
   * @return The subscribed method that threw the exception.
   */
  @SuppressWarnings("WeakerAccess")
  public Method getSubscriberMethod() {
    return subscriberMethod;
  }
}
