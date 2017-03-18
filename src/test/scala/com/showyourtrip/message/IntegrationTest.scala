package com.showyourtrip.message

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import com.showyourtrip.message.models.Message
import com.showyourtrip.message.services._
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.stream.ActorMaterializer
import com.showyourtrip.message.http.HttpService

class IntegrationTest extends TestKit(ActorSystem("test-system"))
  with FunSuiteLike
  with ImplicitSender
  with BeforeAndAfterAll {

  implicit val actorMaterializer = ActorMaterializer()
  implicit val executor = system.dispatcher

  override def beforeAll {
    val eventHandlerActor = system.actorOf(Props(classOf[EventHandlerActor], new MessageEventBus), "eventHandlerActor")
    val storeActor = system.actorOf(Props(classOf[StoreActor], (message: Message) => {}), "storeActor")
    val messageActor = system.actorOf(Props(classOf[MessageActor], eventHandlerActor, storeActor), "messageActor")

    Http().bindAndHandle(new HttpService(messageActor).route, "localhost", 9092)
  }

  override def afterAll {
    shutdown()
  }

  test("subscribe and send message") {
    val selectionSubscription = system
      .actorSelection("akka.tcp://test-system@127.0.0.1:3652/user/eventHandlerActor")

    selectionSubscription ! Subscribe("system", testActor)
    expectMsg(Subscribed("system"))

    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "http://localhost:9092/message",
        headers = List[HttpHeader](),
        entity = HttpEntity(
          `application/json`,
          """
            {
              "senderId": "sender",
              "receiverId": "receiver",
              "text": "simple text",
              "date": "today"
            }
          """.stripMargin
        )))

    expectMsg(Message("sender", "receiver", "simple text", "today"))
  }

}