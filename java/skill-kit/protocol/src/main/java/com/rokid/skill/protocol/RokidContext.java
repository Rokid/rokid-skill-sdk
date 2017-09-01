package com.rokid.skill.protocol;

import com.google.gson.Gson;
import com.rokid.skill.protocol.RokidResponse.Action;
import com.rokid.skill.protocol.RokidResponse.ActionEnum;
import com.rokid.skill.protocol.RokidResponse.ActionType;
import com.rokid.skill.protocol.RokidResponse.Card;
import com.rokid.skill.protocol.RokidResponse.CardType;
import com.rokid.skill.protocol.RokidResponse.Form;
import com.rokid.skill.protocol.RokidResponse.Media;
import com.rokid.skill.protocol.RokidResponse.MediaItem;
import com.rokid.skill.protocol.RokidResponse.Response;
import com.rokid.skill.protocol.RokidResponse.Session;
import com.rokid.skill.protocol.RokidResponse.Voice;
import com.rokid.skill.protocol.RokidResponse.VoiceItem;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lion.xys
 */
@SuppressWarnings("unused")
public class RokidContext {

  private static final Gson GSON = new Gson();

  private static final ThreadLocal<RokidRequest> currentRequest = new ThreadLocal<>();

  private static final ThreadLocal<RokidResponse> currentResponse = new ThreadLocal<>();

  /**
   * 取得当前请求体内容
   *
   * @return 当前请求体内容
   */
  public static RokidRequest getRequest() {
    return currentRequest.get();
  }

  /**
   * 将请求内容添加到请求体容器
   *
   * @param request 请求
   */
  public static void add(RokidRequest request) {

    currentRequest.set(request);

    RokidResponse response = new RokidResponse();
    response.setAppId(request.getContext().getApplication().getApplicationId());
    RokidResponse.Response responseInner = new Response();
    responseInner.setRespId(request.getRequest().getReqId());
    responseInner.setResType(request.getRequest().getReqType());
    response.setResponse(responseInner);

    currentResponse.set(response);

  }

  /**
   * 取得当前请求结果
   *
   * @return 当前请求结果
   */
  public static RokidResponse getResponse() {

    return currentResponse.get();

  }

  /**
   * 当前请求体里的session是否为新的session
   *
   * @return true ：是 / false : 不是
   */
  public static boolean isNewSession() {

    RokidRequest.Session session = getRokidRequestSession();

    return session.isNewSession();

  }

  /**
   * 清空上下文
   */
  public static void clearContext() {

    currentRequest.remove();

    currentResponse.remove();

  }

  /**
   * 添加session值
   *
   * @param key session 键
   * @param value session 值
   */
  public static void addSessionAttributes(String key, String value) {

    RokidResponse.Session session = getRokidResponseSession();

    LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

    attributes.put(key, value);

    session.setAttributes(attributes);

  }

  /**
   * 添加session值
   *
   * @param attributes session 键值
   */
  public static void addSessionAttributes(Map<String, String> attributes) {

    RokidResponse.Session session = getRokidResponseSession();

    LinkedHashMap<String, String> attributesTmp = new LinkedHashMap<>();

    attributesTmp.putAll(attributes);

    session.setAttributes(attributesTmp);

  }

  /**
   * 设置返回体操作类型
   *
   * @param type 返回体操作类型
   */
  public static void setActionType(ActionType type) {

    Action action = getRokidResponseAction();

    action.setType(type);

  }

  /**
   * 设置返回体场景化信息
   *
   * @param form 场景化信息
   */
  public static void setForm(Form form) {

    Action action = getRokidResponseAction();

    action.setForm(form);

  }

  /**
   * 设置返回体场景化信息
   *
   * @param shouldEndSession 是否结束session
   */
  public static void setShouldEndSession(boolean shouldEndSession) {

    Action action = getRokidResponseAction();

    action.setShouldEndSession(shouldEndSession);

  }

  /**
   * 设置语音信息的tts
   *
   * @param tts tts信息
   */
  public static void setVoice(String tts) {

    Voice voice = getRokidResponseVoice();

    VoiceItem item = voice.getItem();

    item.setTts(tts);

  }

