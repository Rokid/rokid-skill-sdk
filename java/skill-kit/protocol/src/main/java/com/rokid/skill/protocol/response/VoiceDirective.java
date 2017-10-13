package com.rokid.skill.protocol.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * tts指令
 *
 * @author lion.xys
 */
@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper = true)
public class VoiceDirective extends SoundDirective {

  /**
   * voice的具体内容
   */
  private VoiceItem item = new VoiceItem();

  @SuppressWarnings("WeakerAccess")
  public VoiceDirective() {
    this.setType(DirectiveType.voice);
  }

  public VoiceDirective(VoiceItem item) {
    this();
    this.item = item;
  }

  /**
   * voice的具体内容
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class VoiceItem {

    /**
     * tts内容的Id
     */
    private String itemId;

    /**
     * 需要播报的TTS内容
     */
    private String tts;

  }

}