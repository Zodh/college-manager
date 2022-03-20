package io.github.zodh.college.manager.model.repositories;

import io.github.zodh.college.manager.model.entities.Subject;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

  Optional<Subject> findBySubjectDescription(String subjectDescription);

}
