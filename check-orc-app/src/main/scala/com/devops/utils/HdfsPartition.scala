package com.devops.utils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration

class HdfsPartition(hdfsNsUrl: String)  {
  
  val fs = HdfsUtil.getFileSystem(hdfsNsUrl)

  def getFileList(dir: String): List[String] = {
    try {
      val path = new Path(dir)
      val fileStatusList = fs.listStatus(path)
      val fileList = fileStatusList.flatMap { fileStatus =>
        if (fileStatus.isDirectory) {
          getFileList(fileStatus.getPath.toString)
        } else {
          List(fileStatus.getPath.toString)
        }
      }.toList
      fileList
    } 
    catch {
      case  e: Exception => 
      //println("Exception: " + e.getMessage)
      e.printStackTrace()
      val fileList: List[String] = List()
      fileList
    }
  }

  def getFileList(dir: List[String]): List[String] = { 
    val bigFileList = dir.flatMap(filePath => getFileList(filePath))
    bigFileList
  }

  def moveHdfsFile(filePath: String): Unit = {
    val sourceFile = new Path(filePath)
    val dir = sourceFile.getParent.toString
    val fileName = sourceFile.getName
    val uri = new java.net.URI(dir)
    val destDir = new Path("/tmp/invalidOrc" + uri.getPath)
    val destFile = new Path(destDir, fileName)

    if(!fs.exists(sourceFile)){
      println("Source file does not exist")
    } 
    if(!fs.exists(destDir)){
      fs.mkdirs(destDir)
    }
    fs.rename(sourceFile, destFile)
  }
}