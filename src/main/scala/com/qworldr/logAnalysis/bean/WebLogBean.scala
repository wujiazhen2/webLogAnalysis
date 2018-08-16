package com.qworldr.logAnalysis.bean

import com.qworldr.logAnalysis.utils.DateUtils

import scala.beans.BeanProperty

/**
  * Created by wujiazhen on 2018/3/9.
  */
class WebLogBean  extends  Ordered[WebLogBean] with Serializable{
  @BeanProperty
  var valid:Boolean = true // 判断数据是否合法
  @BeanProperty
  var remote_addr:String = null // 记录客户端的ip地址
  @BeanProperty
  var remote_user:String = null // 记录客户端用户名称,忽略属性"-"
  @BeanProperty
  var time_local:String = null // 记录访问时间与时区
  @BeanProperty
  var request:String = null // 记录请求的url与http协议
  @BeanProperty
  var status:String = null // 记录请求状态；成功是200
  @BeanProperty
  var body_bytes_sent:String = null // 记录发送给客户端文件主体内容大小
  @BeanProperty
  var http_referer:String = null // 用来记录从那个页面链接访问过来的
  @BeanProperty
  var http_user_agent:String = null // 记录客户浏览器的相关信息


  override def toString = s"$valid, $remote_addr, $remote_user, $time_local, $request, $status, $body_bytes_sent, $http_referer, $http_user_agent"

  override def compare(that: WebLogBean): Int =  DateUtils.parseDate(this.getTime_local).compareTo(DateUtils.parseDate(that.getTime_local));
}
