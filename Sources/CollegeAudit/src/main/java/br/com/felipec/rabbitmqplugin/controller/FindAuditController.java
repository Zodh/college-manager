package br.com.felipec.rabbitmqplugin.controller;

import br.com.felipec.rabbitmqplugin.model.CollegeAuditMessage;
import br.com.felipec.rabbitmqplugin.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindAuditController {

  @Autowired
  private MessageRepository messageRepository;

  @GetMapping(value = "/audits/{requestId}")
  public ResponseEntity<CollegeAuditMessage> getAuditMessage(@PathVariable String requestId) {
    return new ResponseEntity<>(messageRepository.findById(requestId), HttpStatus.OK);
  }

}