  /**
   * 设置语音信息
   *
   * @param action 该语音操作
   * @param tts tts信息
   */
  public static void setVoice(ActionEnum action, String tts) {

    Voice voice = getRokidResponseVoice();

    voice.setAction(action);

    VoiceItem item = voice.getItem();

    item.setTts(tts);

  }

  /**
   * 设置语音信息
   *
   * @param voiceInput 语音配置信息
   */
  public static void setVoice(Voice voiceInput) {

    Action action = getRokidResponseAction();

    action.setVoice(voiceInput);

  }

  /**
   * 设置当前返回体里的媒体内容
   *
   * @param item 媒体详细内容
   */
  public static void setMedia(MediaItem item) {

    Media media = getRokidResponseMedia();

    media.setItem(item);

  }

  /**
   * 设置当前返回体里的媒体内容
   *
   * @param action 操作动作
   * @param item 媒体详细内容
   */
  public static void setMedia(ActionEnum action, MediaItem item) {

    Media media = getRokidResponseMedia();

    media.setAction(action);

    media.setItem(item);

  }

  /**
   * 设置当前返回体里的媒体内容
   *
   * @param media 媒体内容
   */
  public static void setMedia(Media media) {

    Action action = getRokidResponseAction();

    action.setMedia(media);

  }

  /**
   * 设置card
   *
   * @param type card类型
   */
  public static void setCard(CardType type) {

    RokidResponse response = currentResponse.get();

    RokidResponse.Card card = new Card();

    card.setType(type);

    response.getResponse().setCard(card);

  }

  /**
   * 从{@link RokidRequest}取得账户关联Id
   *
   * @return 账户关联Id
   */
  public static String getAccountLinkedId() {

    RokidRequest.UserInfo userInfo = getRequest().getContext().getUser();

    return userInfo.getAccountLinkedId();

  }

  /**
   * 取得命中的slots
   *
   * @return slots
   */
  public static LinkedHashMap<String, String> getSlots(){

    RokidRequest.Request request = getRequest().getRequest();

    String contentJson = GSON.toJson(request.getContent());

    IntentContent content = GSON.fromJson(contentJson, IntentContent.class);

    return content.getSlots();

  }

  /**
   * 根据key取得指定的slot的值
   *
   * @return 指定slot的值
   */
  public static String getSlot(String key){

    RokidRequest.Request request = getRequest().getRequest();

    String contentJson = GSON.toJson(request.getContent());

    IntentContent content = GSON.fromJson(contentJson, IntentContent.class);

    return content.getSlots().get(key);

  }

  /**
   * 取得当前返回体里的session
   *
   * @return session
   */
  private static Session getRokidResponseSession() {

    RokidResponse response = currentResponse.get();

    RokidResponse.Session session = response.getSession();

    if (session == null) {

      session = new RokidResponse.Session();

      response.setSession(session);

    }

    return session;

  }

  /**
   * 取得当前返回体里的返回操作内容
   *
   * @return 返回操作内容
   */
  private static Action getRokidResponseAction() {

    RokidResponse response = currentResponse.get();

    return response.getResponse().getAction();

  }

  /**
   * 取得当前返回体里的语音相关配置
   *
   * @return 语音相关配置
   */
  private static Voice getRokidResponseVoice() {

    RokidResponse response = currentResponse.get();

    Voice voice = response.getResponse().getAction().getVoice();

    if (voice == null) {

      voice = new Voice();

      response.getResponse().getAction().setVoice(voice);

    }

    return voice;

  }

  /**
   * 取得当前返回体里的媒体相关配置
   *
   * @return 媒体相关配置
   */
  private static Media getRokidResponseMedia() {

    RokidResponse response = currentResponse.get();

    Media media = response.getResponse().getAction().getMedia();

    if (media == null) {

      media = new Media();

      response.getResponse().getAction().setMedia(media);

    }

    return media;

  }

  /**
   * 取得当前请求体里的session
   *
   * @return session
   */
  private static RokidRequest.Session getRokidRequestSession() {

    RokidRequest request = currentRequest.get();

    return request.getSession();
  }

}
