package com.rokid.skill.dispatcher;

import com.rokid.skill.protocol.RokidRequest;

/**
 * UNKNOWN request handler
 *
 * @author lion.xys
 */
public interface UnknownRequestHandler {

  /**
   * handle unknown request
   *
   * @param request rokid request
   */
  void handleUnknown(RokidRequest request);

}
