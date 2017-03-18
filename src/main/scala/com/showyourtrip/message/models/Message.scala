package com.showyourtrip.message.models

import com.showyourtrip.message.utils.MongoSupport
import reactivemongo.bson.{BSONDocumentWriter, Macros}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure

object Message extends MongoSupport {

  implicit def messageWriter: BSONDocumentWriter[Message] = Macros.writer[Message]

  val insertFunction = (message: Message) =>
    collection("messages")
      .flatMap(_.insert(message))
      .onComplete(_ match {
        case Success(w) => // TODO handle success log?
        case Failure(e) => // TODO handle error
      })
}

case class Message(senderId: String, receiverId: String, text: String, date: String)
