package io.github.zodh.college.manager.services.courses;

import io.github.zodh.model.CreateCourseRequest;
import io.github.zodh.model.CreateCourseResponse;
import io.github.zodh.model.EditCourseRequest;
import io.github.zodh.model.EditCourseResponse;
import io.github.zodh.model.ListCourseResponse;

public interface CourseService {

  CreateCourseResponse createCourse(String user, CreateCourseRequest createCourseRequest);

  ListCourseResponse listCourses(String user);

  EditCourseResponse updateCourse(String user, EditCourseRequest editCourseRequest);

}
