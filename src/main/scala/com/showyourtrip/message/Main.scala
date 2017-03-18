package com.showyourtrip.message

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.showyourtrip.message.http.HttpService
import com.showyourtrip.message.models.Message
import com.showyourtrip.message.services.{EventHandlerActor, MessageActor, MessageEventBus, StoreActor}
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]) {
    implicit val actorSystem = ActorSystem("message-writing-system")
    implicit val actorMaterializer = ActorMaterializer()
    implicit val executor = actorSystem.dispatcher

    val eventHandlerActor = actorSystem.actorOf(Props(classOf[EventHandlerActor], new MessageEventBus), "eventHandlerActor")
    val storeActor = actorSystem.actorOf(Props(classOf[StoreActor], Message.insertFunction), "storeActor")
    val messageActor = actorSystem.actorOf(Props(classOf[MessageActor], eventHandlerActor, storeActor), "messageActor")

    val host = ConfigFactory.load().getString("message.push.service.hostname")
    val port = ConfigFactory.load().getInt("message.push.service.port")

    Http().bindAndHandle(new HttpService(messageActor).route, host, port)

    println("server started at 8080")
  }

}
