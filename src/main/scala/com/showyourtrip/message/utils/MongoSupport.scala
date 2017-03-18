package com.showyourtrip.message.utils

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MongoSupport {
  val mongoUri = "mongodb://localhost:27017/showyourtrip"

  val driver = MongoDriver()
  val parsedUri = MongoConnection.parseURI(mongoUri)
  val connection = parsedUri.map(driver.connection(_))
  val futureConnection = Future.fromTry(connection)
}

trait MongoSupport {

  def db: Future[DefaultDB] = MongoSupport.futureConnection.flatMap(_.database("showyourtrip"))

  def collection(name: String) = db.map(_.collection[BSONCollection](name))
}
