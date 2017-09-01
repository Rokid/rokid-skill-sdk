package com.rokid.skill.protocol;

import java.util.LinkedHashMap;
import lombok.Data;

/**
 * 基于 NLP 的结果产生的请求内容
 *
 * @author lion.xys
 */
@Data
@SuppressWarnings("unused")
public class IntentContent {

  /**
   * 当前命中的技能Id
   */
  private String applicationId;

  /**
   * 当前命中到的意图
   */
  private String intent;

  /**
   * 当前用户语句中出现的槽以及对应的值
   */
  private LinkedHashMap<String, String> slots;

}
