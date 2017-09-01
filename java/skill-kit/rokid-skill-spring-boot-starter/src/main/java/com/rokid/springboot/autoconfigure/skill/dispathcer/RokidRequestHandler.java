package com.rokid.springboot.autoconfigure.skill.dispathcer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;

/**
 * handle the rokid requst Whether it is intent request or event request
 *
 * @author lion.xys
 */
@SuppressWarnings("WeakerAccess")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RokidRequestHandler {

}
