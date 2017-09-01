package com.rokid.skill.dispatcher;

import static com.rokid.skill.protocol.RokidConstant.EVENT_REQUEST;
import static com.rokid.skill.protocol.RokidConstant.INTENT_REQUEST;
import static com.rokid.skill.protocol.RokidConstant.Intent.ROKID_INTENT_EXIT;
import static com.rokid.skill.protocol.RokidConstant.Intent.ROKID_INTENT_WELCOME;
import static com.rokid.skill.protocol.RokidConstant.ROKID_UNKNOWN;

import com.rokid.skill.protocol.EventContent;
import com.rokid.skill.protocol.IntentContent;
import com.rokid.skill.protocol.RokidContext;
import com.rokid.skill.protocol.RokidRequest;

/**
 * rokid request listener
 *
 * @author lion.xys
 */
public class RokidRequestListener {

  private final RokidBus requestBus;

  private final UnknownRequestHandler unknownRequestHandler;

  private final RokidDefaultIntentHandler defaultIntentHandler;

  RokidRequestListener(RokidBus requestBus, RokidDefaultIntentHandler welcomeIntentHandler) {
    this.requestBus = requestBus;
    this.unknownRequestHandler = new DefaultUnknownRequestHandler();
    this.defaultIntentHandler = welcomeIntentHandler;
  }

  RokidRequestListener(RokidBus requestBus) {
    this.requestBus = requestBus;
    this.unknownRequestHandler = new DefaultUnknownRequestHandler();
    this.defaultIntentHandler = new DefaultWelcomeIntentHandler();
  }

  /**
   * handle rokid intent request
   *
   * @param request rokid intent request
   */
  @SuppressWarnings("unused")
  @RokidSubscribe(INTENT_REQUEST)
  public void receiveIntent(RokidRequest request) {

    requestBus.postIntent(ConvertUtil.convertFromRequst(request, IntentContent.class));

  }

  /**
   * handle rokid event request
   *
   * @param request rokid event request
   */
  @SuppressWarnings("unused")
  @RokidSubscribe(EVENT_REQUEST)
  public void receiveEvent(RokidRequest request) {

    requestBus.postEvent(ConvertUtil.convertFromRequst(request, EventContent.class));

  }

  /**
   * handle exception
   *
   * @param request rokid request
   */
  @SuppressWarnings("unused")
  @RokidSubscribe(ROKID_UNKNOWN)
  public void receiveUnknown(RokidRequest request) {

    unknownRequestHandler.handleUnknown(request);

  }

  /**
   * handle rokid welcome intent
   *
   * @param request rokid request
   */
  @SuppressWarnings("unused")
  @RokidSubscribe(ROKID_INTENT_WELCOME)
  public void receiveWelcomeIntent(RokidRequest<IntentContent> request) {

    defaultIntentHandler.handleWelcomeIntent(request);

  }

  /**
   * handle rokid exit intent
   *
   * @param request rokid request
   */
  @SuppressWarnings("unused")
  @RokidSubscribe(ROKID_INTENT_EXIT)
  public void receiveExitIntent(RokidRequest<IntentContent> request) {

    defaultIntentHandler.hadnleExitIntent(request);

  }

  static class DefaultUnknownRequestHandler implements UnknownRequestHandler {

    @Override
    public void handleUnknown(RokidRequest request) {

    }
  }

  static class DefaultWelcomeIntentHandler implements RokidDefaultIntentHandler {

    @Override
    public void handleWelcomeIntent(RokidRequest<IntentContent> request) {

      RokidContext.setVoice("欢迎回来");

    }

    @Override
    public void hadnleExitIntent(RokidRequest<IntentContent> request) {

      RokidContext.setVoice("下次再来哦");

    }
  }

}