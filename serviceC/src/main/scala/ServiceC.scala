import java.util.Date

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object ServiceC {
  private val Database = "alonsodb"
  private val Collection = "robots"
  private val MongoHost = "localhost"
  private val MongoPort = 27017
  private val MongoProvider = "com.stratio.datasource.mongodb"

  private def prepareMongoEnvironment(): MongoClient = {
    val mongoClient = MongoClient(MongoHost, MongoPort)
    mongoClient
  }

  def main(args: Array[String]) {

    val sparkConf = new SparkConf()
      .setAppName("KafkaConnector")
      .setMaster("local[1]")
      .set("spark.driver.allowMultipleContexts", "true")

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val topicsSet = Set("robots")
    val kafkaParams =
      Map[String, String]("metadata.broker.list" -> "localhost:9092")
    val messages = KafkaUtils
      .createDirectStream[String, String, StringDecoder, StringDecoder](
        ssc,
        kafkaParams,
        topicsSet)

    val mongoClient = prepareMongoEnvironment()
    val collection = mongoClient(Database)(Collection)

    val sc = new SparkContext(sparkConf)
    try {
      val sqlContext = new SQLContext(sc)
      sqlContext.sql(s"""|CREATE TEMPORARY TABLE $Collection
            |(id STRING, tweets STRING)
            |USING $MongoProvider
            |OPTIONS (
            |host '$MongoHost:$MongoPort',
            |database '$Database',
            |collection '$Collection'
            |)
            """.stripMargin.replaceAll("\n", " "))

      messages.foreachRDD(rdd => {
        val count = rdd.count()
        if (count > 0) {
          val topList = rdd.take(count.toInt)
          println(
            "\nReading data from kafka broker... (%s total):".format(
              rdd.count()))
          topList.foreach(println)

          for (tweet <- topList) {
            collection.insert {
              MongoDBObject("id" -> new Date(), "tweets" -> tweet)
            }
          }

        }
      })

      val robotsDF = sqlContext.read
        .format("com.stratio.datasource.mongodb")
        .table(s"$Collection")
      robotsDF.show(5)
    } finally {
      println("finished withSQLContext...")
    }

    ssc.start()
    ssc.awaitTermination()

    println("Finished!")
  }
}

