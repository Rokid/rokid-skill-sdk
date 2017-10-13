package com.rokid.skill.protocol.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 音频指令
 *
 * @author lion.xys
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MediaDirective extends SoundDirective {

  /**
   * media的具体内容
   */
  private MediaItem item = new MediaItem();

  @SuppressWarnings("WeakerAccess")
  public MediaDirective() {
    this.setType(DirectiveType.media);
  }

  public MediaDirective(MediaItem item) {
    this();
    this.item = item;
  }

  /**
   * 播放内容
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @SuppressWarnings("WeakerAccess")
  public static class MediaItem {

    /**
     * media内容Id
     */
    private String itemId;

    /**
     * 用于鉴权的token，由CloudApp填充和判断
     */
    private String token;

    /**
     * 媒体类型
     */
    @Builder.Default
    private MediaType type = MediaType.AUDIO;

    /**
     * 可用的流媒体播放链接
     */
    private String url;

    /**
     * 毫秒数值，表明从哪里开始播放
     */
    private long offsetInMilliseconds;

  }

  /**
   * 媒体类型
   */
  @SuppressWarnings("unused")
  public enum MediaType {
    /**
     * 音频
     */
    AUDIO,
    /**
     * 视频
     */
    VIDEO
  }

}
