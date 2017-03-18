package com.showyourtrip.message.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.showyourtrip.message.models.Message
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class MessageRoute(messageActor: ActorRef)(implicit executionContext: ExecutionContext) extends CirceSupport {

  val route = pathPrefix("message") {
    post {
      entity(as[Message]) { data =>

        messageActor ! data
        complete(StatusCodes.Created, data.asJson)
      }
    }
  }
}
