package com.rokid.skill.dispatcher;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.rokid.skill.protocol.RokidConstant.Event.ROKID_EVENT_UNKNOWN;
import static com.rokid.skill.protocol.RokidConstant.Intent.ROKID_INTENT_UNKNOWN;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.util.concurrent.MoreExecutors;
import com.rokid.skill.protocol.EventContent;
import com.rokid.skill.protocol.IntentContent;
import com.rokid.skill.protocol.RokidConstant;
import com.rokid.skill.protocol.RokidContext;
import com.rokid.skill.protocol.RokidRequest;
import com.rokid.skill.protocol.RokidResponse;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dispatches request to listeners, and provides ways for listeners to register themselves.
 */
@Beta
@SuppressWarnings("unused")
public class RokidBus {

  private static final Logger logger = Logger.getLogger(RokidBus.class.getName());

  private final String identifier;
  private final Executor executor;
  private final SubscriberExceptionHandler exceptionHandler;

  private final RokidSubscriberRegistry subscribers = new RokidSubscriberRegistry(this);
  private final RokidDispatcher dispatcher;

  /**
   * Creates a new RokidBus named "default".
   */
  public RokidBus() {
    this("default", null);
  }

  /**
   * Creates a new RokidBus named "default".
   */
  public RokidBus(RokidDefaultIntentHandler welcomeIntentHandler) {
    this("default", welcomeIntentHandler);
  }

  /**
   * Creates a new RokidBus with the given {@code identifier}.
   *
   * @param identifier a brief name for this bus, for logging purposes. Should be a valid Java
   * identifier.
   */
  @SuppressWarnings("WeakerAccess")
  public RokidBus(String identifier, RokidDefaultIntentHandler welcomeIntentHandler) {
    this(
        identifier,
        MoreExecutors.directExecutor(),
        RokidDispatcher.perThreadDispatchQueue(),
        DefaultExceptionHandler.INSTANCE, welcomeIntentHandler);
  }

  @SuppressWarnings("WeakerAccess")
  RokidBus(
      String identifier,
      Executor executor,
      RokidDispatcher dispatcher,
      SubscriberExceptionHandler exceptionHandler,
      RokidDefaultIntentHandler welcomeIntentHandler) {
    this.identifier = checkNotNull(identifier);
    this.executor = checkNotNull(executor);
    this.dispatcher = checkNotNull(dispatcher);
    this.exceptionHandler = checkNotNull(exceptionHandler);

    RokidRequestListener listener;
    if (welcomeIntentHandler == null) {
      listener = new RokidRequestListener(this);
    } else {
      listener = new RokidRequestListener(this, welcomeIntentHandler);
    }
    this.register(listener);
  }

  /**
   * Returns the identifier for this rokid request bus.
   */
  @SuppressWarnings("WeakerAccess,unused")
  public final String identifier() {
    return identifier;
  }

  /**
   * Returns the default executor this event bus uses for dispatching events to subscribers.
   */
  final Executor executor() {
    return executor;
  }

  /**
   * Handles the given exception thrown by a subscriber with the given context.
   */
  void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
    checkNotNull(e);
    checkNotNull(context);
    try {
      exceptionHandler.handleException(e, context);
    } catch (Throwable e2) {
      // if the handler threw an exception... well, just log it
      logger.log(
          Level.SEVERE,
          String.format(Locale.ROOT, "Exception %s thrown while handling exception: %s", e2, e),
          e2);
    }
  }

  /**
   * Registers all subscriber methods on {@code object} to receive events.
   *
   * @param object object whose subscriber methods should be registered.
   */
  @SuppressWarnings("WeakerAccess")
  public void register(Object object) {
    subscribers.register(object);
  }

  /**
   * Unregisters all subscriber methods on a registered {@code object}.
   *
   * @param object object whose subscriber methods should be unregistered.
   * @throws IllegalArgumentException if the object was not previously registered.
   */
  @SuppressWarnings("unused")
  public void unregister(Object object) {
    subscribers.unregister(object);
  }

  /**
   * post rokid request
   *
   * @param request rokid request to post.
   */
  @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
  public <T> RokidResponse post(RokidRequest<T> request) {

    // add to request context
    RokidContext.add(request);

    String reqType = request.getRequest().getReqType();

    Iterator<RokidSubscriber> eventSubscribers = subscribers.getSubscribers(reqType);

    if (eventSubscribers.hasNext()) {

      dispatcher.dispatch(eventSubscribers);

    } else if (Objects.equal(reqType, RokidConstant.ROKID_UNKNOWN))  {

      logger.warning("not listener subscribe this request type - " + reqType);

      request.getRequest().setReqType(RokidConstant.ROKID_UNKNOWN);

      this.post(request);

      request.getRequest().setReqType(reqType);

    }

    RokidResponse response = RokidContext.getResponse();

    RokidContext.clearContext();

    return response;
  }

  /**
   * post rokid intent request
   *
   * @param request rokid request to post.
   */
  void postIntent(RokidRequest<IntentContent> request) {

    String intent = request.getRequest().getContent().getIntent();

    Iterator<RokidSubscriber> eventSubscribers = subscribers.getSubscribers(intent);

    if (eventSubscribers.hasNext()) {

      dispatcher.dispatch(eventSubscribers);

    } else if (!Objects.equal(intent, ROKID_INTENT_UNKNOWN))  {
      logger.warning("not listener subscribe this intent - " + intent);

      request.getRequest().getContent().setIntent(ROKID_INTENT_UNKNOWN);

      this.postIntent(request);

      request.getRequest().getContent().setIntent(intent);
    }
  }

  /**
   * post rokid event request
   *
   * @param request rokid request to post.
   */
  void postEvent(RokidRequest<EventContent> request) {

    String event = request.getRequest().getContent().getEvent();

    Iterator<RokidSubscriber> eventSubscribers = subscribers.getSubscribers(event);

    if (eventSubscribers.hasNext()) {

      dispatcher.dispatch(eventSubscribers);

    } else if (!Objects.equal(event, ROKID_EVENT_UNKNOWN)) {

      logger.warning("not listener subscribe this event - " + event);

      request.getRequest().getContent().setEvent(ROKID_EVENT_UNKNOWN);

      this.postEvent(request);

      request.getRequest().getContent().setEvent(event);

    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).addValue(identifier).toString();
  }

  /**
   * Simple logging handler for subscriber exceptions.
   */
  static final class DefaultExceptionHandler implements SubscriberExceptionHandler {
    static final DefaultExceptionHandler INSTANCE = new DefaultExceptionHandler();

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
      Logger logger = logger(context);
      if (logger.isLoggable(Level.SEVERE)) {
        logger.log(Level.SEVERE, message(context), exception);
      }

      RokidContext.setVoice("我遇到了一点小问题");
    }

    private static Logger logger(SubscriberExceptionContext context) {
      return Logger.getLogger(RokidBus.class.getName() + "." + context.getEventBus().identifier());
    }

    private static String message(SubscriberExceptionContext context) {
      Method method = context.getSubscriberMethod();
      return "Exception thrown by subscriber method "
          + method.getName()
          + '('
          + method.getParameterTypes()[0].getName()
          + ')'
          + " on subscriber "
          + context.getSubscriber()
          + " when dispatching event: "
          + context.getEvent();
    }
  }
}
