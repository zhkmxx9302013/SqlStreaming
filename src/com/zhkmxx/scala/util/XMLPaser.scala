package com.zhkmxx.scala.util

import scala.xml.Node

/**
  * Created by zhao on 2017/3/27.
  */
class XMLPaser (val fileName:String){
  def openXMLConfigFile = scala.xml.XML.loadFile (fileName)

  val getChild: Node => Unit = rootNode => for (node <- rootNode.child)
    node match {
      //支持多级标签匹配
      case <property><name>{ text }</name></property> => text.text match {
        case "snp.oracle.server.name" => print(text.text)
      }
    }
  def execute = getChild(openXMLConfigFile)

}
