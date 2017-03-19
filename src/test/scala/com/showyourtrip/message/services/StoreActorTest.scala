package com.showyourtrip.message.services

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import com.showyourtrip.message.models.Message
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}


class StoreActorTest extends TestKit(ActorSystem("test-system"))
  with FunSuiteLike
  with MockFactory
  with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  test("should invoke insertFunction when message arrives") {

    val insertFunctionMock = mockFunction[Message, Unit]
    val storeActor = system.actorOf(Props(classOf[StoreActor], insertFunctionMock))
    val message = Message("sender", "receiver", "test message", "simple date")
    insertFunctionMock.expects(message)

    storeActor ! message
  }
}
