package com.showyourtrip.message.services

import akka.actor.Actor
import com.showyourtrip.message.models.Message


class StoreActor extends Actor {

  def receive = {
    case m: Message => {
      // TODO
    }
  }
}
