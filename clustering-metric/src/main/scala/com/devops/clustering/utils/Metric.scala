package com.devops.clustering.utils
import org.apache.spark.sql.types.{StructType, StructField, StringType, DateType}

object Metric { 
  def getName(sql: String): String = {
    var name = ""
    sql match {
      case sql if sql.contains("example_crane_ads.tb_test_data") =>
        name = "clustering.example_crane_ads.tb_hourly_data"

      case sql if sql.contains("example_crane_ads.work_daily_count") =>
        name = "clustering.example_crane_ads.work_daily_count"

      case sql if sql.contains("example_crane_ads.mysql_cluster_tmp") =>
        name = "clustering.example_crane_ads.mysql_cluster_daily"

      case sql if sql.contains("example_crane_ads.mysql_cluster_detail_tmp") =>
        name = "clustering.example_crane_ads.mysql_cluster_detail_daily"

      case _ => 
        name = ""
    }
    return name
  }

  def getSchema(): StructType = {
    val schema = StructType(Array(
      StructField("metricDate", DateType, true),
      StructField("metricName", StringType, true),
      StructField("metricStatus", StringType, true),
      StructField("excuteSql", StringType, true)
    ))
    return schema
  }
}  