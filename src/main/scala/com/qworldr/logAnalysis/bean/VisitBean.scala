package com.qworldr.logAnalysis.bean

import scala.beans.BeanProperty

/**
  * Created by wujiazhen on 2018/3/9.
  */
class VisitBean extends Serializable{
  @BeanProperty
   var session:String = null
  @BeanProperty
  var userid:String=null
  @BeanProperty
   var remote_addr:String = null
  @BeanProperty
   var inTime:String = null
  @BeanProperty
   var outTime:String = null
  @BeanProperty
   var inPage:String = null
  @BeanProperty
   var outPage:String = null
  @BeanProperty
   var referal :String= null
  @BeanProperty
   var pageVisits:Int = 0


  override def toString = s"$session,$userid, $remote_addr, $inTime, $outTime, $inPage, $outPage, $referal, $pageVisits"
}
