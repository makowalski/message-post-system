package com.showyourtrip.message.services

import akka.actor.{Actor, ActorRef, Terminated}
import akka.event.{ActorEventBus, LookupClassification}
import com.showyourtrip.message.models.Message

case class MessageEvent(topic: String, message: Message)

case class Subscribe(topic: String, subscriber: ActorRef)

case class Subscribed(topic: String)

class EventHandlerActor(val messageEventBus: MessageEventBus) extends Actor {

  def receive = {
    case m: Message => {
      messageEventBus.publish(MessageEvent("system", m))
    }
    case Subscribe(topic, subscriber) => {
      messageEventBus.subscribe(subscriber, topic)

      context.watch(subscriber)

      subscriber ! Subscribed(topic)
    }
    case Terminated(actor) => {
      messageEventBus.unsubscribe(actor)
    }
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    // TODO handle restart
  }
}

class MessageEventBus extends ActorEventBus with LookupClassification {
  type Event = MessageEvent
  type Classifier = String

  def mapSize(): Int = {
    16
  }

  protected def classify(event: Event): Classifier = {
    event.topic
  }

  protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.message
  }
}
