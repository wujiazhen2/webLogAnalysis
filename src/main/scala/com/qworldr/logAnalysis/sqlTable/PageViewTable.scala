package com.qworldr.logAnalysis.sqlTable

/**
  * Created by wujiazhen on 2018/3/18.
  */
case class PageViewTable(
                     session:String ,
                remote_addr:String ,
                     userid:String,
                timestr:String ,
                request:String ,
                step:Int ,
                staylong:Long ,
                referal :String,
                useragent:String ,
                bytes_send :String,
                status:String )
