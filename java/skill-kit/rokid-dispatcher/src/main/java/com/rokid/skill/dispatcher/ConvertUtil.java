package com.rokid.skill.dispatcher;

import com.google.gson.Gson;
import com.rokid.skill.protocol.RokidRequest;
import com.rokid.skill.protocol.RokidRequest.Request;

/**
 * 请求体转换
 *
 * @author lion.xys
 */
@SuppressWarnings("unused")
class ConvertUtil {

  private static final Gson GSON = new Gson();

  /**
   * 将未知类型的请求转为指定类型请求
   *
   * @param request 请求实例
   * @param tClass  指定类型
   * @param <T> 指定类型定义
   * @return  指定类型请求
   */
  static <T> RokidRequest<T> convertFromRequst(RokidRequest request, Class<T> tClass) {

    RokidRequest<T> requestTmp = new RokidRequest<T>();

    requestTmp.setVersion(request.getVersion());

    requestTmp.setContext(request.getContext());

    requestTmp.setSession(request.getSession());

    Request orign = request.getRequest();

    Request<T> tRequest = new Request<T>();

    tRequest.setReqId(orign.getReqId());

    tRequest.setReqType(orign.getReqType());

    String json = GSON.toJson(orign.getContent());

    tRequest.setContent(GSON.fromJson(json, tClass));

    requestTmp.setRequest(tRequest);

    return requestTmp;
  }

}
