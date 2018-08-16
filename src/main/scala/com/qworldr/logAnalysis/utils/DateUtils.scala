package com.qworldr.logAnalysis.utils

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Date, Locale}

/**
  * Created by wujiazhen on 2018/3/10.
  */
object DateUtils {

  def formatDate(date:Date):String={
      return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
  }
  def formatDateTime(date:Date):String={
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date)
  }
  def formatDateTimeWithoutSpilt(date:Date):String={
    val dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
    return dateFormat.format(date)
  }
  def  formatLogDate(time_local:String)= {
    try {
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US).parse(time_local));
    } catch  {
      case  e:Exception => {
         null
      }
    }

  }

  def  parseDate(time:String):Date ={
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(time)
  }


  def timeDiff(time1: String, time2: String): Long = {
    val d1: Date = parseDate(time1)
    var d2: Date=null;

      d2 = parseDate(time2)

    d1.getTime - d2.getTime
  }
}
