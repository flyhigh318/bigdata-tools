package com.devops.utils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration


object HdfsUtil {
  def getFileSystem(hdfsNsUrl: String): FileSystem = {
    val conf = new Configuration()
    //conf.set("fs.defaultFS", "hdfs://saasProduction")
    conf.set("fs.defaultFS", hdfsNsUrl)
    FileSystem.get(conf)
  }
}
