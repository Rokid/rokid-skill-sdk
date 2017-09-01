package com.rokid.skill.protocol;

import java.util.LinkedHashMap;
import lombok.Data;

/**
 * 事件请求内容
 *
 * @author lion.xys
 */
@Data
@SuppressWarnings("unused")
public class EventContent {

  /**
   * 具体的事件类型
   */
  private String event;

  /**
   * 自定义字段
   */
  private LinkedHashMap<String, Object> extra;

  /**
   * media类型的eventrequest支持如下扩展字段
   */
  @Data
  @SuppressWarnings("unused")
  public static class Media {

    /**
     * MediaItem里面的token
     */
    private String token;

    /**
     * 当前进度
     */
    private String progress;

    /**
     * 音频文件的总长度
     */
    private String duration;

  }

//  /**
//   * 事件类型
//   */
//  public enum Event {
//    /**
//     * 当Voice开始播放时发生
//     */
//    VOICE_STARTED("Voice.STARTED"),
//    /**
//     * 当Voice停止是发生，此处停止可能是被打断，可能是播放完成，也可能是播放失败，但都作为统一的事件抛出
//     */
//    VOICE_FINISHED("Voice.FINISHED"),
//    /**
//     * 当MediaPlayer开始播放时发生
//     */
//    MEDIA_START_PLAYING("Media.START_PLAYING"),
//    /**
//     * 当MediaPlayer中途停止时发生
//     */
//    MEDIA_PAUSED("Media.PAUSED"),
//    /**
//     * 当播放内容结束前15秒时发生，当总时长不足15秒时，会在 Media.START_PLAYING* 后发生
//     */
//    MEDIA_NEAR_FINISH("Media.NEAR_FINISH"),
//    /**
//     * 当播放内容结束时发生
//     */
//    MEDIA_FINISHED("Media.FINISHED");
//
//    private String event;
//
//    Event(String event) {
//      this.event = event;
//    }
//  }

}