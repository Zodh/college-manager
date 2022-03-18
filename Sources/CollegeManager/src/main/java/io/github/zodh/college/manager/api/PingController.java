package io.github.zodh.college.manager.api;

import io.github.zodh.api.PingApi;
import io.github.zodh.model.PingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${default.request.mapping}")
public class PingController implements PingApi {

  @Override
  public ResponseEntity<PingResponse> checkServerStatus() {
    return ResponseEntity.ok(new PingResponse().message("College Manager Server is available"));
  }
}
