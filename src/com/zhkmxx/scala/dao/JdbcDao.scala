package com.zhkmxx.scala.dao

import java.sql.{Connection, Timestamp}
import java.util.Properties

import com.zhkmxx.scala.util.Const
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects, JdbcType}
import org.apache.spark.sql.types._

/**
  * Created by zhaozihe on 2017/1/19.
  */
class JdbcDao {
  //初始化Oracle方言
  def initOracleDialect:Unit = {
    val OracleDialect = new JdbcDialect {
      override def canHandle(url: String): Boolean = url.startsWith("jdbc:oracle") || url.contains("oracle")

      //getJDBCType is used when writing to a JDBC table
      override def getJDBCType(dt: DataType): Option[JdbcType] = dt match {
        case StringType => Some(JdbcType("VARCHAR2(255)", java.sql.Types.VARCHAR))
        case BooleanType => Some(JdbcType("NUMBER(1)", java.sql.Types.NUMERIC))
        case IntegerType => Some(JdbcType("NUMBER(16)", java.sql.Types.NUMERIC))
        case LongType => Some(JdbcType("NUMBER(16)", java.sql.Types.NUMERIC))
        case DoubleType => Some(JdbcType("NUMBER(16,4)", java.sql.Types.NUMERIC))
        case FloatType => Some(JdbcType("NUMBER(16,4)", java.sql.Types.NUMERIC))
        case ShortType => Some(JdbcType("NUMBER(5)", java.sql.Types.NUMERIC))
        case ByteType => Some(JdbcType("NUMBER(3)", java.sql.Types.NUMERIC))
        case BinaryType => Some(JdbcType("BLOB", java.sql.Types.BLOB))
        case TimestampType => Some(JdbcType("DATE", java.sql.Types.DATE))
        case DateType => Some(JdbcType("DATE", java.sql.Types.DATE))
        case DecimalType.Unlimited => Some(JdbcType("NUMBER(38,4)", java.sql.Types.NUMERIC))
        case _ => None
      }
    }

    JdbcDialects.registerDialect(OracleDialect)
  }

  //生成Oracle连接地址
  def generateUrl:String = {
    val url = "jdbc:oracle:thin:@" +  Const.ORACLE_DRIVER_URL + ":" + Const.ORACLE_DRIVER_PORT+":" + Const.ORACLE_DRIVER_DATABASE
    url
  }

  //Oracle连接项目配置
  def initOracleConn:Connection = {

    val oracleDriverUrl = generateUrl

    Class.forName(Const.DRIVER_CLASS_NAME).newInstance()

    val conn = JdbcUtils.createConnectionFactory(oracleDriverUrl, initOracleProp)()
    conn
  }

  def initOracleProp:Properties = {
    val connectProperties = new Properties()
    connectProperties.put("user", Const.USERNAME)
    connectProperties.put("password", Const.PASSWORD)
    connectProperties
  }

  /**
    * 执行将统计结果存入到Oracle
    * @param dataSet
    */
  def execute2Oracle(dataSet: Tuple8[String,String,Int,String,Int,Int,String,String]):Unit={
    initOracleDialect
    val conn = initOracleConn

    val recId = dataSet._1
    val sumTable = dataSet._2
    val recVer = dataSet._3
    val unitCode = dataSet._4
    val rowIndex = dataSet._5
    val colIndex = dataSet._6
    val sumValue = dataSet._7.toLong
    val institutionGUID = dataSet._8
    val sumTableName = "s_" + sumTable

    if(rowIndex == -1 || colIndex == -1){
      println("[SNP_ERROR]Cell index is wrong (rowIndex:" + rowIndex + ", colIndex:" + colIndex + ")")
    }

    val ps = conn.prepareStatement("insert into  " + sumTableName + "(RECID,RECVER,UNIT_CODE,GRID_NAME,ROW_INDEX,COL_INDEX,SUM_VALUE) values(utl_raw.cast_to_raw(?),?,?,?,?,?,?)")
    try{
      ps.setString(1, recId)
      ps.setInt(2, recVer)
      ps.setString(3, unitCode)
      ps.setString(4, sumTable)
      ps.setInt(5, rowIndex)
      ps.setInt(6, colIndex)
      ps.setLong(7, sumValue)

      ps.executeUpdate()
    }catch{
      case e:Exception => e.printStackTrace()
    }finally {
      ps.close()
      conn.close()
    }
  }

  /**
    * 进度跟踪存入进度表
    * @param currentFormulaCount
    * @param formulaCount
    * @param taskID
    */
  def process2Oracle(currentFormulaCount:Int, formulaCount:Int, taskID:String, taskRecid:String, status:Int):Unit = {
    initOracleDialect
    val conn = initOracleConn
    var processRate = currentFormulaCount * 100 / formulaCount  + "%"
    if(status == Const.PROCESS_STATUS_NORMAL){
      var updateSQL = ""
      val today = new java.util.Date()
      if(processRate != "100%"){
        updateSQL = "update " +
                    Const.PROCESS_TABLE +
                    " set PROCESSRATE=? " +
                    " where TASKID=? and RECID=?"
        println("[NOT 100%]"+updateSQL+","+processRate+","+taskID+","+taskRecid)
      }else if(processRate == "100%"){
        updateSQL = "update " +
                    Const.PROCESS_TABLE +
                    " set PROCESSRATE=?, STATUS=3, ENDTIME=?" +
                    " where TASKID=? and RECID=?"
        println("[IS 100%]"+updateSQL+","+processRate+","+taskID+","+taskRecid)
      }
      val ps = conn.prepareStatement(updateSQL)
      try{
        ps.setString(1, processRate)
        ps.setTimestamp(2, new java.sql.Timestamp(today.getTime()))
        ps.setString(3, taskID)
        ps.setString(4, taskRecid)
        ps.executeUpdate()
      }catch{
        case e:Exception => e.printStackTrace()
      }finally {
        ps.close()
        conn.close()
      }
    }else if(status == Const.PROCESS_STATUS_ERROR){
      val ps = conn.prepareStatement("update " +
                                      Const.PROCESS_TABLE +
                                      " set PROCESSRATE=?, STATUS=5" +
                                      " where TASKID=? and RECID=?")
      println("[NOT 100%]"+processRate+","+taskID+","+taskRecid)
      try{
        ps.setString(1, processRate)
        ps.setString(2, taskID)
        ps.setString(3, taskRecid)
        ps.executeUpdate()
      }catch{
        case e:Exception => e.printStackTrace()
      }finally {
        ps.close()
        conn.close()
      }
    }
  }

}
