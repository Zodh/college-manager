package br.com.felipec.rabbitmqplugin.model;

import javax.persistence.Column;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CollegeAuditMessage {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "message")
  private String message;

}
