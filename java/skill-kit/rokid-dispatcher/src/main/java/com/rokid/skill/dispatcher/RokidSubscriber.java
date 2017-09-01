package com.rokid.skill.dispatcher;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.rokid.skill.protocol.RokidContext;
import com.rokid.skill.protocol.RokidRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

/**
 * A subscriber method on a specific object, plus the executor that should be used for dispatching
 * events to it.
 *
 * <p>Two subscribers are equivalent when they refer to the same method on the same object (not
 * class). This property is used to ensure that no subscriber method is registered more than once.
 */
class RokidSubscriber {

  /**
   * Creates a {@code RokidSubscriber} for {@code method} on {@code listener}.
   */
  static RokidSubscriber create(RokidBus bus, Object listener, Method method) {
    return isDeclaredThreadSafe(method)
        ? new RokidSubscriber(bus, listener, method)
        : new SynchronizedSubscriber(bus, listener, method);
  }

  /** The event bus this subscriber belongs to. */
  private RokidBus bus;

  /** The object with the subscriber method. */
  @SuppressWarnings("WeakerAccess")
  @VisibleForTesting final Object target;

  /** RokidSubscriber method. */
  private final Method method;

  /** Executor to use for dispatching events to this subscriber. */
  private final Executor executor;

  private RokidSubscriber(RokidBus bus, Object target, Method method) {
    this.bus = bus;
    this.target = checkNotNull(target);
    this.method = method;
    method.setAccessible(true);

    this.executor = bus.executor();
  }

  /**
   * Dispatches {@code event} to this subscriber using the proper executor.
   */
  final void dispatchEvent() {

    final RokidRequest request = RokidContext.getRequest();

    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            try {
              invokeSubscriberMethod(request);
            } catch (InvocationTargetException e) {
              bus.handleSubscriberException(e.getCause(), context(request));
            }
          }
        });
  }

  /**
   * Gets the context for the given event.
   */
  private SubscriberExceptionContext context(RokidRequest event) {
    return new SubscriberExceptionContext(bus, event, target, method);
  }

  /**
   * Invokes the subscriber method. This method can be overridden to make the invocation
   * synchronized.
   */
  @VisibleForTesting
  void invokeSubscriberMethod(RokidRequest request) throws InvocationTargetException {
    try {
      method.invoke(target, checkNotNull(request));
    } catch (IllegalArgumentException e) {
      throw new Error("Method rejected target/argument: " + request, e);
    } catch (IllegalAccessException e) {
      throw new Error("Method became inaccessible: " + request, e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Error) {
        throw (Error) e.getCause();
      }
      throw e;
    }
  }

  @Override
  public final int hashCode() {
    return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
  }

  @Override
  public final boolean equals(@Nullable Object obj) {
    if (obj instanceof RokidSubscriber) {
      RokidSubscriber that = (RokidSubscriber) obj;
      // Use == so that different equal instances will still receive events.
      // We only guard against the case that the same object is registered
      // multiple times
      return target == that.target && method.equals(that.method);
    }
    return false;
  }

  /**
   * Checks whether {@code method} is thread-safe, as indicated by the presence of the
   * {@link AllowConcurrentEvents} annotation.
   */
  private static boolean isDeclaredThreadSafe(Method method) {
    return method.getAnnotation(AllowConcurrentEvents.class) != null;
  }

  /**
   * RokidSubscriber that synchronizes invocations of a method to ensure that only one thread may enter
   * the method at a time.
   */
  @VisibleForTesting
  static final class SynchronizedSubscriber extends RokidSubscriber {

    private SynchronizedSubscriber(RokidBus bus, Object target, Method method) {
      super(bus, target, method);
    }

    @Override
    void invokeSubscriberMethod(RokidRequest request) throws InvocationTargetException {
      synchronized (this) {
        super.invokeSubscriberMethod(request);
      }
    }
  }
}
