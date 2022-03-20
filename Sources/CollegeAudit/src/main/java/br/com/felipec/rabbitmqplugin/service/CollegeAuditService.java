package br.com.felipec.rabbitmqplugin.service;

import br.com.felipec.rabbitmqplugin.model.CollegeAuditMessage;
import br.com.felipec.rabbitmqplugin.repository.MessageRepository;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CollegeAuditService {

  @Autowired
  private MessageRepository messageRepository;

  public void saveMessage(String message) throws JSONException {
    var collegeAuditMessage = new CollegeAuditMessage();
    var jsonObject = new JSONObject(message);
    var requestId = jsonObject.getString("requestId");
    collegeAuditMessage.setId(requestId);
    collegeAuditMessage.setMessage(message);
    messageRepository.save(collegeAuditMessage);
    log.info("Object saved - tracked by request id: " + requestId);
  }
}
