package io.github.zodh.college.manager.model.entities;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "code")
  private Long code;

  @Column(name = "description")
  private String courseDescription;

  @Column(name = "creator")
  private String user;

  @ReadOnlyProperty
  @Column(name = "creation_date")
  private OffsetDateTime creationDate;

  @Column(name = "last_update")
  private OffsetDateTime lastDateUpdated;

  @Column(name = "syllabus")
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "code")
  private List<Subject> subjects;
}
