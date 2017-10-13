package com.rokid.skill.dispatcher;

import com.rokid.skill.protocol.request.EventContent;
import com.rokid.skill.protocol.request.IntentContent;
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
  void handleExitIntent(RokidRequest<IntentContent> request);

  /**
   * handle voice started event
   *
   * @param request rokid request
   */
  void handleVoiceStarted(RokidRequest<EventContent> request);

  /**
   * handle voice finished event
   *
   * @param request rokid request
   */
  void handleVoiceFinished(RokidRequest<EventContent> request);

}
