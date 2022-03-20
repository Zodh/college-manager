package io.github.zodh.college.manager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollegeMessagerConfiguration {

  public static final String CALLER_PLUGIN_QUEUE = "Caller";
  public static final String CALLER_PLUGIN_EXCHANGE = "CallerMessagesManager";
  public static final String CALLER_ROUTING_KEY = "CallerRK";

  public static String toJson(Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (Exception exception) {
      return null;
    }
  }

  @Bean
  DirectExchange callerExchange() {
    return new DirectExchange(CALLER_PLUGIN_EXCHANGE);
  }

  @Bean
  Queue callerQueue() {
    return new Queue(CALLER_PLUGIN_QUEUE, true, false, false);
  }

  @Bean
  Binding callerBinding() {
    return new Binding(callerExchange().getName(), DestinationType.EXCHANGE,
        callerExchange().getName(), CALLER_ROUTING_KEY, null);
  }
}
