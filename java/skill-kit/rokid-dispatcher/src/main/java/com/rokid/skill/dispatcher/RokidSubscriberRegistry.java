package com.rokid.skill.dispatcher;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.rokid.skill.protocol.RokidRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

/**
 * Registry of subscribers to a single rokid dispatcher.
 */
final class RokidSubscriberRegistry {

  /**
   * All registered subscribers, indexed by event type.
   *
   * <p>The {@link CopyOnWriteArraySet} values make it easy and relatively lightweight to get an
   * immutable snapshot of all current subscribers to an event without any locking.
   */
  private final ConcurrentMap<String, CopyOnWriteArraySet<RokidSubscriber>> subscribers =
      Maps.newConcurrentMap();

  /**
   * The rokid request bus this registry belongs to.
   */
  private final RokidBus bus;

  RokidSubscriberRegistry(RokidBus bus) {
    this.bus = checkNotNull(bus);
  }

  /**
   * Registers all subscriber methods on the given listener object.
   */
  void register(Object listener) {
    Multimap<String, RokidSubscriber> listenerMethods = findAllSubscribers(listener);

    for (Map.Entry<String, Collection<RokidSubscriber>> entry : listenerMethods.asMap()
        .entrySet()) {
      String eventType = entry.getKey();
      Collection<RokidSubscriber> eventMethodsInListener = entry.getValue();

      CopyOnWriteArraySet<RokidSubscriber> eventSubscribers = subscribers.get(eventType);

      if (eventSubscribers == null) {
        CopyOnWriteArraySet<RokidSubscriber> newSet = new CopyOnWriteArraySet<RokidSubscriber>();
        eventSubscribers =
            MoreObjects.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
      }

      eventSubscribers.addAll(eventMethodsInListener);
    }
  }

  /**
   * Unregisters all subscribers on the given listener object.
   */
  void unregister(Object listener) {
    Multimap<String, RokidSubscriber> listenerMethods = findAllSubscribers(listener);

    for (Map.Entry<String, Collection<RokidSubscriber>> entry : listenerMethods.asMap()
        .entrySet()) {
      String eventType = entry.getKey();
      Collection<RokidSubscriber> listenerMethodsForType = entry.getValue();

      CopyOnWriteArraySet<RokidSubscriber> currentSubscribers = subscribers.get(eventType);
      if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType)) {
        // if removeAll returns true, all we really know is that at least one subscriber was
        // removed... however, barring something very strange we can assume that if at least one
        // subscriber was removed, all subscribers on listener for that event type were... after
        // all, the definition of subscribers on a particular class is totally static
        throw new IllegalArgumentException(
            "missing event subscriber for an annotated method. Is " + listener + " registered?");
      }

      // don't try to remove the set if it's empty; that can't be done safely without a lock
      // anyway, if the set is empty it'll just be wrapping an array of length 0
    }
  }

  @SuppressWarnings("unused")
  @VisibleForTesting
  Set<RokidSubscriber> getSubscribersForTesting(String eventType) {
    return MoreObjects.firstNonNull(subscribers.get(eventType), ImmutableSet.<RokidSubscriber>of());
  }

  /**
   * Gets an iterator representing an immutable snapshot of all subscribers to the given event at
   * the time this method is called.
   */
  Iterator<RokidSubscriber> getSubscribers(String event) {

    return subscribers.get(event).iterator();
  }

  /**
   * A thread-safe cache that contains the mapping from each class to all methods in that class and
   * all super-classes, that are annotated with {@code @RokidSubscribe}. The cache is shared across
   * all instances of this class; this greatly improves performance if multiple RokidBus
   * instances are created and objects of the same class are registered on all of them.
   */
  private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache =
      CacheBuilder.newBuilder()
          .weakKeys()
          .build(
              new CacheLoader<Class<?>, ImmutableList<Method>>() {
                @Override
                public ImmutableList<Method> load(
                    @SuppressWarnings("NullableProblems") Class<?> concreteClass) throws Exception {
                  return getAnnotatedMethodsNotCached(concreteClass);
                }
              });

  /**
   * Returns all subscribers for the given listener grouped by the type of event they subscribe to.
   */
  private Multimap<String, RokidSubscriber> findAllSubscribers(Object listener) {
    Multimap<String, RokidSubscriber> methodsInListener = HashMultimap.create();
    Class<?> clazz = listener.getClass();
    for (Method method : getAnnotatedMethods(clazz)) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length != 1) {
        throw new RuntimeException("the subscriber method must have only one parameter");
      }
      if (parameterTypes[0] != RokidRequest.class) {
        throw new RuntimeException("the subscriber method's parameter must be RokidRequest.class");
      }
      RokidSubscribe subscribe = method.getAnnotation(RokidSubscribe.class);
      methodsInListener.put(subscribe.value(), RokidSubscriber
          .create(bus, listener, method));
    }
    return methodsInListener;
  }

  private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
    return subscriberMethodsCache.getUnchecked(clazz);
  }

  private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
    Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
    Map<MethodIdentifier, Method> identifiers = Maps.newHashMap();
    for (Class<?> supertype : supertypes) {
      for (Method method : supertype.getDeclaredMethods()) {
        if (method.isAnnotationPresent(RokidSubscribe.class) && !method.isSynthetic()) {
          // TODO(cgdecker): Should check for a generic parameter type and error out
          Class<?>[] parameterTypes = method.getParameterTypes();
          checkArgument(
              parameterTypes.length == 1,
              "Method %s has @RokidSubscribe annotation but has %s parameters."
                  + "RokidSubscriber methods must have exactly 1 parameter.",
              method,
              parameterTypes.length);

          MethodIdentifier ident = new MethodIdentifier(method);
          if (!identifiers.containsKey(ident)) {
            identifiers.put(ident, method);
          }
        }
      }
    }
    return ImmutableList.copyOf(identifiers.values());
  }

  private static final class MethodIdentifier {

    private final String name;
    private final List<Class<?>> parameterTypes;

    MethodIdentifier(Method method) {
      this.name = method.getName();
      this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(name, parameterTypes);
    }

    @Override
    public boolean equals(@Nullable Object o) {
      if (o instanceof MethodIdentifier) {
        MethodIdentifier ident = (MethodIdentifier) o;
        return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
      }
      return false;
    }
  }
}
