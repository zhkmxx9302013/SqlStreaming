package com.zhkmxx.scala.test

import com.zhkmxx.scala.parser.ExprParsre
import com.zhkmxx.scala.util.Const

/**
  * Created by zhao on 2017/3/9.
  */
object testExprParser {
  def main(args: Array[String]): Unit = {
    val parser = new ExprParsre
    val str = "{\"SUMTABLE\":\"S_0111\",\"RECVER\":0,\"UNITCODE\":\"320000\",\"ROWINDEX\":1,\"COLINDEX\":1,\"FORMULA\":\"if SNP_R601[H003]=\\\"1\\\"  then SNP_R601[RECID,COUNT]\",\"INSTITUTIONGUID\":\"INSTITUTID123SDFEWR\"}"
    val inputString = "if (SNP_R601[H022]>=\"2\") then SNP_R601[H033,SUM]"
    val result = parser.parserAll(parser.expr, inputString)
    if (result.successful)
      println(result.get)
    else
      println(Const.PARSE_FORMULA_FAILURE)

  }

    //    val parser = new ExprParsre
    //    val str = "{\"SUMTABLE\":\"S_0111\",\"RECVER\":0,\"UNITCODE\":\"320000\",\"ROWINDEX\":1,\"COLINDEX\":1,\"FORMULA\":\"if SNP_R601[H003]=\\\"1\\\"  then SNP_R601[RECID,COUNT]\",\"INSTITUTIONGUID\":\"INSTITUTID123SDFEWR\"}"
    //    //val inputString = "if (SNP_R601[D003]=\"邢おb7肮α䵵\" || SNP_R601[H003]<\"2\") && SNP_R601[D005]<\"wed\" then SNP_R601[H022,COUNT]"
    //    //val result = parser.parserAll(parser.expr, inputString)
    //    //println(result.get)
    ////    val parseResult = JSON.parseFull(str)
    ////    val tupleList = ("",1,"",1,1,"","")
    ////    parseResult match {
    ////      case Some(m : Map[String,Any]) => {
    ////        tupleList._1. = m("SUMTABLE").toString
    ////        tupleList(1) = m("RECVER")
    ////        tupleList(2) = m("UNITCODE").toString
    ////        tupleList(3) = m("ROWINDEX")
    ////        tupleList(4) = m("COLINDEX")
    ////        tupleList(5) = m("FORMULA").toString
    ////        tupleList(6) = m("INSTITUTIONGUID").toString
    ////      }
    ////
    ////    }
    ////    println(tupleList)
    ////    var m = Map()
    ////    m = b.getOrElse().asInstanceOf(Map)
}


