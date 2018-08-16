package com.qworldr.logAnalysis.sqlTable

import scala.beans.BeanProperty

/**
  * Created by wujiazhen on 2018/4/12.
  */
case  class WeblogBeanTable(
       valid:Boolean = true, // 判断数据是否合法
       remote_addr:String , // 记录客户端的ip地址
       remote_user:String , // 记录客户端用户名称,忽略属性"-"
       time_local:String , // 记录访问时间与时区
       request:String , // 记录请求的url与http协议
       status:String , // 记录请求状态；成功是200
       body_bytes_sent:String , // 记录发送给客户端文件主体内容大小
       http_referer:String , // 用来记录从那个页面链接访问过来的
       http_user_agent:String  // 记录客户浏览器的相关信息
)
