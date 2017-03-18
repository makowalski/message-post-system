package com.showyourtrip.message.services

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import com.showyourtrip.message.models.Message
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class EventHandlerActorTest extends TestKit(ActorSystem("test-system"))
  with FunSuiteLike
  with MockitoSugar
  with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  test("should subscribe ActorRef when Subscribe message arrive") {

    val eventBusMock = mock[MessageEventBus]
    val eventHandlerActor = system.actorOf(Props(classOf[EventHandlerActor], eventBusMock))
    val subscription = Subscribe("system", testActor)
    Mockito.when(eventBusMock.subscribe(testActor, "system")).thenReturn(true)

    eventHandlerActor ! subscription

    expectMsg(Subscribed("system"))
    awaitAssert(Mockito.verify(eventBusMock).subscribe(testActor, "system"))
  }

  test("should restart when Subscribe message processing causes exception") {

    val eventBusMock = mock[MessageEventBus]
    val eventHandlerActor = system.actorOf(Props(classOf[EventHandlerActor], eventBusMock))
    val subscription = Subscribe("system", testActor)

    Mockito.when(eventBusMock.subscribe(testActor, "system")).thenThrow(new RuntimeException).thenReturn(true)
    eventHandlerActor ! subscription
    eventHandlerActor ! subscription
    eventHandlerActor ! subscription

    expectMsg(Subscribed("system"))
    awaitAssert(Mockito.verify(eventBusMock, Mockito.times(3)).subscribe(testActor, "system"))
  }

  test("should publish message when Message message arrive") {

    val eventBusMock = mock[MessageEventBus]
    val eventHandlerActor = system.actorOf(Props(classOf[EventHandlerActor], eventBusMock))
    val message = Message("sender", "receiver", "text", "date")

    eventHandlerActor ! message

    awaitAssert(Mockito.verify(eventBusMock).publish(any[MessageEvent]()))
  }

  test("should restart when Message message processing causes exception") {

    val eventBusMock = mock[MessageEventBus]
    val eventHandlerActor = system.actorOf(Props(classOf[EventHandlerActor], eventBusMock))
    val message = Message("sender", "receiver", "text", "date")

    Mockito.doThrow(new RuntimeException).doNothing().when(eventBusMock).publish(any[MessageEvent])
    eventHandlerActor ! message
    eventHandlerActor ! message
    eventHandlerActor ! message

    awaitAssert(Mockito.verify(eventBusMock, Mockito.times(3)).publish(any[MessageEvent]))
  }
}
