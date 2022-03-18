package io.github.zodh.college.manager.api;

import io.github.zodh.api.CoursesApi;
import io.github.zodh.model.CreateCourseRequest;
import io.github.zodh.model.CreateCourseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CoursesController implements CoursesApi {

  @Override
  public ResponseEntity<CreateCourseResponse> createCourse(String user,
      CreateCourseRequest createCourseRequest) {
    return CoursesApi.super.createCourse(user, createCourseRequest);
  }
}
