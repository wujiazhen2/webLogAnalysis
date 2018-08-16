import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import com.qworldr.logAnalysis.analysis.{DwOrigin, ParsePageView, ParseVisitBean, ParseWebLog}
import com.qworldr.logAnalysis.bean.{PageView, WebLogBean}
import com.qworldr.logAnalysis.sqlTable.{PageViewTable, VisitBeanTable, WeblogBeanTable}
import com.qworldr.logAnalysis.utils.{DateUtils, StringUtils}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * Created by wujiazhen on 2018/3/9.
  */
object LogAnalyze {

  def main(args: Array[String]): Unit = {
    //创建SparkConf()并设置App名称
    val conf = new SparkConf().setMaster("local[1]").setAppName("LogAnalyze")
    //创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)
    val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
    import hiveContext.implicits._
    val date = new Date();
    //使用sc创建RDD并执行相应的transformation和action
    //42.236.48.217 - - [16/May/2017:03:13:01 +0800] "GET /newslist.do?kind=15 HTTP/1.1" 200 18248 "http://www.wtu.edu.cn/newslist.do?kind=15"
    var webLogBean = null
    //清洗过滤数据
    var rdd = sc.textFile("hdfs://hadoop01:9000/logAnalyze/log/").mapPartitions(ParseWebLog.parses(_))
      .filter(_.getValid()).cache();

    rdd.map(webLogBean => WeblogBeanTable(webLogBean.getValid,
      webLogBean.getRemote_addr, webLogBean.getRemote_user,
      webLogBean.getTime_local, webLogBean.getRequest,
      webLogBean.getStatus,
      webLogBean.getBody_bytes_sent,
      webLogBean.getHttp_referer
      , webLogBean.getHttp_user_agent)).toDF().registerTempTable("table1");
    hiveContext.sql("insert into ods_weblog_origin partition(datestr='" + DateUtils.formatDate(date) + "') " +
      "select valid, remote_addr, remote_user, time_local, request, status, body_bytes_sent, http_referer, " +
      "http_user_agent " +
      "from " +
      "table1")
    //计算pageView
    var pageRdd = rdd.map(webLogBean => (webLogBean.getRemote_user, webLogBean)).groupByKey().mapPartitions(ParsePageView.parsePage(_)).cache()
    pageRdd.map(pageView => PageViewTable(pageView.getSession, pageView.getRemote_addr, pageView.getUserid, pageView.getTimestr, pageView.getRequest, pageView.getStep, pageView.getStaylong,
      pageView.referal, pageView.getUseragent, pageView.getBytes_send, pageView.getStatus)).toDF().registerTempTable("table2")
    hiveContext.sql("insert into ods_click_pageviews partition(datestr='" + DateUtils.formatDate(date) + "') select " +
      "session, remote_addr,userid, timestr, request, step, staylong, referal, useragent, bytes_send, status from table2")

    //计算visitView
    val visitRdd = pageRdd.groupBy(_.getSession).mapPartitions(ParseVisitBean.parseVisit(_)).cache()

    visitRdd.map(visitBean => VisitBeanTable(visitBean.getSession, visitBean.getUserid, visitBean.getRemote_addr, visitBean.getInTime,
      visitBean.getOutTime, visitBean.getInPage, visitBean.getOutPage, visitBean.getReferal, visitBean.getPageVisits)).toDF()
      .registerTempTable("table3")

    hiveContext.sql("insert into ods_click_visit partition(datestr='" + DateUtils.formatDate(date) + "') select " +
      "session,userid, remote_addr, inTime, outTime, inPage, outPage, referal, pageVisits from table3")




    //日记详细信息表
    hiveContext.sql(
      "insert into table  ods_weblog_detail partition(datestr='" + DateUtils.formatDate(date) + "') " +
        "select c.valid,c.remote_addr,c.remote_user,c.time_local, " +
        "substring(c.time_local,0,10) as daystr, " +
        "substring(c.time_local,12) as tmstr, " +
        " month(c.time_local) as month, " +
        " day(c.time_local) as day, " +
        " hour(c.time_local) as hour, " +
        "c.request, " +
        "c.status, " +
        "c.body_bytes_sent, " +
        "c.http_referer, " +
        "c.ref_host, " +
        "c.ref_path, " +
        "c.ref_query, " +
        "c.ref_query_id, " +
        "c.http_user_agent, " +
        " if(old.ip is null,1,0) as isNewVisitor " +
        "from " +
        "(SELECT  " +
        "a.valid, " +
        "a.remote_addr, " +
        "a.remote_user,a.time_local, " +
        "a.request,a.status,a.body_bytes_sent,a.http_referer,a.http_user_agent,b.ref_host,b.ref_path,b.ref_query,b.ref_query_id  " +
        "FROM ods_weblog_origin a  " +
        "LATERAL VIEW  " +
        "parse_url_tuple(http_referer, 'HOST', 'PATH','QUERY', 'QUERY:id') b  " +
        "as ref_host, ref_path, ref_query, ref_query_id" +
        " where datestr='"+ DateUtils.formatDate(date) +"') c " +
        "left outer join  " +
        "dw_user_dsct_history old " +
        "on c.remote_addr=old.ip "
    )

    //记录去重历史用户表
    hiveContext.sql(
      "insert into table dw_user_dsct_history partition(datestr='" + DateUtils.formatDate(date) + "') " +
        "select tmp.day as day,tmp.today_addr as new_ip from " +
        "( " +
        "select today.day as day,today.remote_addr as today_addr,old.ip as old_addr  " +
        "from  " +
        "(select distinct remote_addr as remote_addr," + DateUtils.formatDate(date) + " as day from ods_weblog_origin where datestr=" + DateUtils.formatDate(date) + ") today " +
        "left outer join  " +
        "dw_user_dsct_history old " +
        "on today.remote_addr=old.ip " +
        ") tmp " +
        "where tmp.old_addr is null"
    )
    /*  DwOrigin.load(hiveContext);*/
    //停止sc，结束该任务
    sc.stop()
  }

}
