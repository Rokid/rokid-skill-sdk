package com.rokid.skill.protocol.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 指令
 * @author lion.xys
 */
@Data
@EqualsAndHashCode
public class Directive {

  /**
   * 指令类型
   */
  private DirectiveType type;

  /**
   * 指令类型枚举
   */
  public enum DirectiveType {
    voice,
    media,
    display,
    confirm,
    pickup
  }

}
