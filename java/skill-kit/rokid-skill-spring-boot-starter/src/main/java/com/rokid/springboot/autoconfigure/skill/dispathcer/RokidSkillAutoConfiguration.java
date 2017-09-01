package com.rokid.springboot.autoconfigure.skill.dispathcer;

import com.rokid.skill.dispatcher.RokidBus;
import com.rokid.skill.dispatcher.RokidDefaultIntentHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author lion.xys
 */
@Configuration
@Import(RokidRequestController.class)
public class RokidSkillAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public AnnotationRokidRequestHandlerDiscoverer defaultRokidRequestHandlerFinder() {
    return new AnnotationRokidRequestHandlerDiscoverer();
  }

  @Configuration
  @ConditionalOnBean({RokidDefaultIntentHandler.class, AnnotationRokidRequestHandlerDiscoverer.class})
  @ConditionalOnMissingBean({RokidBus.class})
  static class RokidRequestBusConfiguration {

    private final RokidDefaultIntentHandler welcomeIntentHandler;

    private final AnnotationRokidRequestHandlerDiscoverer discoverer;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public RokidRequestBusConfiguration(
        RokidDefaultIntentHandler welcomeIntentHandler,
        AnnotationRokidRequestHandlerDiscoverer discoverer) {
      this.welcomeIntentHandler = welcomeIntentHandler;
      this.discoverer = discoverer;
    }

    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public RokidBus requestBus() {

      RokidBus bus = new RokidBus(welcomeIntentHandler);

      registerRokidHandlers(bus, discoverer);

      return bus;
    }
  }

  @Configuration
  @ConditionalOnBean({AnnotationRokidRequestHandlerDiscoverer.class})
  @ConditionalOnMissingBean({RokidDefaultIntentHandler.class, RokidBus.class})
  static class DefaultRokidRequestBusConfiguration {

    private final AnnotationRokidRequestHandlerDiscoverer discoverer;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public DefaultRokidRequestBusConfiguration(
        AnnotationRokidRequestHandlerDiscoverer discoverer) {
      this.discoverer = discoverer;
    }

    @Bean
    public RokidBus requestBus() {

      RokidBus bus = new RokidBus();

      registerRokidHandlers(bus, discoverer);

      return bus;
    }
  }

  private static void registerRokidHandlers(RokidBus bus,
      AnnotationRokidRequestHandlerDiscoverer discoverer) {

    for (Object object : discoverer.findRokidRequestHandler()) {
      bus.register(object);
    }

  }

}