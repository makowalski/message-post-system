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
import com.typesafe.config.ConfigFactory

class IntegrationTest extends TestKit(ActorSystem("test-system"))
  with FunSuiteLike
  with ImplicitSender
  with BeforeAndAfterAll {

  implicit val actorMaterializer = ActorMaterializer()
  implicit val executor = system.dispatcher

  val host = ConfigFactory.load().getString("message.push.service.hostname")
  val port = ConfigFactory.load().getInt("message.push.service.port")

  override def beforeAll {
    val eventHandlerActor = system.actorOf(Props(classOf[EventHandlerActor], new MessageEventBus), "event-handler")
    val storeActor = system.actorOf(Props(classOf[StoreActor], (message: Message) => {}), "store")
    val messageActor = system.actorOf(Props(classOf[MessageActor], eventHandlerActor, storeActor), "message")

    Http().bindAndHandle(new HttpService(messageActor).route, host, port)
  }

  override def afterAll {
    shutdown()
  }

  test("subscribe and post message") {
    val selectionSubscription = system
      .actorSelection("akka.tcp://test-system@127.0.0.1:3652/user/event-handler")

    selectionSubscription ! Subscribe("system", testActor)
    expectMsg(Subscribed("system"))

    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = s"http://$host:$port/message",
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