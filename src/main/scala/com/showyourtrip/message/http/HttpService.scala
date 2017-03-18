package com.showyourtrip.message.http

import akka.actor.ActorRef
import com.showyourtrip.message.http.routes.{MessageRoute, _}

import scala.concurrent.ExecutionContext

class HttpService(messageActor: ActorRef)(implicit executionContext: ExecutionContext) {
  val messageRoute = new MessageRoute(messageActor)
  val route = messageRoute.route
}
