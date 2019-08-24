package ktest

import org.apache.spark.sql.SparkSession

object queryEvents {

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .master("spark://10.139.64.4:7077")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    println(spark.sparkContext.getConf.getAll)

    println(spark.sql("SELECT COUNT(*) FROM events"))

  }
}

