package com.qworldr.logAnalysis.analysis


import com.qworldr.logAnalysis.bean.{PageView, VisitBean}
import org.apache.commons.beanutils.BeanUtils

import scala.collection.mutable.ListBuffer

/**
  * Created by wujiazhen on 2018/3/11.
  */
object ParseVisitBean {
  def parseVisit(pageViewsEntity: (String,Iterable[PageView])):Iterable[VisitBean] ={
      val visitBeans =new ListBuffer[VisitBean];
      var pageList = List[PageView]()
      pageList++=pageViewsEntity._2;
      pageList=pageList.sortBy(_.getStep)

    // 取这次visit的首尾pageview记录，将数据放入VisitBean中
    val visitBean = new VisitBean
    // 取visit的首记录
    visitBean.setInPage(pageList.head.getRequest)
    visitBean.setInTime(pageList.head.getTimestr)
    // 取visit的尾记录
    visitBean.setOutPage(pageList.last.getRequest)
    visitBean.setOutTime(pageList.last.getTimestr)
    // visit访问的页面数
    visitBean.setUserid(pageList.last.getUserid);
    visitBean.setPageVisits(pageList.size)
    // 来访者的ip
    visitBean.setRemote_addr(pageList.head.getRemote_addr)
    // 本次visit的referal
    visitBean.setReferal(pageList.head.getReferal)
    visitBean.setSession(pageList.head.getSession.toString)
    visitBeans+=visitBean
    visitBeans
  }
  def parseVisit(iterator: Iterator[(String, Iterable[PageView])]): Iterator[VisitBean] = {
    val visitBeans =new ListBuffer[VisitBean];
    iterator.foreach(visits=>{
      visitBeans++=parseVisit(visits)
    })
    visitBeans.iterator
  }

}
