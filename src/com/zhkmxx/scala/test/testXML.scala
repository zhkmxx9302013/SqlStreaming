package com.zhkmxx.scala.test

import com.zhkmxx.scala.util.XMLPaser

/**
  * Created by zhao on 2017/3/27.
  */
object testXML {
  def main(args: Array[String]): Unit = {
    val xmlParser = new XMLPaser("D:\\Codes\\Scala\\SqlStreaming\\src\\com\\zhkmxx\\scala\\config\\snp_runtime_config.xml")
    xmlParser.execute
  }
}
