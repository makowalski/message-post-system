package com.showyourtrip.message.services

import akka.actor.Actor
import com.showyourtrip.message.models.Message

import scala.util.{Failure, Success}

class StoreActor(val insertMessage: (Message) => Unit) extends Actor {

  def receive = {
    case m: Message => {
      insertMessage(m)
    }
  }
}
