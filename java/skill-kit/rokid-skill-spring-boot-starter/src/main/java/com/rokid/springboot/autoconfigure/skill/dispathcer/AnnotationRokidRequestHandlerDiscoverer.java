package com.rokid.springboot.autoconfigure.skill.dispathcer;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * find rokid reuqust handler by {@link RokidRequestHandler}
 *
 * @author lion.xys
 */
@Slf4j
@SuppressWarnings("unused")
public class AnnotationRokidRequestHandlerDiscoverer implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * find all rokid request handler
   *
   * @return rokid request handler list
   */
  public Collection<Object> findRokidRequestHandler() {

    String[] beanNames = this.applicationContext
        .getBeanNamesForAnnotation(RokidRequestHandler.class);

    List<Object> beans = Lists.newArrayList();

    for (String beanName : beanNames) {

      beans.add(this.applicationContext.getBean(beanName));

    }

    return beans;
  }
}
