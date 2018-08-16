package com.qworldr.logAnalysis.utils

import com.qworldr.logAnalysis.bean.WebLogBean

/**
  * Created by wujiazhen on 2018/3/10.
  */
object OrderUtils {

  implicit def webLogBeanOrderByTime(w : WebLogBean) = new Ordered[WebLogBean]{
   override def compare(that: WebLogBean): Int = {
       DateUtils.parseDate(w.getTime_local).compareTo(DateUtils.parseDate(that.getTime_local));
    }
  }
}
