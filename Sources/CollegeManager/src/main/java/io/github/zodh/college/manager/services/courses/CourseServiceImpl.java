package io.github.zodh.college.manager.services.courses;

import static io.github.zodh.college.manager.utils.RandomGenerator.generateRequestId;

import io.github.zodh.college.manager.builders.CourseBuilder;
import io.github.zodh.college.manager.exceptions.FlowException;
import io.github.zodh.college.manager.mappers.CollegeMapper;
import io.github.zodh.college.manager.mappers.ErrorMapper;
import io.github.zodh.college.manager.model.entities.Course;
import io.github.zodh.college.manager.model.repositories.CourseRepository;
import io.github.zodh.model.CreateCourseRequest;
import io.github.zodh.model.CreateCourseResponse;
import io.github.zodh.model.ErrorResponse;
import io.github.zodh.model.ListCourseResponse;
import io.github.zodh.model.ListSubjectResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CourseServiceImpl implements CourseService {

  @Value("${log.level}")
  private String DEFAULT_LOG_LEVEL;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private CourseBuilder courseBuilder;

  @Autowired
  private CollegeMapper collegeMapper;

  @Autowired
  private ErrorMapper errorMapper;

  @Override
  public CreateCourseResponse createCourse(String user, CreateCourseRequest createCourseRequest) {
    var requestId = generateRequestId(user);
    MDC.put("requestId", requestId);
    var createCourseResponse = new CreateCourseResponse();
    var errorResponse = new ErrorResponse();
    try {
      generateLog("Starting create course flow");
      var course = courseRepository.save(
          (Course) courseBuilder.build(
              createCourseRequest,
              user,
              requestId
          )
      );
      createCourseResponse = new CreateCourseResponse()
          .courseCode(course.getCode())
          .requestId(requestId);
      return createCourseResponse;
    } catch (FlowException flowException) {
      log.error("Message: {} | Description: {}",
          flowException.getMessage(),
          flowException.getErrorDescription());
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } catch (Exception exception) {
      var message = String.format(
          "Error trying to save course %s | Error: %s",
          createCourseRequest.getCourseDescription(),
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to save course, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      generateLog("Generating audit data");
      generateLog(errorResponse.toString());
      generateLog(createCourseResponse.toString());
      MDC.clear();
    }
  }

  @Override
  public ListCourseResponse listCourses(String user) {
    var requestId = generateRequestId(user);
    MDC.put("requestId", requestId);
    var listCourseResponse = new ListCourseResponse();
    var errorResponse = new ErrorResponse();
    try {
      generateLog("Starting list courses flow");
      generateLog("Listing courses");
      var courses = courseRepository.findAll();
      listCourseResponse = new ListCourseResponse()
          .requestId(requestId)
          .courses(collegeMapper.fromCourseEntityListToCourseDTOList(courses));
      return listCourseResponse;
    } catch (Exception exception){
      var message = String.format(
          "Error trying to list courses | Error: %s",
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to list courses, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      generateLog("Finishing list courses flow");
    }
  }

  private void generateLog(String message) {
    log.log(Level.toLevel(DEFAULT_LOG_LEVEL), message);
  }
}
