package com.devops.utils
import org.apache.spark.sql.SparkSession

object SparkUtil {
  def getSparkSession: SparkSession = {
    SparkSession.builder()
      .enableHiveSupport()
      .getOrCreate()
  }
}