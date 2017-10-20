package com.rokid.skill.protocol;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.rokid.skill.protocol.RokidResponse.Action;
import com.rokid.skill.protocol.RokidResponse.ActionType;
import com.rokid.skill.protocol.RokidResponse.Card;
import com.rokid.skill.protocol.RokidResponse.CardType;
import com.rokid.skill.protocol.RokidResponse.Form;
import com.rokid.skill.protocol.RokidResponse.Response;
import com.rokid.skill.protocol.RokidResponse.Session;
import com.rokid.skill.protocol.request.IntentContent;
import com.rokid.skill.protocol.response.Directive;
import com.rokid.skill.protocol.response.Directive.DirectiveType;
import com.rokid.skill.protocol.response.MediaDirective;
import com.rokid.skill.protocol.response.MediaDirective.MediaItem;
import com.rokid.skill.protocol.response.SoundDirective.ActionEnum;
import com.rokid.skill.protocol.response.VoiceDirective;
import com.rokid.skill.protocol.response.VoiceDirective.VoiceItem;
import java.util.LinkedHashMap;
import java.util.List;

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
   * @param attributes session 键值
   */
  public static void addSessionAttributes(LinkedHashMap<String, JsonElement> attributes) {

    RokidResponse.Session session = getRokidResponseSession();

    LinkedHashMap<String, JsonElement> attributesTmp = new LinkedHashMap<>();

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
    setVoice(ActionEnum.PLAY, tts);
  }

  /**
   * 设置语音信息
   *
   * @param action 该语音操作
   * @param tts tts信息
   */
  @SuppressWarnings("WeakerAccess")
  public static void setVoice(ActionEnum action, String tts) {

    VoiceItem item = new VoiceItem();

    item.setTts(tts);

    VoiceDirective voiceDirective = new VoiceDirective(item);

    voiceDirective.setAction(action);

    setVoice(voiceDirective);

  }

  /**
   * 设置语音信息
   *
   * @param directive tts指令
   */
  @SuppressWarnings("WeakerAccess")
  public static void setVoice(VoiceDirective directive) {

    List<Directive> directives = removeDirective(DirectiveType.voice);

    directives.add(directive);

    getResponse().getResponse().getAction().setDirectives(directives);

  }

  /**
   * 设置当前返回体里的媒体内容
   *
   * @param item 媒体详细内容
   */
  public static void setMedia(MediaItem item) {

    setMedia(ActionEnum.PLAY, item);

  }

  /**
   * 设置当前返回体里的媒体内容
   *
   * @param action 操作动作
   * @param item 媒体详细内容
   */
  @SuppressWarnings("WeakerAccess")
  public static void setMedia(ActionEnum action, MediaItem item) {

    MediaDirective media = new MediaDirective(item);

    media.setAction(action);

    setMedia(media);
  }

  /**
   * 设置当前返回体里的媒体内容
   *
   * @param media 媒体内容
   */
  @SuppressWarnings("WeakerAccess")
  public static void setMedia(MediaDirective media) {

    List<Directive> directives = removeDirective(DirectiveType.media);

    directives.add(media);

    getResponse().getResponse().getAction().setDirectives(directives);

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
   * 设置card
   *
   * @param type card类型
   */
  public static void setCard(CardType type, String content) {

    RokidResponse response = currentResponse.get();

    RokidResponse.Card card = new Card();

    card.setType(type);

    card.setContent(content);

    response.getResponse().setCard(card);

  }

  /**
   * 添加一个空的指令
   */
  public static void addNullDirective() {

    List<Directive> directives = removeDirective(DirectiveType.display);

    getResponse().getResponse().getAction().setDirectives(directives);

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
  public static LinkedHashMap<String, JsonElement> getSlots() {

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
  @SuppressWarnings("WeakerAccess")
  public static JsonElement getSlotObj(String key) {

    RokidRequest.Request request = getRequest().getRequest();

    String contentJson = GSON.toJson(request.getContent());

    IntentContent content = GSON.fromJson(contentJson, IntentContent.class);

    return content.getSlots().get(key);

  }

  /**
   * 根据key取得指定的slot的值
   *
   * @return 指定slot的值
   */
  public static String getSlot(String key) {

    JsonElement element = getSlotObj(key);

    if (element == null || element.isJsonNull() || !element.isJsonObject()
        || element.getAsJsonObject() == null) {
      return "";
    }

    return element.getAsJsonObject().get("value").getAsString();

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
   * 取得指令列表
   *
   * @return 指令列表
   */
  private static List<Directive> getRokidResponseDirective() {
    RokidResponse response = currentResponse.get();
    return response.getResponse().getAction().getDirectives();
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

  /**
   * 根据指令类型删除并取得指令列表
   *
   * @param type 指令类型
   * @return 指令列表
   */
  private static List<Directive> removeDirective(final DirectiveType type) {

    List<Directive> directives = getRokidResponseDirective();

    Optional<Directive> directiveOptional = Iterables
        .tryFind(directives, new Predicate<Directive>() {
          @Override
          public boolean apply(Directive input) {
            return input != null && input.getType() == type;
          }
        });

    if (directiveOptional.isPresent()) {

      Iterators.removeIf(directives.iterator(), new Predicate<Directive>() {
        @Override
        public boolean apply(Directive input) {
          return input != null && input.getType() == type;
        }
      });

    }

    return directives;

  }

}
