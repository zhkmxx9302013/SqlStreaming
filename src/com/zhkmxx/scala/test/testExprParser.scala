package com.zhkmxx.scala.test

import com.zhkmxx.scala.parser.{ExprParsre, InListJGDMParser}
import com.zhkmxx.scala.util.Const

/**
  * Created by zhao on 2017/3/9.
  */
object testExprParser {
  def main(args: Array[String]): Unit = {
    val parser = new InListJGDMParser
    val str = "{\"SUMTABLE\":\"S_0111\",\"RECVER\":0,\"UNITCODE\":\"320000\",\"ROWINDEX\":1,\"COLINDEX\":1,\"FORMULA\":\"if SNP_R601[H003]=\\\"1\\\"  then SNP_R601[RECID,COUNT]\",\"INSTITUTIONGUID\":\"INSTITUTID123SDFEWR\"}"
    val formula = "if(InList([JGDM],\"\")) then SNP_R601[H211,SUM]"
//    val result = parser.parserAll(parser.expr, inputString)

    val ExpressPaser = {
      if(formula.contains("InList([JGDM]")){
        val exprParser = new InListJGDMParser
        val InListJGDMPaser = exprParser.parserAll(exprParser.expr, formula)//Parsing
        InListJGDMPaser
      }else {
        val exprParser = new ExprParsre
        var ExpressPaser = exprParser.parserAll(exprParser.expr, formula)//Parsing
        ExpressPaser
      }
    }

    if(ExpressPaser.successful){
        println(ExpressPaser.get)
    }
//    println(result)
//    if (result.successful)
//      println(result.get)
//    else
//      println(Const.PARSE_FORMULA_FAILURE)

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


