package io.github.zodh.college.manager.model.repositories;

import io.github.zodh.college.manager.model.entities.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

  Optional<Course> findByCourseDescription(String courseDescription);

}
