package com.rokid.skill.protocol;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private boolean shouldEndSession = true;

    /**
     * 语音交互内容
     */
    private Voice voice;

    /**
     * 流媒体内容
     */
    private Media media;

    /**
     * 客户端显示属性
     */
    private Display display;

    /**
     * 确认信息
     */
    private Confirm confirm;

    private Pickup pickup = new Pickup();

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

  }

  /**
   * 声音相关设置
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Voice {

    /**
     * 表示对当前voice的操作，
     * 可以播放（PLAY)、暂停（PAUSE）、继续播放（RESUME）和停止（STOP）
     * （具体Action行为参照Media的Action行为，但是目前暂未实现，PAUSE以及RESUME操作）;
     */
    private ActionEnum action = ActionEnum.PLAY;

    /**
     * 不监听event request
     */
    private boolean disableEvent = false;

    /**
     * voice的具体内容
     */
    private VoiceItem item = new VoiceItem();

  }

  /**
   * 媒体音频相关
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Media {

    /**
     * 对MediaPlayer的操作，目前只支持 4 种操作：PLAY， PAUSE ， RESUME 和 STOP。其中，只有PLAY接受item数据。
     * PLAY：如果有item数据，则按照最新的item从头开始播放，如果没有item数据，且原来有在播放的内容，则从原来播放的内容开始播放
     * PAUSE：暂停当前播放的内容，播放的进度等数据不会丢失（可以直接通过RESUME指令直接恢复原来的播放状态）
     * RESUME：继续播放（从原来的播放进度播放）
     * STOP：停止播放，并且清空当前的播放进度，但是播放内容不清
     */
    private ActionEnum action = ActionEnum.PLAY;

    /**
     * 不监听event request
     */
    private boolean disableEvent = false;

    /**
     * 播放内容
     */
    private MediaItem item = new MediaItem();

  }

  /**
   * voice的具体内容
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class VoiceItem {

    /**
     * 需要播报的TTS内容
     */
    private String tts;

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

  /**
   * Card类型
   */
  @SuppressWarnings("unused")
  public enum CardType {
    /**
     * 账户关联
     */
    ACCOUNT_LINK
  }

  /**
   * display属性
   */
  @Data
  public static class Display {

  }

  /**
   * 确认相关信息
   */
  @Data
  public static class Confirm {

    /**
     * 确认相关意图
     */
    private String confirmIntent;

    /**
     * 确认相关槽
     */
    private String confirmSlot;

    /**
     * 可选信息
     */
    private List<String> optionWords = new LinkedList<>();

  }

  /**
   * 拾音功能控制
   */
  @Data
  public static class Pickup {

    /**
     * 是否启用
     */
    private boolean enable = false;

    /**
     * 拾音持续时间
     */
    private long durationInMilliseconds = 1000;

  }
}
