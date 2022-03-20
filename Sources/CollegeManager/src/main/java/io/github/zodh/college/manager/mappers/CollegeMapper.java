package io.github.zodh.college.manager.mappers;

import io.github.zodh.college.manager.model.entities.Subject;
import io.github.zodh.model.SubjectDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CollegeMapper {

  List<SubjectDTO> fromSubjectEntityListToSubjectDTOList(List<Subject> subjects);

}
