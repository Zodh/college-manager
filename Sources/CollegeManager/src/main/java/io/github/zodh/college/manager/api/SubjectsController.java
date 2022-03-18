package io.github.zodh.college.manager.api;

import io.github.zodh.api.SubjectsApi;
import io.github.zodh.model.CreateSubjectRequest;
import io.github.zodh.model.CreateSubjectResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${default.request.mapping}")
public class SubjectsController implements SubjectsApi {

  @Override
  public ResponseEntity<CreateSubjectResponse> createSubject(String user,
      CreateSubjectRequest createSubjectRequest) {
    return SubjectsApi.super.createSubject(user, createSubjectRequest);
  }
}
