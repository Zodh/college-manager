package io.github.zodh.college.manager.builders;

import io.github.zodh.college.manager.exceptions.FlowException;
import io.github.zodh.college.manager.model.entities.Subject;
import io.github.zodh.college.manager.model.repositories.SubjectRepository;
import io.github.zodh.model.CreateSubjectRequest;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SubjectBuilder extends AbstractBuilder {

  @Autowired
  private SubjectRepository subjectRepository;

  @Override
  public Object build(Object request, String user, String requestId) {
    var createSubjectRequest = (CreateSubjectRequest) request;
    checkIfWorkloadIsValid(createSubjectRequest, requestId);
    if (isValidRequest(request)) {
      return new Subject(
          null,
          createSubjectRequest.getSubjectDescription(),
          createSubjectRequest.getTotalWorkload(),
          createSubjectRequest.getPracticalWorkload(),
          createSubjectRequest.getTheoreticalWorkload(),
          createSubjectRequest.getStudentQuantity(),
          user,
          new Date(),
          new Date()
      );
    }
    throw FlowException
        .builder()
        .requestId(requestId)
        .errorDescription("A subject with this description already exists!")
        .httpStatus(HttpStatus.BAD_REQUEST)
        .build();
  }

  @Override
  boolean isValidRequest(Object request) {
    log.info("Validating subject request");
    return subjectRepository.findBySubjectDescription(
        ((CreateSubjectRequest) request).getSubjectDescription()).isEmpty();
  }

  private void checkIfWorkloadIsValid(CreateSubjectRequest createSubjectRequest,
      String requestId) {
    var totalSubjectWorkload = createSubjectRequest.getTotalWorkload();
    if (totalSubjectWorkload
        != (createSubjectRequest.getPracticalWorkload()
        + createSubjectRequest.getTheoreticalWorkload())) {
      throw FlowException
          .builder()
          .requestId(requestId)
          .errorDescription("Subject workload is invalid! totalWorkload may be (practicalWorkload"
              + " + theoreticalWorkload)")
          .httpStatus(HttpStatus.BAD_REQUEST)
          .build();
    }
  }
}
