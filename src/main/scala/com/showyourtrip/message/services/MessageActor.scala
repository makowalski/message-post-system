package com.showyourtrip.message.services

import akka.actor.{Actor, ActorRef}
import com.showyourtrip.message.models.Message

class MessageActor(eventActor: ActorRef, storeActor: ActorRef) extends Actor {

  def receive = {
    case m: Message => {
      storeActor ! m
      eventActor ! m
    }
  }
}
