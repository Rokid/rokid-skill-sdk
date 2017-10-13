package com.rokid.skill.protocol;

import com.rokid.skill.protocol.response.Directive;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

/**
 * CloudApp 向客户端的返回结果
 *
 * @author lion.xys
 */
@Data
@SuppressWarnings("WeakerAccess")
public class RokidResponse {

  /**
   * Response协议的版本，必须由 CloudApp 填充。当前协议版本是 2.0.0
   */
  private String version = "2.0.0";

  private boolean startWithActiveWord = false;

  /**
   * 技能Id
   */
  private String appId;

  /**
   * 当前技能的session
   */
  private Session session = new Session();

  /**
   * 给 CloudAppClient 的Response内容
   */
  private Response response = new Response();

  /**
   * 当前技能的session
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Session {

    /**
     * 上下文信息
     */
    private LinkedHashMap<String, String> attributes;

  }

  /**
   * 返回给 CloudAppClient 的Response内容
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Response {

    private Action action = new Action();

    /**
     * card设置
     */
    private Card card;

    /**
     * 返回体类型
     */
    private String resType;

    /**
     * 当前请求的唯一ID
     */
    private String respId;

  }

  /**
   * 实际操作内容
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Action {

    /**
     * action 协议的版本，当前为 2.0.0
     */
    private String version = "2.0.0";

    /**
     * 当前action的类型：NORMAL 或 EXIT。
     */
    private ActionType type = ActionType.NORMAL;

    /**
     * 当前action的展现形式：scene、cut、service。该字段在技能创建时被确定，无法由cloud app更改。
     */
    private Form form = Form.cut;

    /**
     * 表明当此次返回的action执行完后 CloudAppClient 是否要退出，
     * 同时，当 shouldEndSession 为 true 时，CloudAppClient 将会忽略 EventRequests，
     * 即在action执行过程中不会产生 EventRequest
     */
    private boolean shouldEndSession = false;

    /**
     * 指令列表
     */
    private List<Directive> directives = new LinkedList<>();

  }

  /**
   * card参数相关
   */
  @Data
  public static class Card {

    /**
     * card类型
     */
    private CardType type;

    /**
     * card内容
     */
    private String content;

  }

  /**
   * 动作类型
   */
  @SuppressWarnings("unused")
  public enum ActionType {
    /**
     * voice 和 media 会同时执行；
     */
    NORMAL,
    /**
     * action会立即退出，并且在这种情况下，voice 和 media 将会被会被忽略
     */
    EXIT
  }

  /**
   * 是否需要场景化
   */
  @SuppressWarnings("unused")
  public enum Form {
    /**
     * scene的action会在被打断后压栈
     */
    scene,
    /**
     * cut的action会在被打断后直接结束
     */
    cut,
    /**
     * service会在后台执行，但没有任何界面
     */
    service
  }

  /**
   * Card类型
   */
  @SuppressWarnings("unused")
  public enum CardType {
    /**
     * 账户关联
     */
    ACCOUNT_LINK,
    /**
     * 聊天类型
     */
    CHAT
  }

}
