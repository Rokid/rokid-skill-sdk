package com.rokid.skill.protocol;

import com.google.gson.JsonElement;
import java.util.LinkedHashMap;
import lombok.Data;

/**
 * CloudDispatcher产生的用于向CloudApp获取对应返回结果的请求
 *
 * @author lion.xys
 */
@Data
@SuppressWarnings("WeakerAccess")
public class RokidRequest<T> {

  /**
   * Response协议的版本，必须由 CloudApp 填充。当前协议版本是 2.0.0
   */
  private String version = "2.0.0";

  /**
   * 会话的信息
   */
  private Session session;

  /**
   * 当前的设备信息，用户信息和应用状态，用以帮助CloudApp更好的去管理逻辑，状态以及对应的返回结果
   */
  private Context context;

  /**
   * 当前请求的真正内容
   */
  private Request<T> request;

  /**
   * 当前技能的session
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Session {

    /**
     * 每次会话的唯一ID，由系统填充
     */
    private String sessionId;

    /**
     * 向CloudApp表明此次会话是新的会话还是已经存在的会话
     */
    private boolean newSession;

    /**
     * 为CloudApp提供attributes字段留保存上下文信息的字段
     */
    private LinkedHashMap<String, JsonElement> attributes;

  }

  /**
   * 提供了当前的设备信息，用户信息和应用状态，用以帮助CloudApp更好的去管理逻辑，状态以及对应的返回结果
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Context {

    /**
     * 当前技能信息
     */
    private ApplicationInfo application;

    /**
     * 当前设备信息
     */
    private DeviceInfo device;

    /**
     * 与当前设备绑定的用户信息
     */
    private UserInfo user;

  }

  /**
   * 应用信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class ApplicationInfo {

    /**
     * 应用ID字符串
     */
    private String applicationId;

  }

  /**
   * 设备信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class DeviceInfo {

    /**
     * 当前设备的基础信息
     */
    private BasicInfo basic;

    /**
     * 当前设备的屏幕信息
     */
    private ScreenInfo screen;

    /**
     * 当前设备上CloudAppClient中的MediaPlayer的状态信息
     */
    private MediaStatus media;

    /**
     * 当前设备上CloudAppClient中语音交互的状态信息
     */
    private VoiceStatus voice;

    /**
     * 当前设备的地理位置信息
     */
    private LocationInfo location;

  }

  /**
   * 设备基础信息,主要包含设备制造信息、时间信息、国家文字信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class BasicInfo {

    /**
     * 注册生产商ID
     */
    private String vendor;

    /**
     * 生产商设定的设备型号
     */
    private String deviceType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备主人Id
     */
    private String masterId;

    /**
     * 设备当前激活词
     */
    private String voicetrigger;

    /**
     * 国家及语言，标准locale格式
     */
    private String locale;

    /**
     * 当前时间，unix timestamp
     */
    private long timestamp;

  }

  /**
   * 屏幕信息，主要包含屏幕的分辨率信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class ScreenInfo {

    /**
     * X 方向上的像素大小
     */
    private String x;

    /**
     * Y 方向上的像素大小
     */
    private String y;

  }

  /**
   * 设备上CloudAppClient中的MediaPlayer的状态信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class MediaStatus {

    /**
     * 当前设备上CloudAppClient中MediaPlayer的状态
     */
    private State state;

  }

  /**
   * 当前设备上CloudAppClient中语音交互的状态信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class VoiceStatus {

    /**
     * 当前设备上CloudAppClient中语音交互的状态
     */
    private State state;

  }

  /**
   * 地理位置信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class LocationInfo {

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 国家
     */
    private String country;

    /**
     * 州/省份
     */
    private String state;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String area;

    /**
     * 地区，行政
     */
    private String district;

    /**
     * 街道
     */
    private String street;

    /**
     * 时区
     */
    private String timeZone;
  }

  /**
   * 用户信息
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class UserInfo {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 账户关联Id
     */
    private String accountLinkedId;

  }

  /**
   * 请求的真正内容
   */
  @Data
  @SuppressWarnings("WeakerAccess")
  public static class Request<T> {

    /**
     * 当前请求类型
     */
    private String reqType = RokidConstant.INTENT_REQUEST;

    /**
     * 当前请求的唯一ID
     */
    private String reqId;

    /**
     * 当前请求的具体内容
     */
    private T content;

  }

  /**
   * 播放状态
   */
  @SuppressWarnings("unused")
  public enum State {

    /**
     * 当前有媒体正在播放
     */
    PLAYING,
    /**
     * 当前媒体被暂停，可以执行继续播放（RESUME）操作
     */
    PAUSED,
    /**
     * 当前媒体播放器为空闲状态，没有任何媒体数据
     */
    IDLE

  }

}