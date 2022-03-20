package io.github.zodh.college.manager.mappers;

import io.github.zodh.college.manager.model.entities.Course;
import io.github.zodh.college.manager.model.entities.Subject;
import io.github.zodh.model.CourseDTO;
import io.github.zodh.model.SubjectDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CollegeMapper {

  List<SubjectDTO> fromSubjectEntityListToSubjectDTOList(List<Subject> subjects);

  @Mapping(source = "courses.subjects", target = "subjects", qualifiedByName = "mapSubjects")
  List<CourseDTO> fromCourseEntityListToCourseDTOList(List<Course> courses);

  @Named("mapSubjects")
  default List<SubjectDTO> mapSubjects(List<Subject> subjects){
    return fromSubjectEntityListToSubjectDTOList(subjects);
  }

}
