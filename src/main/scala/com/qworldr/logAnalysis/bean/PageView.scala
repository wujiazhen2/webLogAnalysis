package com.qworldr.logAnalysis.bean

import scala.reflect.BeanProperty

/**
  * Created by wujiazhen on 2018/3/9.
  */
class PageView extends Serializable{
  @BeanProperty
   var session:String = null
  @BeanProperty
   var remote_addr:String = null
  @BeanProperty
  var userid:String = null
  @BeanProperty
   var timestr:String = null
  @BeanProperty
   var request:String = null
  @BeanProperty
   var step:Int = 0
  @BeanProperty
   var staylong:Long = 0
  @BeanProperty
   var referal :String= null
  @BeanProperty
   var useragent:String = null
  @BeanProperty
   var bytes_send :String= null
  @BeanProperty
   var status:String = null

  def this(session:String ,
           remote_addr:String ,
           timestr:String ,
           request:String ,
           step:Int ,
           staylong:Long ,
           referal :String,
           useragent:String ,
           bytes_send :String,
           status:String ){
          this()
          this.session=session;
          this.remote_addr=remote_addr
          this.timestr=timestr
          this.request=request
          this.step=step
          this.staylong=staylong
          this.referal =referal
          this.useragent=useragent
          this.bytes_send =bytes_send
          this.status=status
}

  override def toString = s"$session, $remote_addr, $timestr, $request, $step, $staylong, $referal, $useragent, $bytes_send, $status"
}
