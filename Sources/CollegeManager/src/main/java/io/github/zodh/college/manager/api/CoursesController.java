package io.github.zodh.college.manager.api;

import io.github.zodh.api.CoursesApi;
import io.github.zodh.college.manager.services.courses.CourseService;
import io.github.zodh.model.CreateCourseRequest;
import io.github.zodh.model.CreateCourseResponse;
import io.github.zodh.model.EditCourseRequest;
import io.github.zodh.model.EditCourseResponse;
import io.github.zodh.model.ListCourseResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${default.request.mapping}")
public class CoursesController implements CoursesApi {

  @Autowired
  private CourseService courseService;

  @Override
  public ResponseEntity<CreateCourseResponse> createCourse(@RequestHeader String user,
      @Valid @RequestBody CreateCourseRequest createCourseRequest) {
    return new ResponseEntity<>(courseService.createCourse(user, createCourseRequest),
        HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<ListCourseResponse> listCourses(String user) {
    return new ResponseEntity<>(courseService.listCourses(user), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<EditCourseResponse> updateCourse(String user,
      EditCourseRequest editCourseRequest) {
    return new ResponseEntity<>(courseService.updateCourse(user, editCourseRequest), HttpStatus.OK);
  }
}
