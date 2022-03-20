package io.github.zodh.college.manager.api;

import io.github.zodh.api.SubjectsApi;
import io.github.zodh.college.manager.services.subjects.SubjectService;
import io.github.zodh.model.CreateSubjectRequest;
import io.github.zodh.model.CreateSubjectResponse;
import io.github.zodh.model.EditSubjectRequest;
import io.github.zodh.model.EditSubjectResponse;
import io.github.zodh.model.ListSubjectResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${default.request.mapping}")
public class SubjectsController implements SubjectsApi {

  @Autowired
  private SubjectService subjectService;

  @Override
  public ResponseEntity<CreateSubjectResponse> createSubject(String user,
      @Valid CreateSubjectRequest createSubjectRequest) {
    return new ResponseEntity<>(subjectService.createSubject(user, createSubjectRequest),
        HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<ListSubjectResponse> listSubjects(String user) {
    return new ResponseEntity<>(subjectService.listSubjects(user), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<EditSubjectResponse> updateSubject(String user,
      EditSubjectRequest editSubjectRequest) {
    return new ResponseEntity<>(subjectService.updateSubject(user, editSubjectRequest),
        HttpStatus.OK);
  }
}
