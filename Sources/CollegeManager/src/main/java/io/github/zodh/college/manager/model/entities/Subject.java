package io.github.zodh.college.manager.model.entities;

import java.time.OffsetDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subjects")
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "code")
  private Long code;

  @Column(name = "description")
  private String subjectDescription;

  @Column(name = "total_workload")
  private Integer totalWorkload;

  @Column(name = "practical_workload")
  private Integer practicalWorkload;

  @Column(name = "theoretical_workload")
  private Integer theoreticalWorkload;

  @Column(name = "students")
  private Long studentQuantity;

  @Column(name = "creator")
  private String user;

  @ReadOnlyProperty
  @Column(name = "creation_date")
  private OffsetDateTime creationDate;

  @Column(name = "last_update")
  private OffsetDateTime lastDateUpdated;
}
