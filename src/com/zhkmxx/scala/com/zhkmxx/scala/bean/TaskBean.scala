package com.zhkmxx.scala.com.zhkmxx.scala.bean

import scala.beans.BeanProperty

/**
  * Created by zhao on 2017/2/14.
  */
class TaskBean {
  @BeanProperty
  var taskID = null //RECID

  @BeanProperty
  var Recver = 0 //RECVER

  @BeanProperty
  var taskTitle = null //T_TITLE

  @BeanProperty
  var taskName = null //T_NAME(null), TASKNAME

  @BeanProperty
  var taskIsCLD = 0 //T_ISCLD

  @BeanProperty
  var orgUnit = null //ORGUNIT

  @BeanProperty
  var taskTables = null //T_TABLES

  @BeanProperty
  var taskFilter = null //T_FILTER

  @BeanProperty
  var taskUser = null //T_USER

  @BeanProperty
  var sumMode = 0 //T_SUMMODE

  @BeanProperty
  var taskInstitution = null //T_INSTITUTION

  @BeanProperty
  var reportName = null //REPORTNAME

  @BeanProperty
  var creatorId = null //CREATORID

  @BeanProperty
  var creatorName = null //CREATORNAME

  @BeanProperty
  var creatorUnit = null //CREATORUNIT

  @BeanProperty
  var taskCount = 0 //非数据库使用， 数据引擎计算进度使用。



}
