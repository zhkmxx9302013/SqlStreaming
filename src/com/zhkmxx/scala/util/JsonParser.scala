package com.zhkmxx.scala.util

import scala.util.parsing.json.JSON

/**
  * Created by zhao on 2017/1/26.
  */
class JsonParser {
  def parseKafkaJsonString(parseStr : String):Map[String,Any] = {
    val parseResult = JSON.parseFull(parseStr)

    parseResult match {
      case Some(m : Map[String,Any]) => m
      case None => {
        println(Const.PARSE_JSON_FAILURE)
        val m = Map(""->"")
        m
      }
    }
  }
}
