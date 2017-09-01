package com.rokid.skill.protocol;

/**
 * 若琪事件相关
 *
 * @author lion.xys
 */
@SuppressWarnings("unused")
public interface RokidConstant {

  /**
   * intentRequest
   */
  String INTENT_REQUEST = "INTENT";

  /**
   * eventRequest
   */
  String EVENT_REQUEST = "EVENT";

  /**
   * 未知请求类型
   */
  String ROKID_UNKNOWN = "ROKID_UNKNOWN";

  /**
   * intent相关的常量
   */
  interface Intent {

    /**
     * 未处理意图
     */
    String ROKID_INTENT_UNKNOWN = "ROKID.INTENT.ROKID_UNKNOWN";

    /**
     * 打开技能时命中的intent
     */
    String ROKID_INTENT_WELCOME = "ROKID.INTENT.WELCOME";

    /**
     * 退出技能时命中的intent
     */
    String ROKID_INTENT_EXIT = "ROKID.INTENT.EXIT";

  }

  /**
   * EVENT相关常量
   */
  interface Event {

    /**
     * 未处理EVENT
     */
    String ROKID_EVENT_UNKNOWN = "ROKID.EVENT.ROKID_UNKNOWN";

  }

}
