package io.github.zodh.college.manager.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Generics {

  public static String toJson(Object object) {
    try {
      var objectMapper = new ObjectMapper();
      var javaTimeModule = new JavaTimeModule();
      javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
          DateTimeFormatter.ISO_DATE_TIME));
      objectMapper.registerModule(javaTimeModule);
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      return objectMapper.writeValueAsString(object);
    } catch (Exception exception) {
      log.error("Error trying to convert object to json. Error: {}", exception.getMessage());
      return null;
    }
  }

}
