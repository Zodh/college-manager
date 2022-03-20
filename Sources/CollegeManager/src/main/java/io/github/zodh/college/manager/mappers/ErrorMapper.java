package io.github.zodh.college.manager.mappers;

import io.github.zodh.college.manager.exceptions.FlowException;
import io.github.zodh.model.ErrorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ErrorMapper {

  @Mapping(source = "flowException.requestId", target = "requestId")
  @Mapping(source = "flowException.errorDescription", target = "errorDescription")
  ErrorResponse fromFlowExceptionToErrorResponse(FlowException flowException);

}
