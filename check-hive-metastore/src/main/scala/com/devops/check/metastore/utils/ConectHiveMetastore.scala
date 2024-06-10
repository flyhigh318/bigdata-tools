package com.devops.check.metastore.utils
import org.apache.spark.sql.SparkSession

object ConectHiveMetastore {

  def getSparkSession(thrift: String, app: String): SparkSession = {
      val spark = SparkSession
        .builder()
        .appName(app)
        .config("hive.metastore.uris", thrift)
        .enableHiveSupport()
        .getOrCreate()
      return spark
  }

  def excuteSql(spark: SparkSession, sql: String): Unit = {
    val result = spark.sql(sql)
    spark.stop()
  }
}
