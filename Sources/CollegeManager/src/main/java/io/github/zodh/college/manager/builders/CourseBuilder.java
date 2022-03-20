package io.github.zodh.college.manager.builders;

import io.github.zodh.college.manager.exceptions.FlowException;
import io.github.zodh.college.manager.model.entities.Course;
import io.github.zodh.college.manager.model.entities.Subject;
import io.github.zodh.college.manager.model.repositories.CourseRepository;
import io.github.zodh.college.manager.model.repositories.SubjectRepository;
import io.github.zodh.model.CreateCourseRequest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CourseBuilder extends AbstractBuilder {

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private SubjectRepository subjectRepository;

  @Override
  public Object build(Object request, String user, String requestId) {
    var createCourseRequest = (CreateCourseRequest) request;
    if (isValidRequest(request)) {
      var subjects = new ArrayList<Subject>();
      if (Objects.nonNull(createCourseRequest.getSyllabus())) {
        subjects = addCourseSyllabus(createCourseRequest.getSyllabus(), requestId);
      }
      return new Course(
          null,
          createCourseRequest.getCourseDescription(),
          user,
          OffsetDateTime.now(),
          OffsetDateTime.now(),
          subjects
      );
    }
    throw FlowException
        .builder()
        .requestId(requestId)
        .errorDescription("A course with this description already exists!")
        .httpStatus(HttpStatus.BAD_REQUEST)
        .build();
  }

  @Override
  boolean isValidRequest(Object request) {
    log.info("Validating course request");
    return courseRepository.findByCourseDescription(
        ((CreateCourseRequest) request).getCourseDescription()).isEmpty();
  }

  private ArrayList<Subject> addCourseSyllabus(List<Long> subjectsCodes, String requestId) {
    var subjects = new ArrayList<Subject>();
    subjectsCodes.forEach(code -> {
      var subject = subjectRepository.findById(code);
      if (subject.isPresent()) {
        subjects.add(subject.get());
      } else {
        throw FlowException
            .builder()
            .requestId(requestId)
            .errorDescription(String.format("A subject with code %s does not exists!", code))
            .httpStatus(HttpStatus.BAD_REQUEST)
            .build();
      }
    });
    return subjects;
  }
}
