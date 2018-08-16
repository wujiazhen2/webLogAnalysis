import com.qworldr.logAnalysis.bean.WebLogBean
import com.qworldr.logAnalysis.utils.DateUtils

/**
  * Created by wujiazhen on 2018/3/10.
  */
object Test {

  def main(args: Array[String]): Unit = {
     var str="42.236.48.217 - - [16/May/2017:03:13:01 +0800] \"GET /newslist.do?kind=15 HTTP/1.1\" 200 18248 \"http://www.wtu.edu.cn/newslist.do?kind=15\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36; 360Spider\" \"-\""
      val arr = str.split(" ")
      val webLogBean = new WebLogBean()
    if (arr.length > 11) {
      webLogBean.setRemote_addr(arr(0))
      webLogBean.setRemote_user(arr(1))
      var time_local = DateUtils.formatLogDate(arr(3).substring(1))
      if (null == time_local) time_local = "-invalid_time-"
      webLogBean.setTime_local(time_local)
      webLogBean.setRequest(arr(6))
      webLogBean.setStatus(arr(8))
      webLogBean.setBody_bytes_sent(arr(9))
      webLogBean.setHttp_referer(arr(10))
      //如果useragent元素较多，拼接useragent
      if (arr.length > 12) {
        val sb = new StringBuilder
        var i = 11
        while (i < arr.length) {
          {
            sb.append(arr(i))
          }
          {
            i += 1; i - 1
          }
        }
        webLogBean.setHttp_user_agent(sb.toString)
      }
      else webLogBean.setHttp_user_agent(arr(11))
      if (webLogBean.getStatus.toInt >= 400) {
        // 大于400，HTTP错误
        webLogBean.setValid(false)
      }
      if ("-invalid_time-" == webLogBean.getTime_local) webLogBean.setValid(false)
    }
    else webLogBean.setValid(false)
    print(webLogBean)
  }
}
