package com.showyourtrip.message

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.showyourtrip.message.http.HttpService
import com.showyourtrip.message.services.{EventHandlerActor, MessageActor, MessageEventBus, StoreActor}

object Main {

  def main(args: Array[String]) {
    implicit val actorSystem = ActorSystem("message-writing-system")
    implicit val actorMaterializer = ActorMaterializer()
    implicit val executor = actorSystem.dispatcher

    val eventHandlerActor = actorSystem.actorOf(Props(classOf[EventHandlerActor], new MessageEventBus), "eventHandlerActor")
    val storeActor = actorSystem.actorOf(Props[StoreActor], "storeActor")
    val messageActor = actorSystem.actorOf(Props(classOf[MessageActor], eventHandlerActor, storeActor), "messageActor")

    Http().bindAndHandle(new HttpService(messageActor).route, "localhost", 9091)

    println("server started at 8080")
  }

}
