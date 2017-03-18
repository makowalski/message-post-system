package com.showyourtrip.message.services

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import com.showyourtrip.message.models.Message
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}


class MessageActorTest extends TestKit(ActorSystem("test-system"))
  with FunSuiteLike
  with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }


  test("should forward message to EventHandlerActor and StoreActor") {

    val messageActor = system.actorOf(Props(classOf[MessageActor], testActor, testActor))
    val message = Message("sender", "receiver", "test message", "simple date")

    messageActor ! message

    expectMsg(message)
    expectMsg(message)
  }
}
