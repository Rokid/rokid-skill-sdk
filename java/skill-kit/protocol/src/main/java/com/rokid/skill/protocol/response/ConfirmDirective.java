package com.rokid.skill.protocol.response;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 确认指令
 *
 * @author lion.xys
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfirmDirective extends Directive {

  /**
   * 需要进行confirm的intent内容
   */
  private String confirmIntent;

  /**
   * 需要进行confirm的slot内容
   */
  private String confirmSlot;

  /**
   * 动态新增的confirm内容
   */
  private List<String> optionWords = new LinkedList<>();

  public ConfirmDirective() {
    this.setType(DirectiveType.confirm);
  }
}
