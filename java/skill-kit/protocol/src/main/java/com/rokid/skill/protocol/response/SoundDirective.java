package com.rokid.skill.protocol.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 声音指令
 *
 * @author lion.xys
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SoundDirective extends Directive {

  /**
   * 操作
   */
  private ActionEnum action;

  /**
   * 是否不响应客户端事件
   */
  private boolean disableEvent = false;

  /**
   * 播放相关枚举
   */
  @SuppressWarnings("unused")
  public enum ActionEnum {
    /**
     * 播放
     */
    PLAY,
    /**
     * 暂停
     */
    PAUSE,
    /**
     * 继续播放
     */
    RESUME,
    /**
     * 停止
     */
    STOP
  }

}