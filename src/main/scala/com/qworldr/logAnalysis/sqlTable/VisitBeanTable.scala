package com.qworldr.logAnalysis.sqlTable

import scala.beans.BeanProperty

/**
  * Created by wujiazhen on 2018/4/12.
  */
case class VisitBeanTable(
  session:String ,
  userid:String,
  remote_addr:String ,
  inTime:String ,
  outTime:String ,
  inPage:String ,
  outPage:String ,
  referal :String,
  pageVisits:Int
               )
