package br.com.felipec.rabbitmqplugin.service;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Listener {

  @Autowired
  CollegeAuditService collegeAuditService;

  @Autowired
  AmqpTemplate template;

  @RabbitListener(queues = "Caller")
  private void listen(String message) {
    try {
      collegeAuditService.saveMessage(message);
    } catch (Exception exception){
     log.error("Exception: " + exception.getMessage());
    }
  }

}
