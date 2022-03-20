package io.github.zodh.college.manager.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

@Getter
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class FlowException extends RuntimeException {

  private String message;

  private String requestId;

  private String errorDescription;

  private HttpStatus httpStatus;
}
