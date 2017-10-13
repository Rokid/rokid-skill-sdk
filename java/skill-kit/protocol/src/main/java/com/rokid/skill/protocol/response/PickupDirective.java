package com.rokid.skill.protocol.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拾音指令
 *
 * @author lion.xys
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PickupDirective extends Directive {

  /**
   * 是否拾音
   */
  private boolean enable = true;

  /**
   * 在没有用户说话时拾音状态持续多久，单位毫秒
   */
  private long durationInMilliseconds = 1000;

  public PickupDirective() {
    this.setType(DirectiveType.pickup);
  }
}