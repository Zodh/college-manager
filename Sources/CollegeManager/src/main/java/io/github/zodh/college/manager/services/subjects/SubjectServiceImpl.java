package io.github.zodh.college.manager.services.subjects;

import static io.github.zodh.college.manager.utils.RandomGenerator.generateRequestId;

import io.github.zodh.college.manager.builders.SubjectBuilder;
import io.github.zodh.college.manager.exceptions.FlowException;
import io.github.zodh.college.manager.mappers.CollegeMapper;
import io.github.zodh.college.manager.mappers.ErrorMapper;
import io.github.zodh.college.manager.model.entities.Subject;
import io.github.zodh.college.manager.model.repositories.SubjectRepository;
import io.github.zodh.model.CreateSubjectRequest;
import io.github.zodh.model.CreateSubjectResponse;
import io.github.zodh.model.EditSubjectRequest;
import io.github.zodh.model.EditSubjectResponse;
import io.github.zodh.model.ErrorResponse;
import io.github.zodh.model.ListSubjectResponse;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SubjectServiceImpl implements SubjectService {

  @Value("${log.level}")
  private String DEFAULT_LOG_LEVEL;

  @Autowired
  private SubjectRepository subjectRepository;

  @Autowired
  private SubjectBuilder subjectBuilder;

  @Autowired
  private ErrorMapper errorMapper;

  @Autowired
  private CollegeMapper collegeMapper;

  private ErrorResponse errorResponse;

  @Override
  public CreateSubjectResponse createSubject(String user,
      CreateSubjectRequest createSubjectRequest) {
    var requestId = generateRequestId(user);
    MDC.put("requestId", requestId);
    var createSubjectResponse = new CreateSubjectResponse();
    try {
      generateLog("Starting create subject flow");
      var subject = subjectRepository.save(
          (Subject) subjectBuilder.build(
              createSubjectRequest,
              user,
              requestId
          )
      );
      createSubjectResponse = new CreateSubjectResponse()
          .subjectCode(subject.getCode())
          .requestId(requestId);
      return createSubjectResponse;
    } catch (FlowException flowException) {
      log.error("Message: {} | Description: {}",
          flowException.getMessage(),
          flowException.getErrorDescription());
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } catch (Exception exception) {
      var message = String.format(
          "Error trying to save subject %s | Error: %s",
          createSubjectRequest.getSubjectDescription(),
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to save subject, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      generateLog("Finishing create subject flow");
      MDC.clear();
    }
  }

  @Override
  public ListSubjectResponse listSubjects(String user) {
    var requestId = generateRequestId(user);
    MDC.put("requestId", requestId);
    try {
      generateLog("Starting list subjects flow");
      generateLog("Listing subjects");
      var subjects = subjectRepository.findAll();
      return new ListSubjectResponse()
          .requestId(requestId)
          .subjects(collegeMapper.fromSubjectEntityListToSubjectDTOList(subjects));
    } catch (Exception exception){
      var message = String.format(
          "Error trying to list subjects | Error: %s",
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to save subject, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      generateLog("Finishing list subjects flow");
    }
  }

  @Override
  public EditSubjectResponse updateSubject(String user, EditSubjectRequest editSubjectRequest){
    return null;
  }

  private void generateLog(String message) {
    log.log(Level.toLevel(DEFAULT_LOG_LEVEL), message);
  }
}
