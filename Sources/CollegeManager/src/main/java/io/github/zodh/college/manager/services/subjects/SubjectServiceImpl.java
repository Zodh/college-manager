package io.github.zodh.college.manager.services.subjects;

import static io.github.zodh.college.manager.utils.Generics.toJson;
import static io.github.zodh.college.manager.utils.RandomGenerator.generateRequestId;

import io.github.zodh.college.manager.builders.SubjectBuilder;
import io.github.zodh.college.manager.configuration.CollegeMessagerConfiguration;
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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.slf4j.MDC;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SubjectServiceImpl implements SubjectService {

  private static final String REQUEST_ID = "requestId";
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

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Override
  public CreateSubjectResponse createSubject(String user,
      CreateSubjectRequest createSubjectRequest) {
    var requestId = generateRequestId(user);
    MDC.put(REQUEST_ID, requestId);
    var createSubjectResponse = new CreateSubjectResponse();
    var errorResponse = new ErrorResponse();
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
      var response = (Objects.nonNull(errorResponse.getRequestId()))
          ? errorResponse
          : createSubjectResponse;
      amqpTemplate.convertAndSend(CollegeMessagerConfiguration.CALLER_PLUGIN_QUEUE,
          toJson(response));
      generateLog("Finishing create subject flow");
      MDC.clear();
    }
  }

  @Override
  public ListSubjectResponse listSubjects(String user) {
    var requestId = generateRequestId(user);
    MDC.put(REQUEST_ID, requestId);
    var listSubjectResponse = new ListSubjectResponse();
    var errorResponse = new ErrorResponse();
    try {
      generateLog("Starting list subjects flow");
      generateLog("Listing subjects");
      var subjects = subjectRepository.findAll();
      listSubjectResponse = new ListSubjectResponse()
          .requestId(requestId)
          .subjects(collegeMapper.fromSubjectEntityListToSubjectDTOList(subjects));
      return listSubjectResponse;
    } catch (Exception exception) {
      var message = String.format(
          "Error trying to list subjects | Error: %s",
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to list subjects, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      var response = (Objects.nonNull(errorResponse.getRequestId()))
          ? errorResponse
          : listSubjectResponse;
      amqpTemplate.convertAndSend(CollegeMessagerConfiguration.CALLER_PLUGIN_QUEUE,
          toJson(response));
      generateLog("Finishing list subjects flow");
    }
  }

  @Override
  public EditSubjectResponse updateSubject(String user, EditSubjectRequest editSubjectRequest) {
    var requestId = generateRequestId(user);
    MDC.put(REQUEST_ID, requestId);
    var editSubjectResponse = new EditSubjectResponse();
    var errorResponse = new ErrorResponse();
    try {
      generateLog("Starting edit subject flow");
      generateLog("Updating fields");
      var subject = isValidSubject(editSubjectRequest.getSubjectCode(), requestId);
      var updatedFields = getUpdatedFields(editSubjectRequest);
      var description = editSubjectRequest.getSubjectDescription();
      if (subjectRepository.findBySubjectDescription(
          editSubjectRequest.getSubjectDescription()).isEmpty()) {
        if (StringUtils.isNotBlank(description)) {
          subject.setSubjectDescription(editSubjectRequest.getSubjectDescription());
          subject.setLastDateUpdated(OffsetDateTime.now());
        }
      } else {
        updatedFields.remove("<subjectDescription>");
      }
      if (Objects.nonNull(editSubjectRequest.getStudentQuantity())) {
        subject.setStudentQuantity(editSubjectRequest.getStudentQuantity());
        subject.setLastDateUpdated(OffsetDateTime.now());
      }
      var checkedSubjectAndUpdatedFields = verifySubjectWorkload(subject, editSubjectRequest,
          updatedFields);
      subject = (Subject) checkedSubjectAndUpdatedFields.get(0);
      updatedFields = (ArrayList<String>) checkedSubjectAndUpdatedFields.get(1);
      if (updatedFields.isEmpty()) {
        updatedFields = null;
      }
      subjectRepository.save(subject);
      editSubjectResponse = new EditSubjectResponse()
          .requestId(requestId)
          .updatedFields(updatedFields);
      return editSubjectResponse;
    } catch (Exception exception) {
      var message = String.format(
          "Error trying to edit subject | Error: %s",
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to edit subject, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      var response = (Objects.nonNull(errorResponse.getRequestId()))
          ? errorResponse
          : editSubjectResponse;
      amqpTemplate.convertAndSend(CollegeMessagerConfiguration.CALLER_PLUGIN_QUEUE,
          toJson(response));
      generateLog("Finishing edit subject flow");
    }
  }

  private ArrayList<String> getUpdatedFields(EditSubjectRequest editSubjectRequest) {
    var updatedFields = new ArrayList<String>();
    if (StringUtils.isNotBlank(editSubjectRequest.getSubjectDescription())) {
      updatedFields.add("<subjectDescription>");
    }
    if (Objects.nonNull(editSubjectRequest.getPracticalWorkload())) {
      updatedFields.add("<practicalWorkload>");
    }
    if (Objects.nonNull(editSubjectRequest.getTheoreticalWorkload())) {
      updatedFields.add("<theoreticalWorkload>");
    }
    if (Objects.nonNull(editSubjectRequest.getTotalWorkload())) {
      updatedFields.add("<totalWorkload>");
    }
    if (Objects.nonNull(editSubjectRequest.getStudentQuantity())) {
      updatedFields.add("<studentQuantity>");
    }
    return updatedFields;
  }

  private List<Object> verifySubjectWorkload(Subject subject, EditSubjectRequest editSubjectRequest,
      List<String> updatedFields) {
    if (Objects.nonNull(editSubjectRequest.getPracticalWorkload())
        || Objects.nonNull(editSubjectRequest.getTheoreticalWorkload())
        || Objects.nonNull(editSubjectRequest.getTotalWorkload())) {
      var totalWorkload = (Objects.isNull(editSubjectRequest.getTotalWorkload()))
          ? subject.getTotalWorkload()
          : editSubjectRequest.getTotalWorkload();
      if (totalWorkload !=
          (editSubjectRequest.getPracticalWorkload()
              + editSubjectRequest.getTheoreticalWorkload())) {
        updatedFields.remove("<practicalWorkload>");
        updatedFields.remove("<theoreticalWorkload>");
        updatedFields.remove("<totalWorkload>");
      } else {
        if (Objects.nonNull(editSubjectRequest.getPracticalWorkload())) {
          subject.setPracticalWorkload(editSubjectRequest.getPracticalWorkload());
        }
        if (Objects.nonNull(editSubjectRequest.getTheoreticalWorkload())) {
          subject.setTheoreticalWorkload(editSubjectRequest.getTheoreticalWorkload());
        }
        if (Objects.nonNull(editSubjectRequest.getTotalWorkload())) {
          subject.setTotalWorkload(editSubjectRequest.getTotalWorkload());
        } else {
          updatedFields.remove("<totalWorkload>");
        }
      }
    }
    return List.of(subject, updatedFields);
  }

  private Subject isValidSubject(Long subjectCode, String requestId) {
    var subject = subjectRepository.findById(subjectCode);
    if (subject.isEmpty()) {
      throw FlowException
          .builder()
          .requestId(requestId)
          .message("Subject not found for code " + subjectCode)
          .errorDescription(String.format("A subject with code %s does not exists.", subjectCode))
          .httpStatus(HttpStatus.NOT_FOUND)
          .build();
    }
    return subject.get();
  }

  private void generateLog(String message) {
    log.log(Level.toLevel(DEFAULT_LOG_LEVEL), message);
  }
}
