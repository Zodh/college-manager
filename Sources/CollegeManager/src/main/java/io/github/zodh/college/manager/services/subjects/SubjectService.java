package io.github.zodh.college.manager.services.subjects;

import io.github.zodh.model.CreateSubjectRequest;
import io.github.zodh.model.CreateSubjectResponse;
import io.github.zodh.model.EditSubjectRequest;
import io.github.zodh.model.EditSubjectResponse;
import io.github.zodh.model.ListSubjectResponse;

public interface SubjectService {

  CreateSubjectResponse createSubject(String user, CreateSubjectRequest createSubjectRequest);

  ListSubjectResponse listSubjects(String user);

  EditSubjectResponse updateSubject(String user, EditSubjectRequest editSubjectRequest);

}
