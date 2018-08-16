package com.qworldr.logAnalysis.utils

import org.apache.spark.sql.SQLContext

/**
  * Created by wujiazhen on 2018/4/14.
  */
object JdbcUtils {

  def getJdbcDF(sqlContext: SQLContext ,tableName:String): Unit ={
    //定义mysql信息
    val jdbcDF = sqlContext.read.format("jdbc").options(
      Map("url"->"jdbc:mysql://localhost:3306/webstatistics",
        "dbtable"->tableName,
        "driver"->"com.mysql.jdbc.Driver",
        "user"-> "root",
        //"partitionColumn"->"day_id",
        "lowerBound"->"0",
        "upperBound"-> "1000",
        //"numPartitions"->"2",
        "fetchSize"->"100",
        "password"->"ROOT")).load()
  }
}
