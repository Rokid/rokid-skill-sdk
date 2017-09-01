package com.rokid.skill.dispatcher;

import com.rokid.skill.protocol.IntentContent;
import com.rokid.skill.protocol.RokidRequest;

/**
 * welcome intent handler
 *
 * @author lion.xys
 */
public interface RokidDefaultIntentHandler {

  /**
   * handle welcome intent
   *
   * @param request rokid request
   */
  void handleWelcomeIntent(RokidRequest<IntentContent> request);

  /**
   * handle exit intent
   *
   * @param request rokid request
   */
  void hadnleExitIntent(RokidRequest<IntentContent> request);

}
