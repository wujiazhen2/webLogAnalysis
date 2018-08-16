package com.qworldr.logAnalysis.analysis

import java.util.Date

import com.qworldr.logAnalysis.utils.DateUtils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext

/**
  * Created by wujiazhen on 2018/4/14.
  */
object DwOrigin {


    def load(hiveContext: HiveContext): Unit ={
         val df = hiveContext.sql("select daystr,hour,ref_host,isNewVisitor from ods_weblog_detail where datestr='"+DateUtils.formatDate(new Date())+"' group by  daystr,hour,ref_host,isNewVisitor");
        val collect = df.rdd.collect()
        var pv:Long=0;
        var uv:Long=0;
        var ip_num:Long=0;
        var new_visitor:Long=0;
        var avg_visit_time: Long = 0;
        var avg_deepth: Long = 0;
        for(row <- collect){
           val dateStr=row.getString(0);
           val hour=row.getString(1);
           val ref_host=row.getString(2);
           val isNewVisitor=row.getString(3);
           var condition=""
           if(ref_host ==null) {
              condition="where datestr='" + dateStr + "' and hour=" + hour + " and ref_host is null  and isNewVisitor=" + isNewVisitor
           }else {
             condition="where datestr='" + dateStr + "' and hour=" + hour + " and ref_host='"+ref_host+"'  and isNewVisitor=" + isNewVisitor
           }
             var pvDf = hiveContext.sql("select count(*) from ods_weblog_detail "+condition);
              pv= pvDf.first().getLong(0);
             //uv
             var uvDF = hiveContext.sql("select count(distinct remote_user) from ods_weblog_detail "+condition);
             uv= uvDF.first().getLong(0);
             //ip_Num
             var ipDF = hiveContext.sql("select count(distinct remote_addr) from ods_weblog_detail "+condition);
             ip_num= ipDF.first().getLong(0);
             //new_visitor
             if ("1".equals(isNewVisitor)) {
               var new_visitorDF:DataFrame=null;
               if(ref_host ==null) {
                 new_visitorDF = hiveContext.sql("select count(distinct remote_user) from ods_weblog_detail where daystr=" + dateStr +
                   " and hour=" + hour + " and ref_host is null and isNewVisitor=1")
               }else{
                  new_visitorDF = hiveContext.sql("select count(distinct remote_user) from ods_weblog_detail where daystr=" + dateStr +
                   " and hour=" + hour + " and ref_host = '"+ref_host+"' and isNewVisitor=1")
               }
              new_visitor=new_visitorDF.first().getLong(0);
             }
            var visitCondition:String=null;
            if(ref_host ==null) {
              visitCondition="where datestr='" + dateStr + "'  and c.HOST is null  and  if(old.ip is null,1,0)= "+isNewVisitor
            }else {
              visitCondition="where datestr='" + dateStr + "'  and c.HOST='"+ref_host+"'  and if(old.ip is null,1,0)=" + isNewVisitor
            }
             //avg_visit_time
             var avg_visit_timeDF = hiveContext.sql("select avg(unix_timestamp(outtime)-unix_timestamp(intime)) as avg_visit_time from (select a.*,b.host from ods_click_visit a" +
               "LATERAL VIEW  " +
               "parse_url_tuple(referal, 'HOST', 'PATH','QUERY', 'QUERY:id') b) c  " +
               "left outer join  " +
               "dw_user_dsct_history old " +
               "on c.remote_addr=old.ip "+
               visitCondition)
              avg_visit_time=avg_visit_timeDF.first().getLong(0);
             //avg_deepth 平均访问深度
             var avg_deepthDF = hiveContext.sql("select avg(pageVisits) as avg_deepth from ods_click_visit "+
            "LATERAL VIEW  " +
              "parse_url_tuple(referal, 'HOST', 'PATH','QUERY', 'QUERY:id') b  " +
               visitCondition)
            avg_deepth=avg_deepthDF.first().getLong(0);

            println(s"-----------------$pv,$uv,$ip_num,$new_visitor,$avg_visit_time,$avg_deepth-------------------");
         }
    }
}
