package com.devops.utils

class HdfsOrcPath(path: String)  {
  
  def getInputList(): List[String] = {
    // var emptyInput: List[String] = List()
    val input = path
    val containsColon = input.contains(":")
    if (containsColon) {
      val parts = input.split("/")
      val path = parts.slice(0, parts.length-1).mkString("/") + "/"
      val range = parts.last.split(":").map(_.toInt)
      val result = (range(0) to range(1)).map(path + _).toList
      return result
    }
    else {
      val result = input.split(",").toList
      return result
    }
   // return emptyInput
  }
}
