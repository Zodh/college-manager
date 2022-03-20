package io.github.zodh.college.manager.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.zodh.college.manager.exceptions.FlowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ErrorMapperTest {

  private ErrorMapper errorMapperImpl;

  @BeforeEach
  void init(){
    errorMapperImpl = new ErrorMapperImpl();
  }

  @Test
  @DisplayName("Should map a ErrorResponse by a FlowException")
  void fromFlowExceptionToErrorResponse_success(){
    // arrange
    var flowException = new FlowException(null, "request-id", "generic-error", null);

    // act
    var errorResponse = errorMapperImpl.fromFlowExceptionToErrorResponse(flowException);

    // assert
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getRequestId(), "request-id");
    assertEquals(errorResponse.getErrorDescription(), "generic-error");
  }
}
