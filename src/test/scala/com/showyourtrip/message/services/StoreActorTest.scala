package com.showyourtrip.message.services

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import com.showyourtrip.message.models.Message
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}


class StoreActorTest extends TestKit(ActorSystem("test-system"))
  with FunSuiteLike
  with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  test("should invoke insertFunction when message arrives") {

    val storeActor = system.actorOf(Props(classOf[StoreActor], (message: Message) => {}))
    val message = Message("sender", "receiver", "test message", "simple date")

    storeActor ! message

    // TODO validate insertFunction

  }
}
