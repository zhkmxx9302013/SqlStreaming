package com.zhkmxx.scala.parser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/3/24.
  * DNASQL,调查对象代码模糊匹配语法分析器
  */
class InListDCDXDMPaser extends StandardTokenParsers{
  lexical.delimiters += ("=",">=","<=",">","<","!=","&&","||","[","]",",","(",")")
  lexical.reserved   += ("if","then","SUM","COUNT","AND","OR","InList","DCDXDM")

  def expr: Parser[String] = "if" ~ expr_front ~ "then" ~ expr_back ^^{
    case "if" ~ exp1 ~ "then" ~ exp2 => exp2 + exp1
  }


  def expr_front: Parser[String] = "(" ~"InList"~"("~"["~"DCDXDM"~"]"~","~ stringLit ~ ")"~")" ^^{
    case "(" ~"InList"~"("~"["~"DCDXDM"~"]"~","~ stringLit ~ ")"~")" => " WHERE SNP_TARGET.STDCODE LIKE '" + stringLit +"%'"
  }

  def expr_back: Parser[String] = ident ~ "[" ~ ident ~ "," ~ ("SUM"|"COUNT") ~ "]" ^^ {
    case ident1~"["~ident2~","~"COUNT"~"]" => "SELECT COUNT("+ ident1.toString +"."+ ident2.toString +") " +
                                              "FROM " + ident1.toString + " LEFT OUTER JOIN SNP_TARGET ON " +
                                              "(" + ident1.toString +".UNITID=SNP_TARGET.RECID)"
    case ident1~"["~ident2~","~"SUM"~"]" => "SELECT SUM("+ ident1.toString +"."+ ident2.toString +") " +
                                            "FROM " + ident1.toString + " LEFT OUTER JOIN SNP_TARGET ON " +
                                            "(" + ident1.toString +".UNITID=SNP_TARGET.RECID)"
  }

  def parserAll[T]( p : Parser[T], input :String): ParseResult[T]= {
    phrase(p)( new lexical.Scanner(input))
  }
}
