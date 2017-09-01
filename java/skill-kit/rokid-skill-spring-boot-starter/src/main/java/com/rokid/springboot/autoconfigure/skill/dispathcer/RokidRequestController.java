package com.rokid.springboot.autoconfigure.skill.dispathcer;

import com.rokid.skill.dispatcher.RokidBus;
import com.rokid.skill.protocol.RokidRequest;
import com.rokid.skill.protocol.RokidResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lion.xys
 */
@RestController
public class RokidRequestController {

  private final RokidBus requestBus;

  @Autowired
  public RokidRequestController(RokidBus requestBus) {
    this.requestBus = requestBus;
  }

  /**
   * recieve rokid open platform's request
   *
   * @param request rokid request
   * @return rokid response
   */
  @PostMapping
  public RokidResponse receiveIntentRequest(@RequestBody RokidRequest request) {

    return requestBus.post(request);
  }

}
