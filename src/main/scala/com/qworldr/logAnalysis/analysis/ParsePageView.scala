package com.qworldr.logAnalysis.analysis

import java.util.UUID

import com.qworldr.logAnalysis.bean.{PageView, WebLogBean}
import com.qworldr.logAnalysis.utils.DateUtils

import scala.collection.mutable.ListBuffer

/**
  * Created by wujiazhen on 2018/3/11.
  */
object ParsePageView {
  def parsePage(iterator: Iterator[(String, Iterable[WebLogBean])]): Iterator[PageView] = {
    val res =new ListBuffer[PageView];
    while(iterator.hasNext){
      res++=parsePage(iterator.next())
    }
    res.iterator
  }

  def parsePage(iterable:(String,Iterable[WebLogBean])):Iterable[PageView]={
    /**
      * 以下逻辑为：从有序bean中分辨出各次visit，并对一次visit中所访问的page按顺序标号step
      */
    var step = 1
    var session = UUID.randomUUID.toString
    var i = 0
    var beans:ListBuffer[WebLogBean]= new ListBuffer[WebLogBean];
    beans++=iterable._2;
    beans.sortBy(_.getTime_local);
    var lastBean:PageView=null;
    var pageViews=new ListBuffer[PageView];
    beans.foreach(bean=>{
      lastBean=logBeanToPageView(bean,lastBean);
      pageViews+=lastBean;
    })
    pageViews
  }
  def logBeanToPageView(webLogBean: WebLogBean,prePageView:PageView):PageView={
    val pageView =new PageView()
    //$session, $remote_addr, $timestr, $request, $step, $staylong, $referal, $useragent, $bytes_send, $status
    pageView.setRemote_addr(webLogBean.getRemote_addr)
    pageView.setTimestr(webLogBean.getTime_local)
    pageView.setRequest(webLogBean.getRequest)
    pageView.setUserid(webLogBean.getRemote_user)
    if(prePageView!=null){
        val diff = DateUtils.timeDiff(webLogBean.getTime_local,prePageView.getTimestr)
        if (diff < 30 * 60 * 1000) {
          pageView.setSession(prePageView.getSession)
          pageView.setStep(prePageView.getStep+1)
          pageView.setStaylong(diff/1000)
        }else{
          pageView.setSession(UUID.randomUUID.toString)
          pageView.setStep(1)
          pageView.setStaylong(60)
        }
    }else{
        pageView.setSession(UUID.randomUUID.toString)
        pageView.setStep(1)
        pageView.setStaylong(60)
    }
    pageView.setReferal(webLogBean.getHttp_referer)
    pageView.setUseragent(webLogBean.getHttp_user_agent)
    pageView.setBytes_send(webLogBean.getBody_bytes_sent)
    pageView.setStatus(webLogBean.getStatus)
    pageView
  }


}
