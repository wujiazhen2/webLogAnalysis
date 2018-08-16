package com.qworldr.logAnalysis.analysis

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import com.qworldr.logAnalysis.bean.WebLogBean
import com.qworldr.logAnalysis.utils.{DateUtils, StringUtils}

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks

/**
  * Created by wujiazhen on 2018/3/10.
  */
object ParseWebLog {
  def main(args: Array[String]): Unit = {

  }

  val staticResource:String=".js,.css,.jpg,.png,.gif,.ico";
  def filterStaticResource(webLogBean: WebLogBean): Unit ={
    if (webLogBean.getRequest==null ){
       webLogBean.setValid(false);
    }else{
       val request = webLogBean.getRequest
       val split = staticResource.split(",")
      val loop =new  Breaks
      loop.breakable{
        for (i <- split) {
          if (request.endsWith(i) || request.indexOf(i + "?") > 0) {
            webLogBean.setValid(false)
            loop.break()
          }
        }
      }
    }
  }

  def parses(iter: Iterator[String]) : Iterator[WebLogBean] = {
    var res = ListBuffer[WebLogBean]()
    while (iter.hasNext) {
      res+=parse(iter.next);
    }
    res.iterator
  }
  def parse(line:String):WebLogBean= {
   val simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val webLogBean = new WebLogBean()
    try {
      val arr = line.split(" ")
      if (arr.length > 11) {
        webLogBean.setRemote_addr(arr(0))
        webLogBean.setRemote_user(arr(0))
        var time_local = DateUtils.formatLogDate(arr(3).substring(1))
        webLogBean.setTime_local(time_local)
        webLogBean.setRequest(StringUtils.trim(arr(6), '\"'))
        webLogBean.setStatus(arr(8))
        webLogBean.setBody_bytes_sent(arr(9))
        webLogBean.setHttp_referer(StringUtils.trim(arr(10), '\"'))
        //如果useragent元素较多，拼接useragent
        if (arr.length > 12) {
          val sb = new StringBuilder
          var i = 11
          while (i < arr.length) {
            {
              sb.append(StringUtils.trim(arr(i),'\"')).append("&")
            }
            {
              i += 1;
              i - 1
            }
          }
          webLogBean.setHttp_user_agent(sb.toString)
        }
        else webLogBean.setHttp_user_agent(arr(11))
        if (webLogBean.getStatus.toInt >= 400) {
          // 大于400，HTTP错误
          webLogBean.setValid(false)
        }
        if (null == time_local || "".equals(time_local.trim)  || "/".equals(webLogBean.getRequest)) webLogBean.setValid(false)

        if(webLogBean.getTime_local!=null) {
          webLogBean.setTime_local(simpleDateFormat.format(new Date()) + webLogBean.getTime_local.substring(10))
        }
      }
      else {
        webLogBean.setValid(false)
      }
      filterStaticResource(webLogBean);
      webLogBean
    } catch {
      case e: Exception => {
        e.printStackTrace()
        println(s"/n${line}")
        webLogBean
      }
    }
  }
}
