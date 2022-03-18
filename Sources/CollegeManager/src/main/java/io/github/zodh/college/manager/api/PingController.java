package io.github.zodh.college.manager.api;

import io.github.zodh.api.PingApi;
import io.github.zodh.model.PingResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
public class PingController implements PingApi {

  @Override
  public ResponseEntity<PingResponse> checkServerStatus() {
    log.info("Server health status checked");
    return ResponseEntity.ok(new PingResponse().message("College Manager Server is available"));
  }
}
