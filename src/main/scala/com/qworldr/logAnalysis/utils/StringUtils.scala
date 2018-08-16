package com.qworldr.logAnalysis.utils

/**
  * Created by wujiazhen on 2018/3/10.
  */
object StringUtils {

  def trim(string: String,symbol: Char): String = {
    var str: String = string;
    if (string == null || string.length() == 0 ) {
      string;
    } else {
      if (string.charAt(0) == symbol) {
        str = str.substring(1)
      }
      if (string.charAt(string.length() - 1) == symbol) {
        str = str.substring(0, str.length() - 1)
      }
      str
    }
  }
}
