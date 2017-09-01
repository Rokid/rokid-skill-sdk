package com.rokid.skill.dispatcher;

import com.google.common.annotations.Beta;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an event subscriber method as being thread-safe. This annotation indicates that RokidBus
 * may invoke the event subscriber simultaneously from multiple threads.
 *
 * <p>This does not mark the method, and so should be used in combination with {@link RokidSubscribe}.
 */
@SuppressWarnings("WeakerAccess")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Beta
public @interface AllowConcurrentEvents {}