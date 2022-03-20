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
import io.github.zodh.model.EditCourseRequest;
import io.github.zodh.model.EditCourseResponse;
import io.github.zodh.model.ErrorResponse;
import io.github.zodh.model.ListCourseResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
    } catch (Exception exception) {
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

  @Override
  public EditCourseResponse updateCourse(String user, EditCourseRequest editCourseRequest) {
    var requestId = generateRequestId(user);
    MDC.put("requestId", requestId);
    var editCourseResponse = new EditCourseResponse();
    var errorResponse = new ErrorResponse();
    try {
      generateLog("Starting edit course flow");
      generateLog("Updating fields");
      var course = isValidCourse(editCourseRequest.getCourseCode(), requestId);
      var updatedFields = getUpdatedFields(editCourseRequest);
      var syllabus = editCourseRequest.getSyllabus();
      var description = editCourseRequest.getCourseDescription();
      if (Objects.nonNull(syllabus)) {
        course.setSubjects(
            courseBuilder.addCourseSyllabus(editCourseRequest.getSyllabus(), requestId));
        course.setLastDateUpdated(OffsetDateTime.now());
      }
      if (courseRepository.findByCourseDescription(
          editCourseRequest.getCourseDescription()).isEmpty()) {
        if (StringUtils.isNotBlank(description)) {
          course.setCourseDescription(editCourseRequest.getCourseDescription());
          course.setLastDateUpdated(OffsetDateTime.now());
        }
      } else {
        updatedFields.remove("<courseDescription>");
      }
      courseRepository.save(course);
      editCourseResponse = new EditCourseResponse()
          .requestId(requestId)
          .updatedFields(updatedFields);
      return editCourseResponse;
    } catch (Exception exception) {
      var message = String.format(
          "Error trying to edit course | Error: %s",
          exception.getMessage()
      );
      log.error("{}", message);
      var flowException = FlowException
          .builder()
          .requestId(requestId)
          .message(message)
          .errorDescription("Error trying to edit course, try again later.")
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
      errorResponse = errorMapper.fromFlowExceptionToErrorResponse(flowException);
      throw flowException;
    } finally {
      generateLog("Finishing edit course flow");
    }
  }

  private ArrayList<String> getUpdatedFields(EditCourseRequest editCourseRequest) {
    var updatedFields = new ArrayList<String>();
    if (StringUtils.isNotBlank(editCourseRequest.getCourseDescription())) {
      updatedFields.add("<courseDescription>");
    }
    if (Objects.nonNull(editCourseRequest.getSyllabus())) {
      updatedFields.add("<syllabus>");
    }
    return updatedFields;
  }

  private Course isValidCourse(Long courseCode, String requestId) {
    var course = courseRepository.findById(courseCode);
    if (course.isEmpty()) {
      throw FlowException
          .builder()
          .requestId(requestId)
          .message("Course code not found for code " + courseCode)
          .errorDescription(String.format("A course with code %s does not exists.", courseCode))
          .httpStatus(HttpStatus.NOT_FOUND)
          .build();
    }
    return course.get();
  }

  private void generateLog(String message) {
    log.log(Level.toLevel(DEFAULT_LOG_LEVEL), message);
  }
}
