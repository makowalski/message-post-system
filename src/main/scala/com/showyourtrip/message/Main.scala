package com.showyourtrip.message

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.showyourtrip.message.http.HttpService
import com.showyourtrip.message.models.Message
import com.showyourtrip.message.services.{EventHandlerActor, MessageActor, MessageEventBus, StoreActor}
import com.typesafe.config.ConfigFactory
import kamon.Kamon

object Main extends App {
  Kamon.start()

  implicit val actorSystem = ActorSystem("message-post-system")
  implicit val actorMaterializer = ActorMaterializer()
  implicit val executor = actorSystem.dispatcher

  val eventHandlerActor = actorSystem.actorOf(Props(classOf[EventHandlerActor], new MessageEventBus), "event-handler")
  val storeActor = actorSystem.actorOf(Props(classOf[StoreActor], Message.insertFunction), "store")
  val messageActor = actorSystem.actorOf(Props(classOf[MessageActor], eventHandlerActor, storeActor), "message")

  val host = ConfigFactory.load().getString("message.push.service.hostname")
  val port = ConfigFactory.load().getInt("message.push.service.port")

  Http().bindAndHandle(new HttpService(messageActor).route, host, port)

  println(s"server started at $port")
}
